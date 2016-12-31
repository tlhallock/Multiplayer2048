package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.PlayerState;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.gc.LaunchGame;
import org.hallock.tfe.msg.gv.AwardedEvilAction;
import org.hallock.tfe.serve.PlayerConnection.PlayerRole.GameRole;

public abstract class InGamePlayer implements PlayerState
{
	int playerNumber;
	int turnId;
	public TileBoard board;
	TileChanges changes;
	History history;
	boolean finished;
	public PointsCounter points;
	
	public LinkedList<EvilAction> availableActions = new LinkedList<>();
	public LinkedList<EvilAction> appliedActions = new LinkedList<>();

	public abstract void gameStarted(GameUpdateInfo info) throws IOException;
	public abstract void send(Message info) throws IOException;
	public abstract void setPlayerNumber(Game game, int size);
	public abstract void setDone(boolean hasMoreMoves);
	public abstract boolean stillWantsToPlay();
	public abstract void quit(GameServer server);
	public abstract String getName();
	public abstract void award(EvilAction awardEvilAction) throws IOException;

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
	
	public void addPoints(TileChanges newState)
	{
		newState.countPoints(points);
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public void setBoard(TileBoard board)
	{
		this.board = board;
	}

	@Override
	public TileBoard getTileBoard()
	{
		return this.board;
	}

	@Override
	public void setPoints(PointsCounter counter)
	{
		this.points = counter;
	}

	@Override
	public PointsCounter getPoints()
	{
		return points;
	}


	

	static class InGameComputerPlayer extends InGamePlayer
	{
		ComputerAI ai;

		public InGameComputerPlayer(ComputerAI player2)
		{
			this.ai = player2;
		}

		@Override
		public void gameStarted(GameUpdateInfo info)
		{
			ai.start();
		}

		@Override
		public void send(Message info) throws IOException
		{
			// don't need to do anything yet...
		}

		@Override
		public void setPlayerNumber(Game game, int size)
		{
			playerNumber = size;
			ai.number = size;
		}

		@Override
		public void setDone(boolean finished)
		{
			this.finished = finished;
			if (finished)
				ai.stop();
		}

		@Override
		public boolean stillWantsToPlay()
		{
			return false;
		}

		@Override
		public void quit(GameServer server)
		{
			setDone(false);
		}

		@Override
		public String getName()
		{
			return "ai " + playerNumber;
		}

		@Override
		public void award(EvilAction awardEvilAction) throws IOException
		{
			availableActions.add(awardEvilAction);
			LinkedList<Integer> others = new LinkedList<>();
			for (int i = 0; i < ai.game.connections.size(); i++)
			{
				if (i != ai.number)
					others.add(i);
			}
			if (others.isEmpty())
				return;
			Collections.shuffle(others, Constants.random);
			ai.game.playAction(ai.number, awardEvilAction, others.removeFirst());
		}
	}
	
	static class HumanPlayer extends InGamePlayer implements GameRole
	{
		PlayerConnection connectedPlayer;
		Game game;
		int index;

		public HumanPlayer(Game game, PlayerConnection player)
		{
			this.game = game;
			this.connectedPlayer = player;
		}

		@Override
		public void gameStarted(GameUpdateInfo info) throws IOException
		{
			send(new LaunchGame(playerNumber, info));
		}

		@Override
		public void send(Message info) throws IOException
		{
			connectedPlayer.connection.sendMessageAndFlush(info);
		}

		@Override
		public void setPlayerNumber(Game game, int index)
		{
			this.index = index;
		}

		@Override
		public PlayerState getState()
		{
			return PlayerState.InGame;
		}

		@Override
		public void nameChanged() throws IOException
		{
			// broadcast a message that the name changed...
		}

		@Override
		public int getIndex()
		{
			return index;
		}

		@Override
		public Game getGame()
		{
			return game;
		}

		@Override
		public void setDone(boolean finishe)
		{
			this.finished = finishe;
		}

		@Override
		public boolean stillWantsToPlay()
		{
			return !finished;
		}

		@Override
		public void quit(GameServer server)
		{
			server.connectedPlayers.add(connectedPlayer);
			connectedPlayer.setWaiting();
		}

		@Override
		public String getName()
		{
			return connectedPlayer.playerName;
		}

		@Override
		public void award(EvilAction awardEvilAction) throws IOException
		{
			availableActions.add(awardEvilAction);
			connectedPlayer.connection.sendMessageAndFlush(new AwardedEvilAction(awardEvilAction));
		}
	}
}
