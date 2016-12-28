package org.hallock.tfe.msg.gc;

import java.io.IOException;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.msg.Message;

public abstract class GameClientMessage extends Message
{
	public abstract void perform(ClientConnection client) throws IOException;
}
