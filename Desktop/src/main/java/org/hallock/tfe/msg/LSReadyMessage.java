package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.serve.LobbyServer;
import org.hallock.tfe.serve.WaitingPlayer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LSReadyMessage extends LSLobbyServerMessage
{
	boolean ready;

	public LSReadyMessage(boolean ready)
	{
		this.ready = ready;
	}
	
	public LSReadyMessage(JsonParser parser) throws IOException
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
				case "ready":
					ready = false;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_TRUE:
				switch (currentName)
				{
				case "ready":
					ready = true;
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
		player.setReady(ready);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeBooleanField("ready", ready);
		generator.writeEndObject();
	}

	public static final String TYPE = "status_update";
}
