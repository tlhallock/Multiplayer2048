package org.hallock.tfe.client;

import org.hallock.tfe.msg.MessageReader;
import org.hallock.tfe.msg.gc.LaunchGameHandler;
import org.hallock.tfe.msg.gv.AwardIEvilActionHandler;
import org.hallock.tfe.msg.gv.StateChangedHandler;
import org.hallock.tfe.msg.lc.KickHandler;
import org.hallock.tfe.msg.lc.LobbiesListHandler;
import org.hallock.tfe.msg.lc.LobbyInfoHandler;

public class ClientReader extends MessageReader
{
	ClientConnection connection;
	
	public ClientReader(ClientConnection clientConnection)
	{
		this.connection = clientConnection;
	}
	
	public void viewLobbies(LobbyClient viewer)
	{
		removeAllTypes();
		
		add(new KickHandler        (viewer));
		add(new LobbiesListHandler (viewer));
		add(new LobbyInfoHandler   (viewer));

		add(new LaunchGameHandler(connection));
	}
	
	public void viewGame(GameViewer viewer)
	{
		removeAllTypes();
		
		add(new AwardIEvilActionHandler (viewer));
		add(new StateChangedHandler	(viewer));
	}
}
