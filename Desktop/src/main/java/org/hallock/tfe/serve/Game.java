package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.ArrayList;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.msg.gv.StateChanged;

public class Game
{
	ArrayList<InGamePlayer> connections = new ArrayList<>();
	
	GameOptions options;
	
	public Game(GameOptions options)
	{
		this.options = options;
	}

	synchronized int add(PlayerConnection player) throws IOException
	{
		HumanPlayer humanPlayer = new HumanPlayer(player);
		humanPlayer.playerNumber = connections.size();
		player.playerNumber = humanPlayer.playerNumber;
		connections.add(humanPlayer);
		humanPlayer.board = new TileBoard(options.numRows, options.numCols);
		humanPlayer.board.initialize(options);
		humanPlayer.history = new History();
		return humanPlayer.playerNumber;
	}

	synchronized int add(ComputerAI player) throws IOException
	{
		InGameComputerPlayer computerPlayer = new InGameComputerPlayer(player);
		computerPlayer.playerNumber = connections.size();
		computerPlayer.ai.number = computerPlayer.playerNumber;
		connections.add(computerPlayer);
		computerPlayer.board = new TileBoard(options.numRows, options.numCols);
		computerPlayer.board.initialize(options);
		computerPlayer.history = new History();
		return computerPlayer.playerNumber;
	}
	
	public synchronized void broadCast() throws IOException
	{
		for (InGamePlayer c : connections)
		{
			broadCast(c);
		}
	}

	private synchronized void broadCast(InGamePlayer player) throws IOException
	{
		for (InGamePlayer c : connections)
		{
			player.sendWithoutFlushing(new StateChanged(c));
		}
		player.flush();
	}

	public void play(int player, PossiblePlayerActions swipe) throws IOException
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
			history.updated(state, "");
			connections.get(player).turnId++;
			connections.get(player).changes = changes;
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
			history.updated(state, "");
			connections.get(player).turnId++;
			connections.get(player).changes = changes;
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
			history.updated(state, "");
			connections.get(player).turnId++;
			connections.get(player).changes = changes;
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
			history.updated(state, "");
			connections.get(player).turnId++;
			connections.get(player).changes = changes;
			changed = true;
			break;
		}
		case Redo:
		{
			TileBoard newState = history.redo();
			if (newState == null)
				break;
			connections.get(player).board = newState;
			connections.get(player).turnId++;
			connections.get(player).changes = null;
			changed = true;
			break;
		}
		case Undo:
		{
			TileBoard newState = history.redo();
			if (newState == null)
				break;
			connections.get(player).board = newState;
			connections.get(player).turnId++;
			connections.get(player).changes = null;
			changed = true;
			break;
		}
		case Quit:
//			connections.get(player).quit();
			break;
			
		case ShowAllTileBoards:
			broadCast(connections.get(player));
			break;
			
		default:
			System.out.println("Unknown action: " + swipe);
		}
		
		if (changed)
		{
			broadCast();
		}
	}
	


	public void launchComputerPlayers()
	{
		for (InGamePlayer player : connections)
		{
			player.startAI();
		}
	}
	
	public static abstract class InGamePlayer
	{
		int playerNumber;
		int turnId;
		public TileBoard board;
		public TileChanges changes;
		public History history;

		public abstract void sendWithoutFlushing(StateChanged stateChanged) throws IOException;
		public abstract void startAI();
		public abstract void flush() throws IOException;

		public int getPlayerNumber()
		{
			return playerNumber;
		}

		public TileBoard getBoard()
		{
			return board;
		}

		public TileChanges getChanges()
		{
			return changes;
		}

		public int getTurnId()
		{
			return turnId;
		}
	}
	
	private static class InGameComputerPlayer extends InGamePlayer
	{
		ComputerAI ai;

		public InGameComputerPlayer(ComputerAI player2)
		{
			this.ai = player2;
		}

		@Override
		public void sendWithoutFlushing(StateChanged stateChanged) {}
		@Override
		public void flush() {}

		@Override
		public void startAI()
		{
			ai.start();
		}
		
		@Override
		public String toString()
		{
			return "ai";
		}
	}
	
	private static class HumanPlayer extends InGamePlayer
	{
		PlayerConnection connections;

		public HumanPlayer(PlayerConnection player)
		{
			this.connections = player;
		}

		@Override
		public void flush() throws IOException
		{
			connections.connection.flush();
		}

		@Override
		public void sendWithoutFlushing(StateChanged stateChanged) throws IOException
		{
			connections.connection.sendMessageWithoutFlushing(stateChanged);
		}

		@Override
		public void startAI()
		{
			// TODO Auto-generated method stub
			
		}
	}
}
