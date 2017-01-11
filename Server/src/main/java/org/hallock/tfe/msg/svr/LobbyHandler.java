package org.hallock.tfe.msg.svr;

import org.hallock.tfe.msg.MessageHandler;
import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

public abstract class LobbyHandler extends MessageHandler
{
	Lobby lobby;
	PlayerConnection connection;
	
	protected LobbyHandler(Lobby server, PlayerConnection connection)
	{
		this.lobby = server;
		this.connection = connection;
	}
}
