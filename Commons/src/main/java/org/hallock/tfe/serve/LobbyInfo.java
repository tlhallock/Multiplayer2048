package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.msg.SimpleParser.ObjectReader;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LobbyInfo implements Jsonable
{
	public static ObjectReader<LobbyInfo> READER = new ObjectReader<LobbyInfo>() {
		@Override
		public LobbyInfo parse(JsonParser parser) throws IOException
		{
			return new LobbyInfo(parser);
		}};
	
	public boolean allReady;
	public GameOptions options;
	public WaitingPlayerInfo[] players;
	public String id;

	public LobbyInfo() {}

	public LobbyInfo(JsonParser parser) throws IOException
	{
		int numPlayers = -1;
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (next = parser.nextToken())
			{
			case VALUE_NULL:
				break;
			case VALUE_FALSE:
				switch (currentName)
				{
				case "all_ready":
					allReady = false; 
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_TRUE:
				switch (currentName)
				{
				case "all_ready":
					allReady = true; 
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
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
			case VALUE_NUMBER_INT:
				switch (currentName)
				{
				case "numplayers":
					numPlayers = parser.getNumberValue().intValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
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
			case START_ARRAY:
				switch (currentName)
				{
				case "players":
					players = new WaitingPlayerInfo[numPlayers];
					int index = 0;
					while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
					{
						switch (next)
						{
						case START_OBJECT:
							players[index++] = new WaitingPlayerInfo(parser);
							break;
						default:
							throw new RuntimeException("Unexpected.");
						}
					}
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			default:
				throw new RuntimeException("Unexpected: " + next);
			}
		}
	}

	@Override
	public void write(JsonGenerator writer) throws IOException
	{
		writer.writeStartObject();
		writer.writeStringField("id", id);
		writer.writeBooleanField("all_ready", allReady);
		writer.writeFieldName("options");
		options.write(writer);
		writer.writeNumberField("numplayers", players.length);
		writer.writeFieldName("players");
		writer.writeStartArray();
		for (WaitingPlayerInfo player : players)
			player.write(writer);
		writer.writeEndArray();
		writer.writeEndObject();
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return "a name";
	}
}
