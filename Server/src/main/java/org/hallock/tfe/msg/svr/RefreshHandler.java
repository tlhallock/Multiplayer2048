package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonParser;

public class RefreshHandler extends LobbyHandler
{
	protected RefreshHandler(Lobby server, PlayerConnection connection)
	{
		super(server, connection);
	}

	@Override
	public String getType()
	{
		return Message.LOBBY_REFRESH_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		SimpleParser.finishObject(parser);
		lobby.refresh(connection);
	}
}
