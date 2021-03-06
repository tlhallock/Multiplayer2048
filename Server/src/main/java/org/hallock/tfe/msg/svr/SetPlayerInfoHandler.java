package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonParser;

public class SetPlayerInfoHandler extends ServerHandler
{
	protected SetPlayerInfoHandler(GameServer server, PlayerConnection connection)
	{
		super(server, connection);
	}

	@Override
	public String getType()
	{
		return Message.SERVER_SET_PLAYER_INFO_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		SimpleKnownValue<String> value = handler.listenForString("/name");
		SimpleParser.parseAllOfCurrentObject(handler, parser);
		connection.setName(value.getValue());
	}
}
