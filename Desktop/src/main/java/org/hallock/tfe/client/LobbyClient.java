package org.hallock.tfe.client;

import java.util.LinkedList;

import org.hallock.tfe.serve.LobbyInfo;

public interface LobbyClient
{
	void setLobbies(LinkedList<LobbyInfo> lobbies);
	void setLobby(boolean isAdmin, LobbyInfo info);
	void setConnectionStatus(String name);
	void hideViewer();
}
