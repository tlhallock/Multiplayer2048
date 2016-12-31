package org.hallock.tfe.msg.gv;

import java.io.IOException;

import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionContainer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AwardedEvilAction extends GameViewerMessage
{
	EvilActionContainer action;
	
	public AwardedEvilAction(EvilAction action)
	{
		this.action = new EvilActionContainer(action);
	}
	
	public AwardedEvilAction(JsonParser parser) throws IOException
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
				case "container":
					action = new EvilActionContainer(parser);
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
		generator.writeFieldName("container");
		action.write(generator);
		generator.writeEndObject();
		
	}

	@Override
	public void perform(GameViewer client) throws IOException
	{
		client.award(action.getAction());
	}

	public static final String TYPE = "awarded_action";
}
