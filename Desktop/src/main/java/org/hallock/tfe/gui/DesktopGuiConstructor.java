package org.hallock.tfe.gui;

import java.io.IOException;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.dsktp.gui.DesktopGameViewer;
import org.hallock.tfe.sys.GameUpdateInfo;

public class DesktopGuiConstructor implements ClientGuiConstructor
{
	@Override
	public LobbyClient showLobbyBrowser(ClientConnection connection)
	{
		return null;
	}

	@Override
	public GameViewer launchGameGui(ClientConnection clientConnection, int playerNumber, GameUpdateInfo info) throws IOException
	{
		return DesktopGameViewer.launchGameGui(clientConnection, playerNumber, info);
	}
}
