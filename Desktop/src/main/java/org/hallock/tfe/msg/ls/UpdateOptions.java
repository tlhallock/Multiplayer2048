package org.hallock.tfe.msg.ls;

import java.io.IOException;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class UpdateOptions extends LobbyMessage
{
	GameOptions options;
	
	public UpdateOptions(GameOptions options)
	{
		this.options = options;
	}
	
	public UpdateOptions(JsonParser parser) throws IOException
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
	public void perform(Lobby lobby, PlayerConnection player) throws IOException
	{
		lobby.setOptions(player, options);
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
