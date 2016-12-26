package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.serve.GameServer;

public abstract class GSServerMessage extends Message
{
	public abstract void perform(int playerNum, GameServer server) throws IOException;
}
