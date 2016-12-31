package org.hallock.tfe.serve;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.game.evil.AddHighTile;
import org.hallock.tfe.cmn.game.evil.AddInMoreTiles;
import org.hallock.tfe.cmn.game.evil.AddRandomMove;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.RemovePoints;
import org.hallock.tfe.cmn.svr.EvilActionsAwarder;
import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.msg.gv.StateChanged;

public class Game
{
	ArrayList<InGamePlayer> connections = new ArrayList<>();
	
	GameOptions options;
	private EvilActionsAwarder awarder;

	private GameServer server;
	private Statistics statistics;

	public Game(GameServer server, GameOptions options)
	{
		this.server = server;
		this.options = options;
		this.awarder = options.createAwarder();
		statistics = new Statistics();
	}
	
	synchronized void add(InGamePlayer computerPlayer) throws IOException
	{
		computerPlayer.points = options.createPoints();
		computerPlayer.setPlayerNumber(this, connections.size());
		connections.add(computerPlayer);
		computerPlayer.board = new TileBoard(options.numRows, options.numCols);
		computerPlayer.board.initialize(options);
		computerPlayer.history = new History();
	}

	private synchronized void broadCastPlayerToEveryOne(int player) throws IOException
	{
		GameUpdateInfo info = new GameUpdateInfo();
		info.addPlayer(connections.get(player));
		StateChanged stateChanged = new StateChanged(info);
		
		for (InGamePlayer c : connections)
		{
			c.send(stateChanged);
		}
	}
	
	public void start() throws IOException
	{
		GameUpdateInfo info = getGameState();
		
		for (InGamePlayer player : connections)
		{
			player.gameStarted(info);
		}
	}

	private GameUpdateInfo getGameState()
	{
		GameUpdateInfo info = new GameUpdateInfo();
		
		for (InGamePlayer player : connections)
			info.addPlayer(player);
		
		return info;
	}

	public synchronized void play(int player, PossiblePlayerActions swipe, boolean award) throws IOException
	{
		boolean changed = false;
		TileBoard state = connections.get(player).board;
		History history = connections.get(player).history;
		switch (swipe)
		{
		case Up:
		{
			TileChanges changes = new TileChanges();
			state.up(changes);
			if (!changes.changed())
				break;
			state.fillTurn(options, changes);
			connections.get(player).turnId++;
			connections.get(player).changes = changes;
			connections.get(player).setDone(!state.hasMoreMoves());
			connections.get(player).addPoints(changes);
			history.updated(connections.get(player));
//			statistics.update(player, state);
			changed = true;
			break;
		}
		case Down:
		{
			TileChanges changes = new TileChanges();
			state.down(changes);
			if (!changes.changed())
				break;
			state.fillTurn(options, changes);
			connections.get(player).turnId++;
			connections.get(player).changes = changes;
			connections.get(player).setDone(!state.hasMoreMoves());
			connections.get(player).addPoints(changes);
			history.updated(connections.get(player));
//			statistics.update(player, state);
			changed = true;
			break;
		}
		case Left:
		{
			TileChanges changes = new TileChanges();
			state.left(changes);
			if (!changes.changed())
				break;
			state.fillTurn(options, changes);
			connections.get(player).turnId++;
			connections.get(player).changes = changes;
			connections.get(player).setDone(!state.hasMoreMoves());
			connections.get(player).addPoints(changes);
			history.updated(connections.get(player));
//			statistics.update(player, state);
			changed = true;
			break;
		}
		case Right:
		{
			TileChanges changes = new TileChanges();
			state.right(changes);
			if (!changes.changed())
				break;
			state.fillTurn(options, changes);
			connections.get(player).turnId++;
			connections.get(player).changes = changes;
			connections.get(player).setDone(!state.hasMoreMoves());
			connections.get(player).addPoints(changes);
			history.updated(connections.get(player));
//			statistics.update(player, state);
			changed = true;
			break;
		}
		case Redo:
		{
			if (!history.redo(connections.get(player)))
				break;
			connections.get(player).turnId++;
			connections.get(player).changes = null;
			connections.get(player).setDone(!state.hasMoreMoves());
			changed = true;
			break;
		}
		case Undo:
		{
			if (!history.undo(connections.get(player)))
				break;
			connections.get(player).turnId++;
			connections.get(player).changes = null;
			connections.get(player).setDone(!state.hasMoreMoves());
			changed = true;
			break;
		}
		case Quit:
			connections.get(player).setDone(true);
			break;
			
		case ShowAllTileBoards:
			connections.get(player).send(new StateChanged(getGameState()));
			break;
			
		default:
			System.out.println("Unknown action: " + swipe);
		}

		if (changed && award)
		{
			award(connections.get(player));
		}
		
		if (changed)
		{
			broadCastPlayerToEveryOne(player);
		}
		
		if (allAreDone())
		{
			quit();
		}
		
	}
	
	public int getHighestTile()
	{
		int highest = Integer.MIN_VALUE;
		for (InGamePlayer player : connections)
		{
			highest = Math.max(highest, player.board.getHighestTile());
		}
		return highest;
	}


	private boolean award(InGamePlayer inGamePlayer) throws IOException
	{
		EvilAction awardEvilAction = awarder.awardEvilAction(inGamePlayer);
		if (awardEvilAction == null)
			return false;
		
		// This message is not really necessary...
		inGamePlayer.award(awardEvilAction);
		
		return true;
	}


	public void playAction(int player, EvilAction action, int player2) throws IOException
	{
		InGamePlayer sender    = connections.get(player);
		InGamePlayer recipient = connections.get(player2);
		
		boolean found = false;
		for (EvilAction a : sender.availableActions)
		{
			if (a.equals(action))
				continue;
			found = true;
			sender.availableActions.remove(a);
			break;
		}
		if (!found)
		{
			return;
		}
		
		switch (action.getType())
		{
		case AddHighTile:
		{
			TileChanges changes = new TileChanges();
			TileBoard tileBoard = recipient.getTileBoard();
			tileBoard.addNoChanges(changes);
			tileBoard.addTile(((AddHighTile) action).getHighTile(), changes);
			if (!changes.changed())
				break;
			recipient.turnId++;
			recipient.changes = changes;
			recipient.setDone(!tileBoard.hasMoreMoves());
			if (options.evilActionsRemoveHistory)
				recipient.history.clear();
			broadCastPlayerToEveryOne(player2);
			break;
		}
		case BlockCell:
		{
			TileChanges changes = new TileChanges();
			TileBoard tileBoard = recipient.getTileBoard();
			tileBoard.addNoChanges(changes);
			tileBoard.addTile(TileBoard.BLOCKED, changes);
			if (!changes.changed())
				break;
			recipient.turnId++;
			recipient.changes = changes;
			recipient.setDone(!tileBoard.hasMoreMoves());
			if (options.evilActionsRemoveHistory)
				recipient.history.clear();
			broadCastPlayerToEveryOne(player2);
			break;
		}
		case AddInMoreTiles:
		{
			TileChanges changes = new TileChanges();
			TileBoard tileBoard = recipient.getTileBoard();
			tileBoard.addNoChanges(changes);
			DiscreteDistribution dist = ((AddInMoreTiles)action).getDistribution();
			int number = ((AddInMoreTiles)action).getNumberOfNewTiles();
			tileBoard.randomlyFill(number, dist, changes);
			if (!changes.changed())
				break;
			recipient.turnId++;
			recipient.changes = changes;
			recipient.setDone(!tileBoard.hasMoreMoves());
			if (options.evilActionsRemoveHistory)
				recipient.history.clear();
			broadCastPlayerToEveryOne(player2);
			break;
		}
		case AddRandomMove:
		{
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					long wait = ((AddRandomMove) action).getWaitTime();
					int number = ((AddRandomMove) action).getNumberOfMoves();
					for (int i = 0; i < number; i++)
					{
						PossiblePlayerActions randomMove = ((AddRandomMove) action).getMove(i);
						try
						{
							play(recipient.playerNumber, randomMove, false);
							if (options.evilActionsRemoveHistory)
								recipient.history.clear();
							Thread.sleep(wait);
						}
						catch (Throwable t)
						{
							t.printStackTrace();
						}
					}
				}}).start();
			break;
		}
		case RemovePoints:
		{
			BigDecimal numToRemove = ((RemovePoints) action).getNumToRemove();
			recipient.points.subtract(numToRemove);
			broadCastPlayerToEveryOne(player2);
			if (options.evilActionsRemoveHistory)
				recipient.history.clear();
			break;
		}
		case DeflectEvilAction:
		case DelayMoves:
		case DepriveEvilActions:
		case Distract:
		case HideNumberValues:
		case RemoveSwipeAction:
		case NoUndoRedo:
			action.appliedTime = System.currentTimeMillis();
			recipient.appliedActions.add(action);
			break;
		default:
			throw new RuntimeException();
		}
	}

	private void quit()
	{
		for (InGamePlayer player : connections)
		{
			player.quit(server);
		}
		server.gameFinished(this);
	}


	private boolean allAreDone()
	{
		for (InGamePlayer player : connections)
			if (!player.stillWantsToPlay())
				return false;
		return true;
	}
}
