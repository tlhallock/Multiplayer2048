package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonParser;

public class ReadyHandler extends LobbyHandler
{
	protected ReadyHandler(Lobby server, PlayerConnection connection)
	{
		super(server, connection);
	}

	@Override
	public String getType()
	{
		return Message.LOBBY_READY_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		
		SimpleKnownValue<Boolean> value = handler.listenForBoolean("/ready");
		
		SimpleParser.parseAllOfCurrentObject(handler, parser);
		
		lobby.playerIsReady(connection, value.getValue());
	}
}
