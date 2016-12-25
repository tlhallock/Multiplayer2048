package org.hallock.tfe.msg;

import java.io.PrintWriter;
import java.util.Scanner;

import org.hallock.tfe.client.GameClient;
import org.hallock.tfe.cmn.game.TileBoard;

public class GameStateChanged extends ClientMessage
{
	int playerNumber;
	TileBoard board;
	
	public GameStateChanged(int n, TileBoard b)
	{
		playerNumber = n;
		board = new TileBoard(b);
	}
	
	public GameStateChanged(Scanner scanner)
	{
		playerNumber = scanner.nextInt();
		board = new TileBoard(scanner);
	}

	@Override
	public void write(PrintWriter writer)
	{
		writer.print(TYPE + " ");
		writer.write(playerNumber + " ");
		board.print(writer);
	}

	public static final String TYPE = "state_change";

	@Override
	public void perform(GameClient client)
	{
		client.updatePlayer(playerNumber, board);
	}
}
