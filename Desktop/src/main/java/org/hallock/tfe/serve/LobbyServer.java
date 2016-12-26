package org.hallock.tfe.serve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.msg.LCLobbiesListMessage;
import org.hallock.tfe.msg.LSLobbyServerMessage;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.Lobby.LobbyInfo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class LobbyServer
{
	ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
	LinkedList<Lobby> lobbies = new LinkedList<>();
        
        public void listLobbies(WaitingPlayer player) throws IOException
        {
        	LCLobbiesListMessage msg = new LCLobbiesListMessage();
                for (Lobby lobby : lobbies)
                {
                    if (!lobby.needsPlayers())
                    {
                        continue;
                    }
                    
                    msg.foundLobby(lobby.getInfo());
                }
                
                player.connection.sendMessageAndFlush(msg);
        }
        
        public LobbyInfo createLobby(WaitingPlayer player)
        {
        	String id = Utils.createRandomString(50);
        	Lobby lobby = new Lobby();
        	lobby.host = player;
        	lobby.id = id;
        	lobby.options = new GameOptions();
        	lobbies.add(lobby);
        	return lobby.getInfo();
        }
        
        public void addUserToLobby()
        {
        	
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
				if (!(message instanceof LSLobbyServerMessage))
				{
					System.out.println("Ignoring " + message);
					continue;
				}
				LSLobbyServerMessage msg = (LSLobbyServerMessage) message;
				msg.perform(this, player);
			}

			connection.sendClose();
		}
		catch (IOException e)
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
