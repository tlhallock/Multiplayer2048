package org.hallock.tfe.msg.gv;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;
import org.hallock.tfe.sys.GameUpdateInfo;

import com.fasterxml.jackson.core.JsonGenerator;

public class StateChangedSender extends ClientSender
{
	GameUpdateInfo info;
	
	public StateChangedSender(GameUpdateInfo info)
	{
		this.info = info;
	}

	@Override
	public void sendMessage(JsonGenerator writer) throws IOException
	{
		send(writer, info);
	}
	
	public static void send(JsonGenerator writer, GameUpdateInfo info) throws IOException
	{
		writer.writeStartObject();
		writer.writeStringField(Message.TYPE_FIELD, Message.GAME_STATE_CHANGED_TYPE);
		writer.writeFieldName("info");
		info.write(writer);
		writer.writeEndObject();
	}
}
