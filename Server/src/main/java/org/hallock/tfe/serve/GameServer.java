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
import org.hallock.tfe.msg.lc.LobbiesListSender;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class GameServer
{
	ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
	LinkedList<PlayerConnection> connectedPlayers = new LinkedList<>();
	HashMap<String, Lobby> lobbies = new HashMap<>();
	HashMap<String, Boolean> availableLobbies = new HashMap<>();
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
		Boolean old = availableLobbies.get(lobby.id);
		if (old == null || old != lobby.needsPlayers())
		{
			listLobbies(connectedPlayers, false);
			availableLobbies.put(lobby.id, lobby.needsPlayers());
		}
	}

        public void listLobbies(PlayerConnection connectedPlayer) throws IOException
        {
        	listLobbies(Collections.singleton(connectedPlayer), false);
        }
        public void listLobbies(Collection<PlayerConnection> connectedPlayers2, boolean fail) throws IOException
        {
        	LobbiesListSender msg = new LobbiesListSender();
        	
                for (Lobby lobby : lobbies.values())
                {
                    if (!lobby.needsPlayers())
                    {
                        continue;
                    }
                    
                    msg.foundLobby(lobby.createInfo());
                }
                
                for (PlayerConnection c : connectedPlayers2)
                {
                	try
                	{
                		c.connection.sendMessageAndFlush(msg);
                	}
                	catch (IOException t)
                	{
                		if (fail)
                			throw t;
                		else
                			t.printStackTrace();
                	}
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
        	lobby.options = new GameOptions(ServerSettings.DEFAULT_GAME_OPTIONS);
        	lobby.setNumPlayers(lobby.options.numberOfPlayers, false);
        	lobby.addPlayer(player, false, true);
        	lobbies.put(lobby.id, lobby);
        	player.reader.joinedLobby(lobby);
        	lobby.broadcastChanges();
        }
        
        public void addUserToLobby(PlayerConnection player, String id) throws IOException
        {
        	if (id.length() == 0)
        		return;
        	Lobby lobby = lobbies.get(id);
        	if (lobby == null)
        		return;
        	connectedPlayers.remove(player);
        	lobby.addPlayer(player, true, false);
        	player.reader.joinedLobby(lobby);
	}

	public void migrateToGame(Lobby lobby, Game game) throws IOException
	{
        	lobbies.remove(lobby.id);
        	games.add(game);
        	availableLobbies.remove(lobby.id);
        	listLobbies(connectedPlayers, false);
	}

	public void gameFinished(Game game)
	{
		games.remove(game);
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
			PlayerConnection player = new PlayerConnection(this, connection);
			Json.readOpen(parser);
			player.setWaiting();
			
			listLobbies(player);

			while (player.reader.handleNextMessage(parser))
				;

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
