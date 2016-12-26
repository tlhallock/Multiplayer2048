package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.client.GameClient;
import org.hallock.tfe.cmn.game.TileBoard;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class GCGameStateChanged extends GCClientMessage
{
	int playerNumber;
	TileBoard board;
	
	public GCGameStateChanged(int n, TileBoard b)
	{
		playerNumber = n;
		board = new TileBoard(b);
	}
	
	public GCGameStateChanged(JsonParser parser) throws IOException
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
				case "playernum":
					playerNumber = parser.getNumberValue().intValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_OBJECT:
				switch (currentName)
				{
				case "board":
					board = new TileBoard(parser);
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
		writer.writeStringField(Message.TYPE_FIELD, TYPE);
		writer.writeNumberField("playernum", playerNumber);
		writer.writeFieldName("board");
		board.write(writer);
		writer.writeEndObject();
	}

	@Override
	public void perform(GameClient client)
	{
		client.updatePlayer(playerNumber, board);
	}


	public static final String TYPE = "state_change";
}
