package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.serve.LobbyServer;
import org.hallock.tfe.serve.WaitingPlayer;

public abstract class LSLobbyServerMessage extends Message
{
	public abstract void perform(LobbyServer server, WaitingPlayer player) throws IOException;
}
