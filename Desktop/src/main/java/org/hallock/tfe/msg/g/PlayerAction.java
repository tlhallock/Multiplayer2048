package org.hallock.tfe.msg.g;

import java.io.IOException;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.Game;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class PlayerAction extends GameMessage
{
	PossiblePlayerActions swipe;

	public PlayerAction(PossiblePlayerActions showalltileboards)
	{
		this.swipe = showalltileboards;
	}
	
	public PlayerAction(JsonParser parser) throws IOException
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
				case "action":
					this.swipe = PossiblePlayerActions.valueOf(parser.getValueAsString());
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
		writer.writeStringField("action", swipe.name());
		writer.writeEndObject();
	}
	
	public static final String TYPE = "playeraction";

	@Override
	public void perform(Game server, PlayerConnection player) throws IOException
	{
		server.play(player.getPlayerNumber(), swipe);
	}
}
