package org.hallock.tfe.serve;

import java.io.PrintWriter;
import java.util.Scanner;

import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.msg.Message;

public class Player
{
	private PrintWriter output;
	private Scanner input;

	private GameServer server;
	int playerNum;
	
	TileBoard board;
	History history;

	public Player(
			int playerNum,
			GameServer server,
			PrintWriter writer,
			Scanner scanner)
	{
		this.playerNum = playerNum;
		this.server = server;
		this.output = writer;
		this.input = scanner;
	}

	public void send(Message gameStateChanged)
	{
		gameStateChanged.write(output);
		output.flush();
	}

	public void quit()
	{
		System.out.println("Ignoring quit");
	}
}
