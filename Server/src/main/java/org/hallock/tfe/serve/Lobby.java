package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.ArrayList;

import org.hallock.tfe.ai.AiOptions;
import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.msg.ls.UpdatePlayer.UpdateAction;
import org.hallock.tfe.serve.LobbyPlayer.ComputerWaitingPlayer;
import org.hallock.tfe.serve.LobbyPlayer.ConnectedPlayer;
import org.hallock.tfe.serve.LobbyPlayer.EmptyHumanPlayer;
import org.hallock.tfe.serve.PlayerConnection.PlayerRole;


public class Lobby
{
	String lobbyName;
	GameOptions options;
	String id;
	ArrayList<LobbyPlayer> players = new ArrayList<>();

	GameServer server;
	
	public Lobby(GameServer server)
	{
		this.server = server;
	}

	
	private void swap(int i, int j)
	{
		LobbyPlayer tmp = players.get(j);
		players.set(j, players.get(i));
		players.set(i, tmp);
		players.get(i).setIndex(i);
		players.get(j).setIndex(j);
	}

	private boolean pushHumanPlayersUp() throws IOException
	{
		boolean changed = false;
		for (int i = 0; i < players.size(); i++)
		{
			if (!players.get(i).isWaitingForPlayer())
			{
				continue;
			}
			for (int j = i + 1; j < players.size(); j++)
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
        	for (LobbyPlayer p : players)
        		if (p.isWaitingForPlayer())
        			return true;
        	return false;
        }
        
        private boolean allAreReady()
        {
        	for (LobbyPlayer p : players)
        		if (!p.isReady())
        			return false;
        	return true;
        }

	private int countDesiredHumanPlayers()
	{
		int count = 0;
		for (LobbyPlayer p : players)
			if (p.isWaitingForPlayer())
				count++;
		return count;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public void setNumPlayers(int numPlayers, boolean b) throws IOException
	{
		// Add enough
		while (players.size() < numPlayers)
		{
			switch (ServerSettings.DEFAULT_NEW_PLAYER)
			{
			case Computer:
				ComputerWaitingPlayer computerWaitingPlayer = new ComputerWaitingPlayer(players.size());
				computerWaitingPlayer.setAiOptions(options.aiOptions);
				players.add(computerWaitingPlayer);
				break;
			case HumanPlayer:
				players.add(new EmptyHumanPlayer(players.size()));
				break;
			default:
				throw new RuntimeException();
			}
		}
		
		pushHumanPlayersUp();
		
		// Remove extra
		while (players.size() > numPlayers)
		{
			LobbyPlayer playerPlaceHolder = players.get(players.size() - 1);
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
			connect.setIndex(i);
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
		for (LobbyPlayer player : players)
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
		LobbyPlayer waitingPlayer = players.get(connection.getRole().getIndex());
		waitingPlayer.updateLobby();
	}
	
	
        
        public void playerIsReady(PlayerConnection connection, boolean ready) throws IOException
        {
		if (!PlayerRole.isInLobby(connection))
			return;
        	LobbyPlayer player = players.get(connection.getRole().getIndex());
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
		setComputerAiOptions(options.aiOptions);
	}
	
	private void setComputerAiOptions(AiOptions aiOptions)
	{
		for (LobbyPlayer player : players)
		{
			player.setAiOptions(aiOptions);
		}
	}


	public void startGame(PlayerConnection launcher) throws IOException
	{
		if (!PlayerRole.isInLobby(launcher))
			return;
		if (!players.get(launcher.getRole().getIndex()).isHost())
			return;
		
		Game game = new Game(server, options);
		server.migrateToGame(this, game);
		for (LobbyPlayer player : players)
		{
			player.join(game);
		}
		game.start();
	}
	
	private void setPlayerToHuman(int player) throws IOException
	{
		if (players.get(player).isHost())
			return;
		
		players.set(player, new EmptyHumanPlayer(player));
		broadcastChanges();
	}
	
	private void setPlayerToComputer(int player) throws IOException
	{
		if (players.get(player).isHost())
			return;
		
		if (players.get(player).isConnected())
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
		ComputerWaitingPlayer computerWaitingPlayer = new ComputerWaitingPlayer(player);
		computerWaitingPlayer.setAiOptions(options.aiOptions);
		players.set(player, computerWaitingPlayer);
		broadcastChanges();
	}

	private void kick(int recipient) throws IOException
	{
		if (players.get(recipient).isHost())
			return;
		players.get(recipient).kick(server);
		players.set(recipient, new EmptyHumanPlayer(recipient));
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
			setPlayerToComputer(playerNumber);
			break;
		case SetHuman:
			setPlayerToHuman(playerNumber);
			break;
		default:
			throw new RuntimeException("not implemented.");
		}
	}
}
