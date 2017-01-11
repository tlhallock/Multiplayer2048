package org.hallock.tfe.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.msg.MessageSender;
import org.hallock.tfe.msg.g.PlayEvilActionSender;
import org.hallock.tfe.msg.g.PlayerActionSender;
import org.hallock.tfe.msg.ls.LaunchSender;
import org.hallock.tfe.msg.ls.ReadySender;
import org.hallock.tfe.msg.ls.RefreshSender;
import org.hallock.tfe.msg.ls.UpdateOptionsSender;
import org.hallock.tfe.msg.ls.UpdatePlayer;
import org.hallock.tfe.msg.ls.UpdatePlayerSender;
import org.hallock.tfe.msg.svr.CreateLobbySender;
import org.hallock.tfe.msg.svr.JoinLobbySender;
import org.hallock.tfe.msg.svr.ListLobbiesSender;
import org.hallock.tfe.msg.svr.SetPlayerInfoSender;
import org.hallock.tfe.serve.WaitingPlayerInfo;
import org.hallock.tfe.sys.GameUpdateInfo;
import org.hallock.tfe.sys.Registry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class ClientConnection
{
	Connection connection;
	
	GameOptions options;

	LobbyClient lobby;
	GameViewer game;
	
	ClientReader reader = new ClientReader(this);
	

	LinkedBlockingQueue<MessageSender> toSend = new LinkedBlockingQueue<>();
	
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
	
	
	


	public void launchGameGui(int playerNumber, GameUpdateInfo info) throws IOException
	{
		game = Registry.clientGuiImplemententation.launchGameGui(this, playerNumber, info);
		send(new PlayerActionSender(PossiblePlayerActions.ShowAllTileBoards));
		reader.viewGame(game);
		lobby.hideViewer();
	}

	
	
	
	
	
	

	public Connection getConnection()
	{
		return connection;
	}
	public void setViewer(LobbyClient lobbyViewer2)
	{
		this.lobby = lobbyViewer2;
		reader.viewLobbies(this.lobby);
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
	

	public void gameStopped()
	{
		this.lobby.view();
		reader.viewLobbies(this.lobby);
		game = null;
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	protected void send()
	{
		while (true)
		{
			MessageSender message = null;
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
					message.sendMessage(connection.getGenerator());
					connection.flush();
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
				JsonParser parser = Json.createOpenedParser(socket.getInputStream());)
			{
				connection = new Connection(socket, generator, parser);
				setConnectionStatus(ConnectionStatus.Connected);
				lock.unlock();
				
				connection.sendMessageAndFlush(new SetPlayerInfoSender(myName));

				while (reader.handleNextMessage(parser))
					;

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



























	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void send(MessageSender message)
	{
		if (connection == null)
			throw new RuntimeException("Not connected!");
		toSend.offer(message);
	}
	public void sendName(String name)
	{
		send(new SetPlayerInfoSender(name));
	}
	public void sendUpdatePlayer(WaitingPlayerInfo player, UpdatePlayer.UpdateAction action)
	{
		send(new UpdatePlayerSender(player.lobbyNumber, action));
	}
	public void sendReadyUpdate(boolean isReady)
	{
		send(new ReadySender(isReady));
	}
	public void sendLaunchRequest()
	{
		send(LaunchSender.SENDER);
	}
	public void sendUpdateLobbyRequest()
	{
		send(RefreshSender.SENDER);
	}
	public void sendChangeOptions(GameOptions createOptions)
	{
		send(new UpdateOptionsSender(createOptions));
	}
	public void sendListLobbiesRequest()
	{
		send(ListLobbiesSender.SENDER);
	}
	public void sendNewLobbyRequest()
	{
		send(CreateLobbySender.SENDER);
	}
	public void sendJoinLobbyRequest(String ip, int port, String id)
	{
		send(new JoinLobbySender(id));
	}
	public void sendEvilAction(EvilAction a, int otherPlayer)
	{
		send(new PlayEvilActionSender(a, otherPlayer));
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
