package org.hallock.tfe.msg.gc;

import java.io.IOException;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.serve.GameUpdateInfo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LaunchGame extends GameClientMessage
{
	int playerNumber;
	GameUpdateInfo info;
	
	public LaunchGame(int playerNumber, GameUpdateInfo info)
	{
		this.playerNumber = playerNumber;
		this.info = info;
	}
	
	public LaunchGame(JsonParser parser) throws IOException
	{
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
				case "number":
					playerNumber = parser.getNumberValue().intValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_OBJECT:
				switch (currentName)
				{
				case "info":
					info = new GameUpdateInfo(parser);
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
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeNumberField("number", playerNumber);
		generator.writeFieldName("info");
		info.write(generator);
		generator.writeEndObject();
	}

	public static final String TYPE = "open_game";

	@Override
	public void perform(ClientConnection client) throws IOException
	{
		client.launchGameGui(playerNumber, info);
	}
}
