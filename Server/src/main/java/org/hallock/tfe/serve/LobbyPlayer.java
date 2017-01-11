package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.ai.AiOptions;
import org.hallock.tfe.msg.lc.KickSender;
import org.hallock.tfe.msg.lc.LobbyInfoSender;
import org.hallock.tfe.serve.PlayerConnection.PlayerRole.LobbyRole;
import org.hallock.tfe.serve.ServerGamePlayer.HumanPlayer;
import org.hallock.tfe.serve.ServerGamePlayer.InGameComputerPlayer;

public abstract class LobbyPlayer
{
	int index;
	
	protected LobbyPlayer(int index)
	{
		this.index = index;
	}

	@Override
	public String toString()
	{
		return getSpec().name();
	}

	public void setIndex(int j)
	{
		this.index = j;
	}

	public int getIndex()
	{
		return index;
	}
	
	public abstract PlayerSpec getSpec();
	public abstract boolean isReady();
	public abstract boolean isWaitingForPlayer();
	public abstract void kick(GameServer server) throws IOException;
	public abstract boolean isConnected();
	public abstract ConnectedPlayer connect(PlayerConnection player, Lobby lobby);
	public abstract void setInfo(WaitingPlayerInfo playerInfo);
	public abstract void updateLobby() throws IOException;
	public abstract void join(Game game) throws IOException;
	public abstract void setReady(boolean ready);
	public abstract boolean isHost();
	
	public void setAiOptions(AiOptions aiOptions) {}
	
	public static class ComputerWaitingPlayer extends LobbyPlayer
	{
		protected ComputerWaitingPlayer(int index)
		{
			super(index);
		}

		AiOptions options;
		
		@Override
		public PlayerSpec getSpec()
		{
			return PlayerSpec.Computer;
		}

		@Override
		public boolean isReady()
		{
			return true;
		}

		@Override
		public boolean isWaitingForPlayer()
		{
			return false;
		}

		@Override
		public void kick(GameServer server) {}

		@Override
		public boolean isConnected()
		{
			return false;
		}

		@Override
		public void setInfo(WaitingPlayerInfo playerInfo)
		{
			playerInfo.type = getSpec();
			playerInfo.name = "Computer";
			playerInfo.status = "ready";
			playerInfo.isHost = false;
		}

		@Override
		public void updateLobby() {}

		@Override
		public void join(Game game) throws IOException
		{
			game.add(new InGameComputerPlayer(game.getAiManager(), options));
		}


		@Override
		public ConnectedPlayer connect(PlayerConnection player, Lobby lobby)
		{
			throw new RuntimeException("Can't connect to me");
		}
		@Override
		public void setReady(boolean ready)
		{
			throw new RuntimeException("already ready");
		}

		@Override
		public boolean isHost()
		{
			return false;
		}

		@Override
		public void setAiOptions(AiOptions aiOptions)
		{
			this.options = aiOptions;
		}
	}

	public static class EmptyHumanPlayer extends LobbyPlayer
	{
		protected EmptyHumanPlayer(int index)
		{
			super(index);
		}

		@Override
		public PlayerSpec getSpec()
		{
			return PlayerSpec.HumanPlayer;
		}

		@Override
		public boolean isReady()
		{
			return false;
		}

		@Override
		public boolean isWaitingForPlayer()
		{
			return true;
		}

		@Override
		public void kick(GameServer server) {}

		@Override
		public boolean isConnected()
		{
			return false;
		}

		@Override
		public ConnectedPlayer connect(PlayerConnection player, Lobby lobby)
		{
			return new ConnectedPlayer(player, lobby, index);
		}

		@Override
		public void setInfo(WaitingPlayerInfo playerInfo)
		{
			playerInfo.type = getSpec();
			playerInfo.name = "empty";
			playerInfo.status = "waiting";
			playerInfo.isHost = false;
		}

		@Override
		public void updateLobby() {}

		@Override
		public void join(Game game) throws IOException
		{
			throw new RuntimeException("bad state.");
		}
		@Override
		public void setReady(boolean ready)
		{
			throw new RuntimeException("impossible");
		}

		@Override
		public boolean isHost()
		{
			return false;
		}
	}
	public static class ConnectedPlayer extends LobbyPlayer implements LobbyRole
	{
		boolean ready;
		boolean isHost;
		public PlayerConnection connected;
		Lobby lobby;
		
		public ConnectedPlayer(PlayerConnection connected, Lobby lobby, int index)
		{
			super(index);
			this.lobby = lobby;
			this.connected = connected;
		}

		@Override
		public PlayerState getState()
		{
			return PlayerState.InLobby;
		}

		@Override
		public Lobby getLobby()
		{
			return lobby;
		}

		@Override
		public PlayerSpec getSpec()
		{
			return PlayerSpec.HumanPlayer;
		}

		@Override
		public boolean isReady()
		{
			return ready;
		}

		@Override
		public boolean isWaitingForPlayer()
		{
			return false;
		}

		@Override
		public void kick(GameServer server) throws IOException
		{
			connected.setWaiting();
			connected.connection.sendMessageAndFlush(KickSender.SENDER);
		}

		@Override
		public boolean isConnected()
		{
			return true;
		}

		@Override
		public ConnectedPlayer connect(PlayerConnection player, Lobby lobby)
		{
			throw new RuntimeException("already connected");
		}

		@Override
		public void setInfo(WaitingPlayerInfo playerInfo)
		{
			playerInfo.type = getSpec();
			playerInfo.name = connected.playerName;
			playerInfo.status = isReady() ? "ready" : "not ready";
			playerInfo.isHost = isHost();
		}

		@Override
		public void updateLobby() throws IOException
		{
			connected.connection.sendMessageAndFlush(new LobbyInfoSender(
					isHost,
					lobby.createInfo()));
		}

		@Override
		public void join(Game game) throws IOException
		{
			HumanPlayer humanPlayer = new HumanPlayer(game, connected);
			game.add(humanPlayer);
			connected.setRole(humanPlayer);
			connected.reader.joinedGame(game);
		}
		@Override
		public void setReady(boolean ready)
		{
			this.ready = ready;
		}

		@Override
		public boolean isHost()
		{
			return isHost;
		}

		@Override
		public void nameChanged() throws IOException
		{
			lobby.broadcastChanges();
		}
	}
}
