package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class SCreateLobby extends ServerMessage
{
	public SCreateLobby() {}

	public SCreateLobby(JsonParser parser) throws IOException
	{
		while (!parser.nextToken().equals(JsonToken.END_OBJECT))
			;
	}

	@Override
	public void perform(GameServer server, PlayerConnection player) throws IOException
	{
		server.createLobby(player);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, TYPE);
		generator.writeEndObject();
	}

	public static final String TYPE = "create_lobby";
}
