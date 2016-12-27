package org.hallock.tfe.serve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.msg.GSServerMessage;
import org.hallock.tfe.msg.LCLaunchGame;
import org.hallock.tfe.msg.LCLobbiesListMessage;
import org.hallock.tfe.msg.LSLobbyServerMessage;
import org.hallock.tfe.msg.Message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class LobbyServer
{
	ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
	HashMap<String, Lobby> lobbies = new HashMap<>();
        
        public void listLobbies(WaitingPlayer player) throws IOException
        {
        	LCLobbiesListMessage msg = new LCLobbiesListMessage();
        	
                for (Lobby lobby : lobbies.values())
                {
                    if (!lobby.needsPlayers())
                    {
                        continue;
                    }
                    
                    msg.foundLobby(lobby.getInfo());
                }
                
                player.connection.sendMessageAndFlush(msg);
        }
        
        public void createLobby(WaitingPlayer player) throws IOException
        {
        	Lobby lobby = new Lobby();
        	lobby.host = player;
        	lobby.waitingPlayers.add(player);
        	lobby.options = new GameOptions();
        	do {
        		String id = Utils.createRandomString(50);
                	lobby.id = id;
        	} while (lobbies.containsKey(lobby.id));
        	lobbies.put(lobby.id, lobby);
        	
        	player.assignedLobby = lobby;
        	player.admin = true;
        	lobby.setNumPlayers(lobby.options.numberOfPlayers);
        }
        
        public void addUserToLobby(WaitingPlayer player, String id) throws IOException
        {
        	Lobby lobby = lobbies.get(id);
        	if (lobby == null)
        		return;
        	player.assignedLobby = lobby;
        	lobby.addPlayer(player);
	}
        
        public void startGame(Lobby lobby) throws IOException
        {
		lobby.game = new GameServer(lobby.options);
		for (WaitingPlayer player : lobby.waitingPlayers)
		{
			lobby.game.add(player);
			player.connection.sendMessageAndFlush(new LCLaunchGame());
		}

//		
//		for (int i = 0; i < numPlayers; i++)
//		{
//			final int playerNum = i;
//			ts[i] = new Thread(new Runnable(){
//				@Override
//				public void run()
//				{
//					System.out.println("Waiting for player " + playerNum);
//					try (Socket accept = serverSocket.accept();
//						JsonGenerator generator = Json.createOpenedGenerator(accept.getOutputStream());
//						JsonParser parser = Json.createParser(accept.getInputStream());)
//					{
//						System.out.println("Opened connection " + playerNum);
//						Connection connection = new Connection(accept, generator, parser);
//						connection.readOpen();
//						
//						Player player = new Player(connection, playerNum, server);
//						server.add(player);
//
//						Message message;
//						while ((message = connection.readMessage()) != null)
//						{
//							if (!(message instanceof GSServerMessage))
//							{
//								System.out.println("ignoring " + message);
//							}
//							GSServerMessage msg = (GSServerMessage) message;
//							msg.perform(playerNum, server);
//						}
//					}
//					catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//				}});
//			ts[i].start();
//		}
//
//		for (int i = 0; i < numPlayers; i++)
//		{
//			ts[i].join();
//		}
//	
        }

	public void nothing() throws IOException
	{
		try (ServerSocket serverSocket = new ServerSocket(Constants.LOBBY_PORT);)
		{
			int num = 7 ;
			ExecutorService newFixedThreadPool2 = Executors.newFixedThreadPool(num);
			for (int i=0;i<num;i++)
			newFixedThreadPool2.submit(new Runnable()
				{
					@Override
					public void run()
					{
						while (true)
						{
							handleNextConnection(serverSocket);
						}
					}
				});
			try
			{
				newFixedThreadPool2.awaitTermination(99999, TimeUnit.DAYS);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void handleNextConnection(ServerSocket serverSocket)
	{
		try (Socket accept = serverSocket.accept();
			JsonGenerator generator = Json.createOpenedGenerator(accept.getOutputStream());
			JsonParser parser = Json.createParser(accept.getInputStream());)
		{
			Connection connection = new Connection(accept, generator, parser);
			WaitingPlayer player = new WaitingPlayer(connection);
			connection.readOpen();

			Message message;
			while ((message = connection.readMessage()) != null)
			{
				if (message instanceof LSLobbyServerMessage)
				{
					LSLobbyServerMessage msg = (LSLobbyServerMessage) message;
					msg.perform(this, player);
				}
				else if (message instanceof GSServerMessage)
				{
					Lobby lobby = player.getLobby();
					if (lobby == null)
						continue;
					if (lobby.game == null)
						continue;
					GSServerMessage msg = (GSServerMessage) message;
					msg.perform(player.assignedPlayerNumber, lobby.game);
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

	public static void launch()
	{
		new Thread(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					new LobbyServer().nothing();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}}).start();
	}
}
