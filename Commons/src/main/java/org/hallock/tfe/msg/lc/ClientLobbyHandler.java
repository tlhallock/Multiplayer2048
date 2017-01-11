package org.hallock.tfe.msg.lc;

import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.msg.MessageHandler;

public abstract class ClientLobbyHandler extends MessageHandler
{
	LobbyClient lobbyViewer;

	protected ClientLobbyHandler(LobbyClient lobbyViewer)
	{
		this.lobbyViewer = lobbyViewer;
	}
}
