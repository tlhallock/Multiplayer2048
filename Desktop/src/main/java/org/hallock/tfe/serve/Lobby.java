package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.ArrayList;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.msg.ls.UpdatePlayer.UpdateAction;
import org.hallock.tfe.serve.PlayerConnection.PlayerRole;
import org.hallock.tfe.serve.WaitingPlayer.ConnectedPlayer;
import org.hallock.tfe.serve.WaitingPlayer.EmptyHumanPlayer;


public class Lobby
{
	String lobbyName;
	GameOptions options;
	String id;
	ArrayList<WaitingPlayer> players = new ArrayList<>();

	private GameServer server;
	
	public Lobby(GameServer server)
	{
		this.server = server;
	}

	
	private void swap(int i, int j)
	{
		WaitingPlayer tmp = players.get(j);
		players.set(j, players.get(i));
		players.set(i, tmp);
		players.get(i).setIndex(i);
		players.get(j).setIndex(j);
	}

	private boolean pushHumanPlayersUp() throws IOException
	{
		boolean changed = false;
		for (int i=0;i<players.size();i++)
		{
			if (!players.get(i).isWaitingForPlayer())
			{
				continue;
			}
			for (int j=i+1;j<players.size();j++)
			{
				if (!players.get(i).isConnected())
				{
					continue;
				}
				swap(i, j);
				changed = true;
			}
		}
		return changed;
	}
        boolean needsPlayers()
        {
        	for (WaitingPlayer p : players)
        		if (p.isWaitingForPlayer())
        			return true;
        	return false;
        }
        
        private boolean allAreReady()
        {
        	for (WaitingPlayer p : players)
        		if (!p.isReady())
        			return false;
        	return true;
        }

	private int countDesiredHumanPlayers()
	{
		int count = 0;
		for (WaitingPlayer p : players)
			if (p.isWaitingForPlayer())
				count++;
		return count;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public void setNumPlayers(int numPlayers, boolean b) throws IOException
	{
		// Add enough
		while (players.size() < numPlayers)
		{
			players.add(ServerSettings.DEFAULT_NEW_PLAYER.createPlayer());
		}
		
		pushHumanPlayersUp();
		
		// Remove extra
		while (players.size() > numPlayers)
		{
			WaitingPlayer playerPlaceHolder = players.get(players.size() - 1);
			players.remove(players.size() - 1);
			playerPlaceHolder.kick(server);
		}

		if (b)
			broadcastChanges();
	}
        
	public void addPlayer(PlayerConnection player, boolean b, boolean isHost) throws IOException
	{
		if (!needsPlayers())
			return;

		boolean ableToAdd = false;
		for (int i = 0; i < players.size() && !ableToAdd; i++)
		{
			if (!players.get(i).isWaitingForPlayer())
				continue;
			ConnectedPlayer connect = players.get(i).connect(player, this);
			players.set(i, connect);
			player.setRole(connect);
			connect.isHost = isHost;
			ableToAdd = true;
		}

		if (b && ableToAdd)
		{
			broadcastChanges();
		}
	}

	public void broadcastChanges() throws IOException
	{
		for (WaitingPlayer player : players)
		{
			player.updateLobby();
		}
		server.lobbyChanged(this);
	}
	
	public LobbyInfo createInfo()
	{
		LobbyInfo info = new LobbyInfo();
		info.id = id;
		info.options = new GameOptions(options);
		info.players = new WaitingPlayerInfo[players.size()];
		info.allReady = allAreReady();
		
		for (int i = 0; i < players.size(); i++)
		{
			info.players[i] = new WaitingPlayerInfo(i);
			players.get(i).setInfo(info.players[i]);
		}

		return info;
	}
	
	
	
	
	
	
	
	
	
	/*
	 * Message actions
	 */
	
	
	
	public void refresh(PlayerConnection connection) throws IOException
	{
		if (!PlayerRole.isInLobby(connection))
			return;
		WaitingPlayer waitingPlayer = players.get(connection.getRole().getIndex());
		waitingPlayer.updateLobby();
	}
	
	
        
        public void playerIsReady(PlayerConnection connection, boolean ready) throws IOException
        {
		if (!PlayerRole.isInLobby(connection))
			return;
        	WaitingPlayer player = players.get(connection.getRole().getIndex());
        	if (ready != player.isReady())
        	{
        		player.setReady(ready);
        		broadcastChanges();
        	}
        }

	public void setOptions(PlayerConnection player, GameOptions options2) throws IOException
	{
		if (!PlayerRole.isInLobby(player))
			return;
		if (!players.get(player.getRole().getIndex()).isHost())
			return;
		this.options = options2;
		setNumPlayers(options.numberOfPlayers, true);
	}
	
	public void startGame(PlayerConnection launcher) throws IOException
	{
		if (!PlayerRole.isInLobby(launcher))
			return;
		if (!players.get(launcher.getRole().getIndex()).isHost())
			return;
		
		Game game = new Game(server, options);
		server.migrateToGame(this, game);
		for (WaitingPlayer player : players)
		{
			player.join(game);
		}
		
		server.migrateToGame(this, game);
		game.start();
	}

	private void setPlayerSpec(int player, PlayerSpec spec) throws IOException
	{
		if (players.get(player).isHost())
			return;
		
		if (players.get(player).isConnected() && spec.equals(PlayerSpec.Computer))
		{
			boolean ableToMove = false;
			for (int i = 0; i < players.size() && !ableToMove; i++)
			{
				if (i == player || !players.get(i).isWaitingForPlayer())
					continue;
				swap(player, i);
				ableToMove = true;
			}
			if (!ableToMove)
			{
				players.get(player).kick(server);
			}
			
		}
		players.set(player, spec.createPlayer());
		broadcastChanges();
	}
	
	private void kick(int recipient) throws IOException
	{
		if (players.get(recipient).isHost())
			return;
		players.get(recipient).kick(server);
		players.set(recipient, new EmptyHumanPlayer());
		broadcastChanges();
	}

	public void performAction(PlayerConnection player, int playerNumber, UpdateAction action) throws IOException
	{
		if (!PlayerRole.isInLobby(player))
			return;
		if (!players.get(player.getRole().getIndex()).isHost())
			return;
		
		/** Could be done through inheritance **/
		switch (action)
		{
		case Kick:
			kick(playerNumber);
			break;
		case SetComputer:
			setPlayerSpec(playerNumber, PlayerSpec.Computer);
			break;
		case SetHuman:
			setPlayerSpec(playerNumber, PlayerSpec.HumanPlayer);
			break;
		default:
			throw new RuntimeException("not implemented.");
		}
	}
}
