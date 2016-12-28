/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.msg.lc.LobbyInfoMessage;

public class PlayerConnection
{
	// Connection
	Connection connection;

	// Player information
	String playerName;

	// Lobby information
	Lobby lobby;
	int lobbyNumber;
	boolean ready;
	boolean admin;
	
	// Game information
	Game game;
	int playerNumber = -1;
	

	public PlayerConnection(Connection connection2)
	{
		this.connection = connection2;
		playerName = "Unknown";
	}
	
	
	
	
	
	// information methods:

	String getHostInfo()
	{
		return connection.getConnectionInfo();
	}
	
	public String getName()
	{
		return playerName;
	}
	
	
	
	
	
	
	
	// Lobby methods
	public void updateLobby() throws IOException
	{
		if (lobby == null) return;
		
		connection.sendMessageAndFlush(new LobbyInfoMessage(
				admin,
				lobby.getInfo()));
	}

	public void setName(String name) throws IOException
	{
		if (this.playerName.equals(name))
			return;
		this.playerName = name;
		if (lobby != null)
			lobby.broadcastChanges();
	}

	void kick()
	{
		// send message that you are being kicked...
	}

	public void setReady(boolean ready2) throws IOException
	{
		if (ready == ready2)
			return;
		ready = ready2;
		if (lobby != null)
			lobby.broadcastChanges();
	}





	public int getPlayerNumber()
	{
		return playerNumber;
	}
}
