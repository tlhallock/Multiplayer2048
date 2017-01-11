package org.hallock.tfe.msg.gc;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.msg.MessageHandler;

public abstract class ClientConnectHandler extends MessageHandler
{
	ClientConnection client;

	public ClientConnectHandler(ClientConnection client)
	{
		this.client = client;
	}
}
