package org.hallock.tfe.msg.ls;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

public abstract class LobbyMessage extends Message
{
	public abstract void perform(Lobby lobby, PlayerConnection player) throws IOException;
}
