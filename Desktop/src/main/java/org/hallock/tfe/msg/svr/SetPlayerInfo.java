package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class SetPlayerInfo extends ServerMessage
{
	String name;

	public SetPlayerInfo(String name)
	{
		this.name = Utils.createRandomString(10);
	}
	
	public SetPlayerInfo(JsonParser parser) throws IOException
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
				case "name":
					name = parser.getValueAsString();
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
	public void perform(GameServer server, PlayerConnection player) throws IOException
	{
		player.setName(name);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeStringField("name", name);
		generator.writeEndObject();
	}

	public static final String TYPE = "set_player_info";
}
