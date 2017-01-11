package org.hallock.tfe.serve;

import java.io.IOException;
import java.math.BigDecimal;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.InGamePlayer.PlayingStatus;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.game.evil.AddHighTile;
import org.hallock.tfe.cmn.game.evil.AddInMoreTiles;
import org.hallock.tfe.cmn.game.evil.AddRandomMove;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.RemovePoints;
import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.msg.gv.StateChangedSender;
import org.hallock.tfe.serve.ServerGamePlayer.LeftPlayer;
import org.hallock.tfe.serve.ai.ComputerAI;
import org.hallock.tfe.sys.GameUpdateInfo;

public class Game extends GameThing<ServerGamePlayer>
{
	private GameServer server;
	private ComputerAI aiRunner;

	private EvilActionsAwarder awarder;

	public Game(GameServer server, GameOptions options)
	{
		super(options);
		this.server = server;
		awarder = options.createAwarder();
	}
	


	@Override
	protected boolean award(ServerGamePlayer inGamePlayer) throws IOException
	{
		EvilAction awardEvilAction = awarder.awardEvilAction(inGamePlayer);
		if (awardEvilAction == null)
			return false;
		
		// This message is not really necessary...
		inGamePlayer.award(awardEvilAction);
		
		return true;
	}

	@Override
	protected void quit()
	{
		server.gameFinished(this);
		for (ServerGamePlayer player : players)
		{
			if (!player.getStatus().equals(PlayingStatus.Disconnected))
				disconnect(player.getPlayerNumber());
		}
	}


	@Override
	protected void playerChanged(int player)
	{
		players.get(player).changed();
		broadCastPlayerToEveryOne(player);
	}

	@Override
	protected void showAllPlayersTo(int player)
	{
		try
		{
			players.get(player).send(new StateChangedSender(getGameState()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	private synchronized void broadCastPlayerToEveryOne(int player)
	{
		GameUpdateInfo info = new GameUpdateInfo();
		info.addPlayer(players.get(player));
		StateChangedSender stateChanged = new StateChangedSender(info);
		
		for (ServerGamePlayer c : players)
		{
			try
			{
				c.send(stateChanged);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void start() throws IOException
	{
		GameUpdateInfo info = getGameState();
		
		for (ServerGamePlayer player : players)
		{
			player.gameStarted(info);
		}
		
		if (aiRunner != null)
		{
			aiRunner.start();
		}
	}

	@Override
	public void disconnect(int number)
	{
		ServerGamePlayer oldPlayer = players.get(number);
		oldPlayer.setPlayingStatus(PlayingStatus.Disconnected);
		try
		{
			oldPlayer.getWriter().close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		players.set(number, new LeftPlayer(oldPlayer.getName()));
	}

	public ComputerAI getAiManager()
	{
		if (aiRunner == null)
		{
			aiRunner = ComputerAI.createComputerAi(this, options.aiWait);
		}
		return aiRunner;
	}

	@Override
	public void playAction(int player, EvilAction action, int player2) throws IOException
	{
		if (player < 0 || player2 < 0)
			return;
		
		ServerGamePlayer sender    = players.get(player);
		ServerGamePlayer recipient = players.get(player2);
		
		if (!sender.getStatus().canPlay())
			return;
		if (!recipient.getStatus().canPlay())
			return;
		
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
			recipient.incrementDrawId();
			recipient.setChanges(changes);
			if (!tileBoard.hasMoreMoves())
				recipient.setPlayingStatus(PlayingStatus.NoMoreMoves);
			if (options.evilActionsRemoveHistory)
				recipient.getHistory().clear();
			playerChanged(player2);
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
			recipient.incrementDrawId();
			recipient.setChanges(changes);
			if (!tileBoard.hasMoreMoves())
				recipient.setPlayingStatus(PlayingStatus.NoMoreMoves);
			if (options.evilActionsRemoveHistory)
				recipient.getHistory().clear();
			playerChanged(player2);
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
			recipient.incrementDrawId();
			recipient.setChanges(changes);
			recipient.setChanges(changes);
			if (!tileBoard.hasMoreMoves())
				recipient.setPlayingStatus(PlayingStatus.NoMoreMoves);
			if (options.evilActionsRemoveHistory)
				recipient.getHistory().clear();
			playerChanged(player2);
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
							play(player2, randomMove, false);
							if (options.evilActionsRemoveHistory)
								recipient.getHistory().clear();
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
			recipient.getPoints().subtract(numToRemove);
			if (options.evilActionsRemoveHistory)
				recipient.getHistory().clear();
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
}
