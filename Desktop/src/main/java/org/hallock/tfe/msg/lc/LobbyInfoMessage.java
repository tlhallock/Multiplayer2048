package org.hallock.tfe.msg.lc;

import java.io.IOException;

import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.Lobby.LobbyInfo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LobbyInfoMessage extends LobbyClientMessage
{
	boolean isAdmin;
	LobbyInfo info;
	
	public LobbyInfoMessage(boolean isAdmin, LobbyInfo info)
	{
		this.isAdmin = isAdmin;
		this.info = info;
	}
	
	public LobbyInfoMessage(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case VALUE_FALSE:
				switch (currentName)
				{
				case "isAdmin":
					isAdmin = false;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_TRUE:
				switch (currentName)
				{
				case "isAdmin":
					isAdmin = true;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_OBJECT:
				switch (currentName)
				{
				case "info":
					info = new LobbyInfo(parser);
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
	public void perform(LobbyClient lobbyViewer)
	{
		lobbyViewer.setLobby(isAdmin, info);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, TYPE);
		generator.writeBooleanField("isAdmin", isAdmin);
		generator.writeFieldName("info");
		info.write(generator);
		generator.writeEndObject();
	}
	
	
	public static final String TYPE = "lobby_changed";
}
