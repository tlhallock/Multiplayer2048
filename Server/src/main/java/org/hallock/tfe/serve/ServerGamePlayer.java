
package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.hallock.tfe.ai.AiOptions;
import org.hallock.tfe.ai.GameWriterIf;
import org.hallock.tfe.cmn.game.InGamePlayer;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.msg.MessageSender;
import org.hallock.tfe.msg.gc.LaunchGameSender;
import org.hallock.tfe.msg.gv.AwardEvilActionSender;
import org.hallock.tfe.serve.PlayerConnection.PlayerRole.GameRole;
import org.hallock.tfe.serve.ai.ComputerAI;
import org.hallock.tfe.sys.GameUpdateInfo;

public abstract class ServerGamePlayer extends InGamePlayer
{
	public LinkedList<EvilAction> availableActions = new LinkedList<>();
	public LinkedList<EvilAction> appliedActions = new LinkedList<>();
	
	public abstract void gameStarted(GameUpdateInfo info) throws IOException;
	public abstract void send(MessageSender info) throws IOException;
	public abstract void award(EvilAction awardEvilAction) throws IOException;
	public abstract void changed();


	@Override
	public Collection<? extends EvilAction> getAppliedActions()
	{
		return availableActions;
	}

	@Override
	public Collection<? extends EvilAction> getAvailableActions()
	{
		return appliedActions;
	}
	
	static class InGameComputerPlayer extends ServerGamePlayer
	{
		ComputerAI ai;
		AiOptions options;
		
		public InGameComputerPlayer(ComputerAI ai, AiOptions options)
		{
			this.ai = ai;
			this.options = options;
		}

		@Override
		public void gameStarted(GameUpdateInfo info)
		{
			changed();
		}

		@Override
		public void send(MessageSender info) throws IOException
		{
			// don't need to do anything yet...
		}

		@Override
		protected void statusChanged() {
			switch (getStatus())
			{
			case Playing:
				return;
			case Viewing:
			case NoMoreMoves:
			case Disconnected:
				ai.getGame().disconnect(playerNumber);
				break;
			}
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
			ai.award(playerNumber, awardEvilAction);
		}

		@Override
		public void createWriter() throws IOException
		{
			writer = GameWriterIf.createAiWriter(options);
		}

		@Override
		public void changed()
		{
			ai.updateBoard(playerNumber);
		}
	}
	
	static class HumanPlayer extends ServerGamePlayer implements GameRole
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
			send(new LaunchGameSender(playerNumber, info));
		}

		@Override
		public void send(MessageSender info) throws IOException
		{
			connectedPlayer.connection.sendMessageAndFlush(info);
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
		protected void statusChanged()
		{
			switch (getStatus())
			{
			case NoMoreMoves:
			case Playing:
			case Viewing:
				break;
			case Disconnected:
				connectedPlayer.setWaiting();
				break;
			}
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
			connectedPlayer.connection.sendMessageAndFlush(new AwardEvilActionSender(awardEvilAction));
		}

		@Override
		public void createWriter() throws IOException
		{
			writer = GameWriterIf.createHumanPlayerWriter(connectedPlayer.getName());
		}

		@Override
		public void changed()
		{
			// message about to be sent anyway...
		}
	}
	
	public static class LeftPlayer extends ServerGamePlayer
	{
		String name;
		
		public LeftPlayer(String name)
		{
			this.name = name;
		}
		
		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public void award(EvilAction awardEvilAction) throws IOException {}
		@Override
		public void gameStarted(GameUpdateInfo info) throws IOException {}
		@Override
		public void send(MessageSender info) throws IOException {}
		@Override
		public void createWriter() {}
		@Override
		public void changed() {}
	}
}
