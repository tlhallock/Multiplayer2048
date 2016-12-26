package org.hallock.tfe.serve;

import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.util.Connection;

public class Player
{
	Connection connection;

	private GameServer server;
	int playerNum;
	
	TileBoard board;
	History history;

	public Player(
			Connection connection,
			int playerNum,
			GameServer server)
	{
		this.connection = connection;
		this.playerNum = playerNum;
		this.server = server;
	}

//	public void send(Message gameStateChanged) throws IOException
//	{
//		connection.sendMessageAndFlush(gameStateChanged);
//	}

	public void quit()
	{
		System.out.println("Ignoring quit");
	}
}
