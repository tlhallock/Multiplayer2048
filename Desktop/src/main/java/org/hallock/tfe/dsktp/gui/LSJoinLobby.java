package org.hallock.tfe.dsktp.gui;

import java.io.IOException;

import org.hallock.tfe.msg.LSLobbyServerMessage;
import org.hallock.tfe.serve.LobbyServer;
import org.hallock.tfe.serve.WaitingPlayer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LSJoinLobby extends LSLobbyServerMessage
{
	String id;

	public LSJoinLobby(String id)
	{
		this.id = id;
	}
	
	public LSJoinLobby(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case VALUE_STRING:
				switch (currentName)
				{
				case "id":
					id = parser.getValueAsString();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			default:
				throw new RuntimeException("Unexpected.");
			}
		}
	}
	
	@Override
	public void perform(LobbyServer server, WaitingPlayer player) throws IOException
	{
		server.addUserToLobby(player, id);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeStringField("id", id);
		generator.writeEndObject();
	}

	public static final String TYPE = "join_lobby";
}
