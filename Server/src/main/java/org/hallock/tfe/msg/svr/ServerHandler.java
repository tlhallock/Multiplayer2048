package org.hallock.tfe.msg.svr;

import org.hallock.tfe.msg.MessageHandler;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.PlayerConnection;

public abstract class ServerHandler extends MessageHandler
{
	GameServer server;
	PlayerConnection connection;
	
	protected ServerHandler(GameServer server, PlayerConnection connection)
	{
		this.server = server;
		this.connection = connection;
	}
}
