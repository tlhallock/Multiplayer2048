package org.hallock.tfe.serve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer
{
	PlayerConnection[] connections;
	
	
	public static void main(String[] args) throws IOException
	{
		try (ServerSocket socket = new ServerSocket(8088);
				Socket player1 = socket.accept();
				Socket player2 = socket.accept();)
		{
			
			
		}
	}
}
