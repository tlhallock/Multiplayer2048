package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.dsktp.gui.LobbyViewer;

public abstract class LCLobbyClientMessage extends Message
{
	public abstract void perform(LobbyViewer lobbyViewer) throws IOException;
}
