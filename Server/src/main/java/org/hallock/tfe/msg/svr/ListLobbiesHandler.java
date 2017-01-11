package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonParser;

public class ListLobbiesHandler extends ServerHandler
{
	protected ListLobbiesHandler(GameServer server, PlayerConnection connection)
	{
		super(server, connection);
	}

	@Override
	public String getType()
	{
		return Message.SERVER_LIST_LOBBIES_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		SimpleParser.finishObject(parser);
		server.listLobbies(connection);		
	}
}
