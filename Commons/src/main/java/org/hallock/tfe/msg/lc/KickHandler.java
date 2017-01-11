package org.hallock.tfe.msg.lc;

import java.io.IOException;

import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;

import com.fasterxml.jackson.core.JsonParser;

public class KickHandler extends ClientLobbyHandler
{
	public KickHandler(LobbyClient lobbyViewer)
	{
		super(lobbyViewer);
	}

	@Override
	public String getType()
	{
		return Message.LOBBY_CLIENT_KICK_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		SimpleParser.finishObject(parser);
		lobbyViewer.setLobby(false, null);
	}

}
