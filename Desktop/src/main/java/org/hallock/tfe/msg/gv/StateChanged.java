package org.hallock.tfe.msg.gv;

import java.io.IOException;

import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.GameUpdateInfo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class StateChanged extends GameViewerMessage
{
	GameUpdateInfo info;
	
	public StateChanged(GameUpdateInfo info)
	{
		this.info = info;
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
			case START_OBJECT:
				switch (currentName)
				{
				case "info":
					info = new GameUpdateInfo(parser);
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
		writer.writeFieldName("info");
		info.write(writer);
		writer.writeEndObject();
	}

	@Override
	public void perform(GameViewer client)
	{
		client.updateInfo(info);
	}


	public static final String TYPE = "state_change";
}
