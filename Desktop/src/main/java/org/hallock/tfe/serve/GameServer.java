package org.hallock.tfe.serve;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.msg.GameStateChanged;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.ServerMessage;

public class GameServer
{
	ArrayList<Player> connections = new ArrayList<>();
	
	GameOptions options;
	
	public GameServer(int numPlayers, GameOptions options)
	{
		this.options = options;
	}

	private void add(Player player)
	{
		connections.add(player);
		// bad place for this?
		player.board = new TileBoard(options.numRows, options.numCols);
		player.board.randomlyFill(options.startingTiles);
		player.history = new History();
		broadCast();
	}
	
	
	
	
	private void broadCast()
	{
		for (Player c : connections)
		{
			broadCast(c);
		}
	}

	private void broadCast(Player player)
	{
		for (Player c : connections)
		{
			player.send(new GameStateChanged(c.playerNum, c.board));
		}
	}

	public void play(int player, PossiblePlayerActions swipe)
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
			connections.get(player).quit();
			System.out.println("Ignoring quit");
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		try (ServerSocket socket = new ServerSocket(Constants.TEMP_PORT);)
		{
			int numPlayers = 2;
			final GameServer server = new GameServer(numPlayers, new GameOptions());

			Thread[] ts = new Thread[numPlayers];

			for (int i = 0; i < numPlayers; i++)
			{
				final int playerNum = i;
				ts[i] = new Thread(new Runnable(){
					@Override
					public void run()
					{
						System.out.println("Waiting for player " + playerNum);
						try (Socket accept = socket.accept();
								PrintWriter writer = new PrintWriter(accept.getOutputStream());
								Scanner scanner = new Scanner(accept.getInputStream());)
						{
							
							System.out.println("Opened connection " + playerNum);
							Player player = new Player(playerNum, server, writer, scanner);
							server.add(player);

							while (scanner.hasNext())
							{
								Message parse = Message.parse(scanner);
								if (!(parse instanceof ServerMessage))
								{
									System.out.println("ignoring " + parse);
								}
								ServerMessage msg = (ServerMessage) parse;
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

			for (int i = 0; i < numPlayers; i++)
			{
				ts[i].join();
			}
		}
	}
}
