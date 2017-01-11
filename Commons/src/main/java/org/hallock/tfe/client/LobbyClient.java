package org.hallock.tfe.client;

import java.util.Collection;

import org.hallock.tfe.serve.LobbyInfo;

public interface LobbyClient
{
	void setLobbies(Collection<LobbyInfo> arrayList);
	void setLobby(boolean isAdmin, LobbyInfo info);
	void setConnectionStatus(String name);
	void hideViewer();
	void view();
}
