/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.msg.LCLobbyChanged;
import org.hallock.tfe.serve.Lobby.LobbyInfo;

/**
 *
 * @author trever
 */
public class WaitingPlayer
{
	Connection connection;

	String playerName;

	Lobby assignedLobby;
	int assignedPlayerNumber;

	boolean ready;

	public WaitingPlayer(Connection connection2)
	{
		this.connection = connection2;
	}
	
	public void updateLobby(boolean isAdmin, LobbyInfo info) throws IOException
	{
		connection.sendMessageAndFlush(new LCLobbyChanged(isAdmin, info));
	}

	void kick()
	{
		// send message that you are being kicked...
	}

	String getHostInfo()
	{
		return "192.168.0.100:728304";
	}
}
