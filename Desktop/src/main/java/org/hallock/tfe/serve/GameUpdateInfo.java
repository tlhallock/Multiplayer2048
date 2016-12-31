package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.LinkedList;

import org.hallock.tfe.cmn.util.Jsonable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class GameUpdateInfo implements Jsonable
{
	public LinkedList<GamePlayerInfo> changedPlayers = new LinkedList<>();
	
	GameUpdateInfo()
	{
		
	}

	public void addPlayer(InGamePlayer player)
	{
		changedPlayers.add(new GamePlayerInfo(player));
	}
	
	public GameUpdateInfo(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case START_ARRAY:
				switch (currentName)
				{
				case "changed_players":
					while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
					{
						switch (next)
						{
						case START_OBJECT:
							changedPlayers.add(new GamePlayerInfo(parser));
							break;
						default:
							throw new RuntimeException("Unexpected: " + next);
						}
					}
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
	public void write(JsonGenerator writer) throws IOException
	{
		writer.writeStartObject();
		writer.writeFieldName("changed_players");
		writer.writeStartArray();
		for (GamePlayerInfo info : changedPlayers)
			info.write(writer);
		writer.writeEndArray();
		writer.writeEndObject();
	}


	
}
