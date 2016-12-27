package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.serve.LobbyServer;
import org.hallock.tfe.serve.WaitingPlayer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LSUpdateOptions extends LSLobbyServerMessage
{
	GameOptions options;
	
	public LSUpdateOptions(GameOptions options)
	{
		this.options = options;
	}
	
	LSUpdateOptions(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case START_OBJECT:
				switch (currentName)
				{
				case "options":
					options = new GameOptions(parser);
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
		
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeFieldName("options");
		options.print(generator);
		generator.writeEndObject();
	}
	
	public static final String TYPE = "update_options";
}
