package org.hallock.tfe.msg.gv;

import java.io.IOException;

import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.Game.InGamePlayer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class StateChanged extends GameViewerMessage
{
	int playerNumber;
	TileBoard board;
	TileChanges changes;
	int turnId;
	
	public StateChanged(InGamePlayer player)
	{
		playerNumber = player.getPlayerNumber();
		board = new TileBoard(player.getBoard());
		changes = player.getChanges();
		turnId = player.getTurnId();
	}

	public StateChanged(JsonParser parser) throws IOException
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
				case "turnId":
					turnId = parser.getNumberValue().intValue();
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
				case "how":
					changes = new TileChanges(parser);
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_NULL:
				switch (currentName)
				{
				case "how":
					changes = null;
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
		writer.writeNumberField("turnId", turnId);
		writer.writeFieldName("board");
		board.write(writer);
		if (changes == null)
		{
			writer.writeNullField("how");
		}
		else
		{
			writer.writeFieldName("how");
			changes.write(writer);
		}
		writer.writeEndObject();
	}

	@Override
	public void perform(GameViewer client)
	{
		client.updatePlayer(playerNumber, board, changes, turnId);
	}


	public static final String TYPE = "state_change";
}
