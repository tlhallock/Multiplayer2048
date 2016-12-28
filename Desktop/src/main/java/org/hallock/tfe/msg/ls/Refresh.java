package org.hallock.tfe.msg.ls;

import java.io.IOException;

import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Refresh extends LobbyMessage
{
	public Refresh() {}
	public Refresh(JsonParser parser) throws IOException
	{
		while (!parser.nextToken().equals(JsonToken.END_OBJECT))
			;
	}

	@Override
	public void perform(Lobby server, PlayerConnection player) throws IOException
	{
		player.updateLobby();
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeEndObject();
	}

	public static final String TYPE = "refresh";
}
