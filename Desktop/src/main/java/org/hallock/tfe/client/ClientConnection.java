package org.hallock.tfe.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.dsktp.gui.DesktopGameViewer;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.g.PlayerAction;
import org.hallock.tfe.msg.gc.GameClientMessage;
import org.hallock.tfe.msg.gv.GameViewerMessage;
import org.hallock.tfe.msg.lc.LobbyClientMessage;
import org.hallock.tfe.msg.ls.Launch;
import org.hallock.tfe.msg.ls.Ready;
import org.hallock.tfe.msg.ls.Refresh;
import org.hallock.tfe.msg.ls.UpdateOptions;
import org.hallock.tfe.msg.ls.UpdatePlayer;
import org.hallock.tfe.msg.svr.ListLobbies;
import org.hallock.tfe.msg.svr.SCreateLobby;
import org.hallock.tfe.msg.svr.SJoinLobby;
import org.hallock.tfe.msg.svr.SetPlayerInfo;
import org.hallock.tfe.serve.Lobby.LobbyInfo;
import org.hallock.tfe.serve.PlayerInfo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class ClientConnection
{
	Connection connection;
	
	GameOptions options;

	LobbyClient lobby;
	GameViewer game;
	

	LinkedBlockingQueue<Message> toSend = new LinkedBlockingQueue<>();
	
	// Keep network calls off the awt event thread
	Thread sendingThread = new Thread(new Runnable() {
		@Override
		public void run()
		{
			send();
		}});
	Thread receivingThread = new Thread(new Runnable() {
		@Override
		public void run()
		{
			receive();
		}});
	
	


	enum ConnectionStatus
	{
		Loading,
		NotConnected,
		Connecting,
		Connected,
		Closing,
	}
	ConnectionStatus connectionStatus = ConnectionStatus.Loading;
	String name;
	String ip;
	int port;
	Lock lock = new ReentrantLock();
	Condition condition = lock.newCondition();
	
	
	


	public void launchGameGui(int playerNumber, LobbyInfo info) throws IOException
	{
		game = DesktopGameViewer.launchGameGui(this, playerNumber, info);
		send(new PlayerAction(PossiblePlayerActions.ShowAllTileBoards));
		lobby.hideViewer();
	}

	
	
	
	
	
	

	public Connection getConnection()
	{
		return connection;
	}
	public void setViewer(LobbyClient lobbyViewer2)
	{
		this.lobby = lobbyViewer2;
	}

	public void start()
	{
		sendingThread.start();
		receivingThread.start();
	}

	public boolean isConnected()
	{
		// not synchronized yet...
		return connection != null;
	}
	
	private void setConnectionStatus(ConnectionStatus status)
	{
		connectionStatus = status;
		if (lobby != null)
			lobby.setConnectionStatus(status.name());
	}
	public String getConnectionStatus()
	{
		return connectionStatus.name();
	}
	

	public void died(DesktopGameViewer desktopGameViewer)
	{
		game = null;
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	protected void send()
	{
		while (true)
		{
			Message message = null;
			synchronized (toSend)
			{
				try
				{
					message = toSend.take();
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			if (message == null)
				continue;
			try
			{
				lock.lock();
				if (connection == null)
					continue;
				try
				{
					connection.sendMessageAndFlush(message);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			finally
			{
				lock.unlock();
			}
		}
	}


	protected void receive()
	{
		while (true)
		{
			String myIp;
			int myPort;
			String myName;
			lock.lock();
			try
			{
				setConnectionStatus(ConnectionStatus.NotConnected);
				while (ip == null || port < 0)
					condition.await();
				setConnectionStatus(ConnectionStatus.Connecting);
				myIp = ip;
				myPort = port;
				myName = name;
				ip = null;
				port = -1;
				name = null;
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				continue;
			}
			finally
			{
				lock.unlock();
			}
			
			
			lock.lock();
			try (Socket socket = new Socket(myIp, myPort);
				JsonGenerator generator = Json.createOpenedGenerator(socket.getOutputStream());
				JsonParser parser = Json.createParser(socket.getInputStream());)
			{
				connection = new Connection(socket, generator, parser);
				connection.readOpen();
				setConnectionStatus(ConnectionStatus.Connected);
				lock.unlock();
				
				connection.sendMessageAndFlush(new SetPlayerInfo(myName));

				Message message;
				while ((message = connection.readMessage()) != null)
				{
					if (message instanceof LobbyClientMessage && lobby != null)
					{
						LobbyClientMessage msg = (LobbyClientMessage) message;
						msg.perform(lobby);
					}
					else if (message instanceof GameViewerMessage && game != null)
					{
						GameViewerMessage msg = (GameViewerMessage) message;
						msg.perform(game);
					}
					else if (message instanceof GameClientMessage)
					{
						GameClientMessage msg = (GameClientMessage) message;
						msg.perform(this);
					}
					else
					{
						System.out.println("Ignoring " + message);
	                                        continue;
					}
				}

				if (game != null)
				{
					game.die();
				}
//				connection.sendClose();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
			finally
			{
				lock.lock();
				try
				{
					connection = null;
					setConnectionStatus(ConnectionStatus.NotConnected);
				}
				finally
				{
					lock.unlock();
				}
			}
		}
	}



























	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void send(Message message)
	{
		if (connection == null)
			throw new RuntimeException("Not connected!");
		toSend.offer(message);
	}
	public void sendName(String name)
	{
		send(new SetPlayerInfo(name));
	}
	public void sendUpdatePlayer(PlayerInfo player, UpdatePlayer.UpdateAction action)
	{
		send(new UpdatePlayer(player, action));
	}
	public void sendReadyUpdate(boolean isReady)
	{
		send(new Ready(isReady));
	}
	public void sendLaunchRequest()
	{
		send(new Launch());
	}
	public void sendUpdateLobbyRequest()
	{
		send(new Refresh());
	}
	public void sendChangeOptions(GameOptions createOptions)
	{
		send(new UpdateOptions(createOptions));
	}
	public void sendListLobbiesRequest()
	{
		send(new ListLobbies());
	}
	public void sendNewLobbyRequest()
	{
		send(new SCreateLobby());
		send(new ListLobbies()); // I don't think we need to send this one...
	}
	public void sendJoinLobbyRequest(String ip, int port, String id)
	{
		send(new SJoinLobby(id));
	}
	
	
	
	
	



	public void connect(String ip, int port, String name)
	{
		lock.lock();
		try
		{
			this.ip = ip;
			this.port = port;
			this.name = name;
			if (connection != null)
			{
				setConnectionStatus(ConnectionStatus.Closing);
				try
				{
					connection.sendClose();
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			condition.signalAll();
		}
		finally
		{
			lock.unlock();
		}
	}

	public void disconnect()
	{
		lock.lock();
		try
		{
			this.ip = null;
			this.port = -1;
			if (connection != null)
			{
				setConnectionStatus(ConnectionStatus.Closing);
				try
				{
					connection.sendClose();
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			condition.signalAll();
		}
		finally
		{
			lock.unlock();
		}
	}
}
