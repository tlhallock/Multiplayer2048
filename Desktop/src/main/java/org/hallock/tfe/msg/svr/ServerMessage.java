package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.PlayerConnection;

public abstract class ServerMessage extends Message
{
	public abstract void perform(GameServer server, PlayerConnection player) throws IOException;
}
