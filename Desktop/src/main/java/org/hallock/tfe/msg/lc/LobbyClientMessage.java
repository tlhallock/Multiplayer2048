package org.hallock.tfe.msg.lc;

import java.io.IOException;

import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.msg.Message;

public abstract class LobbyClientMessage extends Message
{
	public abstract void perform(LobbyClient lobbyViewer) throws IOException;
}
