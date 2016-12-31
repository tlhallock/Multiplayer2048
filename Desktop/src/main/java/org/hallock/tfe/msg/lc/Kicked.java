package org.hallock.tfe.msg.lc;

import java.io.IOException;

import org.hallock.tfe.client.LobbyClient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Kicked extends LobbyClientMessage
{
	public Kicked() {}
	
	public Kicked(JsonParser parser) throws IOException
	{
		while (!parser.nextToken().equals(JsonToken.END_OBJECT))
			;
	}

	@Override
	public void perform(LobbyClient lobbyViewer) throws IOException
	{
		lobbyViewer.setLobby(false, null);
	}
	
	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeEndObject();
	}

	public static final String TYPE = "kicked";
}
