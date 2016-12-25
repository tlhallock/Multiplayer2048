package org.hallock.tfe.msg;

import org.hallock.tfe.serve.GameServer;

public abstract class ServerMessage extends Message
{
	public abstract void perform(int playerNum, GameServer server);
}
