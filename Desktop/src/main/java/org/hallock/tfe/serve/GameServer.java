package org.hallock.tfe.serve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.msg.GCGameStateChanged;
import org.hallock.tfe.msg.GSServerMessage;
import org.hallock.tfe.msg.Message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class GameServer
{
	ArrayList<WaitingPlayer> connections = new ArrayList<>();
	
	GameOptions options;
	
	public GameServer(GameOptions options)
	{
		this.options = options;
	}

	synchronized void add(WaitingPlayer player) throws IOException
	{
		connections.add(player);
		// bad place for this?
		player.board = new TileBoard(options.numRows, options.numCols);
		player.board.randomlyFill(options.startingTiles);
		player.history = new History();
		broadCast();
	}
	
	
	
	
	private synchronized void broadCast() throws IOException
	{
		for (WaitingPlayer c : connections)
		{
			broadCast(c);
		}
	}

	private synchronized void broadCast(WaitingPlayer player) throws IOException
	{
		for (WaitingPlayer c : connections)
		{
			player.connection.sendMessageWithoutFlushing(
					new GCGameStateChanged(c.assignedPlayerNumber, c.board));
		}
		player.connection.flush();
	}

	public void play(int player, PossiblePlayerActions swipe) throws IOException
	{
		boolean changed = false;
		TileBoard state = connections.get(player).board;
		History history = connections.get(player).history;

		switch (swipe)
		{
		case Up:
			if (!state.up())
				break;
			state.randomlyFill(options.numberOfNewTilesPerTurn);
			history.updated(state, "");
			changed = true;
			break;
		case Down:
			if (!state.down())
				break;
			state.randomlyFill(options.numberOfNewTilesPerTurn);
			history.updated(state, "");
			changed = true;
			break;
		case Left:
			if (!state.left())
				break;
			state.randomlyFill(options.numberOfNewTilesPerTurn);
			history.updated(state, "");
			changed = true;
			break;
		case Right:
			if (!state.right())
				break;
			state.randomlyFill(options.numberOfNewTilesPerTurn);
			history.updated(state, "");
			changed = true;
			break;
		case Redo:
		{
			TileBoard newState = history.redo();
			if (newState == null)
				break;
			connections.get(player).board = newState;
			changed = true;
			break;
		}
		case Undo:
		{
			TileBoard newState = history.redo();
			if (newState == null)
				break;
			connections.get(player).board = newState;
			changed = true;
			break;
		}
		case Quit:
//			connections.get(player).quit();
			break;
			
		case ShowAllTileBoards:
			broadCast(connections.get(player));
			break;
			
		default:
			System.out.println("Unknown action: " + swipe);
		}
		
		if (changed)
		{
			broadCast();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void launchServer() throws InterruptedException, IOException
	{
		try (ServerSocket serverSocket = new ServerSocket(Constants.TEMP_PORT);)
		{
			final GameServer server = new GameServer(new GameOptions());

			Thread[] ts = new Thread[server.options.numberOfPlayers];

			for (int i = 0; i < server.options.numberOfPlayers; i++)
			{
				final int playerNum = i;
				ts[i] = new Thread(new Runnable(){
					@Override
					public void run()
					{
						System.out.println("Waiting for player " + playerNum);
						try (Socket accept = serverSocket.accept();
							JsonGenerator generator = Json.createOpenedGenerator(accept.getOutputStream());
							JsonParser parser = Json.createParser(accept.getInputStream());)
						{
							System.out.println("Opened connection " + playerNum);
							Connection connection = new Connection(accept, generator, parser);
							connection.readOpen();
							
							Player player = new Player(connection, playerNum, server);
//							server.add(player);

							Message message;
							while ((message = connection.readMessage()) != null)
							{
								if (!(message instanceof GSServerMessage))
								{
									System.out.println("ignoring " + message);
								}
								GSServerMessage msg = (GSServerMessage) message;
								msg.perform(playerNum, server);
							}
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}});
				ts[i].start();
			}

			for (int i = 0; i < server.options.numberOfPlayers; i++)
			{
				ts[i].join();
			}
		}
	}
}
