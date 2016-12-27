/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.msg.LCLobbyChanged;

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
	boolean admin;

	public TileBoard board;
	public History history;

	public WaitingPlayer(Connection connection2)
	{
		this.connection = connection2;
	}
	
	public void updateLobby() throws IOException
	{
		connection.sendMessageAndFlush(new LCLobbyChanged(
				assignedPlayerNumber == 0,
				assignedLobby.getInfo()));
	}

	void kick()
	{
		// send message that you are being kicked...
	}

	String getHostInfo()
	{
		return "192.168.0.100:728304";
	}

	public void setReady(boolean ready2) throws IOException
	{
		if (ready == ready2)
			return;
		ready = ready2;
		if (assignedLobby != null)
			assignedLobby.changed();
	}

	public Lobby getLobby()
	{
		return assignedLobby;
	}
}
