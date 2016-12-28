package org.hallock.tfe.msg.lc;

import java.io.IOException;
import java.util.LinkedList;

import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.Lobby.LobbyInfo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LobbiesList extends LobbyClientMessage
{
	LinkedList<LobbyInfo> lobbies = new LinkedList<>();
	
	public LobbiesList() {}

	public LobbiesList(JsonParser parser) throws IOException
	{
		int size = -1;

		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case VALUE_NUMBER_INT:
				switch (currentName)
				{
				case "num":
					size = parser.getNumberValue().intValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_ARRAY:
				while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
				{
					switch (next)
					{
					case START_OBJECT:
						lobbies.add(new LobbyInfo(parser));
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
				}
				break;
			default:
				throw new RuntimeException("Unexpected.");
			}
		}

	}

	public void foundLobby(LobbyInfo info)
	{
		lobbies.add(info);
	}

	@Override
	public void write(JsonGenerator writer) throws IOException
	{
		writer.writeStartObject();
		writer.writeStringField(Message.TYPE_FIELD, TYPE);
		writer.writeNumberField("num", lobbies.size());
		writer.writeFieldName("lobbies");
		writer.writeStartArray();
		for (LobbyInfo info : lobbies)
			info.write(writer);
		writer.writeEndArray();
		writer.writeEndObject();
	}

	public static final String TYPE = "lobbieslist";

	@Override
	public void perform(LobbyClient lobbyViewer)
	{
		lobbyViewer.setLobbies(lobbies);
	}
}
