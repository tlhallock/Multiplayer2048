package org.hallock.tfe.serve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.g.GameMessage;
import org.hallock.tfe.msg.lc.LobbiesList;
import org.hallock.tfe.msg.ls.LobbyMessage;
import org.hallock.tfe.msg.svr.ServerMessage;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class GameServer
{
	ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
	LinkedList<PlayerConnection> connectedPlayers = new LinkedList<>();
	HashMap<String, Lobby> lobbies = new HashMap<>();
	LinkedList<Game> games = new LinkedList<>();
	

	ExecutorService executor = Executors.newCachedThreadPool();
	final Object waitingSync = new Object();
	int desiredNumberWaiting;
	int numThreadCurrentlyWaiting;
	
	ServerSocket socket;
	
	public GameServer(ServerSocket socket, int desiredWaiting)
	{
		this.desiredNumberWaiting = desiredWaiting;
		this.socket = socket;
	}
        

	public void lobbyChanged(Lobby lobby) throws IOException
	{
		if (!lobby.needsPlayers())
			return;
		listLobbies(connectedPlayers);
	}

        public void listLobbies(PlayerConnection connectedPlayer) throws IOException
        {
        	listLobbies(Collections.singleton(connectedPlayer));
        }
        public void listLobbies(Collection<PlayerConnection> connectedPlayers2) throws IOException
        {
        	LobbiesList msg = new LobbiesList();
        	
                for (Lobby lobby : lobbies.values())
                {
                    if (!lobby.needsPlayers())
                    {
                        continue;
                    }
                    
                    msg.foundLobby(lobby.getInfo());
                }
                
                for (PlayerConnection c : connectedPlayers2)
                {
                	c.connection.sendMessageAndFlush(msg);
                }
        }
        
        public void createLobby(PlayerConnection player) throws IOException
        {
        	connectedPlayers.remove(player);
        	Lobby lobby = new Lobby(this);
        	do {
        		String id = Utils.createRandomString(50);
                	lobby.id = id;
        	} while (lobbies.containsKey(lobby.id));
        	lobby.options = new GameOptions();
        	lobby.setNumPlayers(lobby.options.numberOfPlayers, false);
        	lobby.host = player;
        	player.lobby = lobby;
        	player.admin = true;
        	lobby.addPlayer(player, false);
        	lobbies.put(lobby.id, lobby);
        	lobby.broadcastChanges();
        }
        
        public void addUserToLobby(PlayerConnection player, String id) throws IOException
        {
        	Lobby lobby = lobbies.get(id);
        	if (lobby == null)
        		return;
        	connectedPlayers.remove(player);
        	player.lobby = lobby;
        	player.admin = false;
        	lobby.addPlayer(player, true);
	}

	public void migrateToGame(Lobby lobby, Game game)
	{
        	lobbies.remove(lobby.id);
        	games.add(game);
	}
        
        
        
        
        
        
        
        
        
        
        
        
        

        
        
	
        
	private void ensureSpawned()
	{
		if (numThreadCurrentlyWaiting < desiredNumberWaiting)
		{
			executor.submit(new Runnable()
			{
				@Override
				public void run()
				{
					handleNextConnection();
				}
			});
		}
	}
        
	private void handleNextConnection()
	{
		synchronized (waitingSync)
		{
			numThreadCurrentlyWaiting++;
			ensureSpawned();
		}
		
		try (Socket accept = socket.accept();
			JsonGenerator generator = Json.createOpenedGenerator(accept.getOutputStream());
			JsonParser parser = Json.createParser(accept.getInputStream());)
		{
			synchronized (waitingSync)
			{
				numThreadCurrentlyWaiting--;
				ensureSpawned();
			}
			
			Connection connection = new Connection(accept, generator, parser);
			PlayerConnection player = new PlayerConnection(connection);
			connection.readOpen();
			connectedPlayers.add(player);

			Message message;
			while ((message = connection.readMessage()) != null)
			{
				// synchronize on player
				if (message instanceof LobbyMessage && player.lobby != null)
				{
					LobbyMessage msg = (LobbyMessage) message;
					msg.perform(player.lobby, player);
				}
				else if (message instanceof GameMessage && player.game != null)
				{
					GameMessage msg = (GameMessage) message;
					msg.perform(player.game, player);
				}
				else if (message instanceof ServerMessage)
				{
					ServerMessage msg = (ServerMessage) message;
					msg.perform(this, player);
				}
				else
				{
					System.out.println("Ignoring " + message);
				}
			}

			connection.sendClose();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public void start() throws IOException
	{
		ensureSpawned();
	}



}
