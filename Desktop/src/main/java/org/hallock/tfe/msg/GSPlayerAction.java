package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.serve.GameServer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class GSPlayerAction extends GSServerMessage
{
	PossiblePlayerActions swipe;

	public GSPlayerAction(PossiblePlayerActions showalltileboards)
	{
		this.swipe = showalltileboards;
	}
	
	public GSPlayerAction(JsonParser parser) throws IOException
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
	public void perform(int playerNum, GameServer server) throws IOException
	{
		server.play(playerNum, swipe);
	}
}
