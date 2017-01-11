
package org.hallock.tfe.gui;

import java.io.IOException;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.sys.GameUpdateInfo;

public interface ClientGuiConstructor
{
	public LobbyClient showLobbyBrowser(ClientConnection connection);
	public GameViewer launchGameGui(ClientConnection clientConnection, int playerNumber, GameUpdateInfo info) throws IOException;
}
