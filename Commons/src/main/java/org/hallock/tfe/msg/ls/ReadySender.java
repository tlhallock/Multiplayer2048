package org.hallock.tfe.msg.ls;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class ReadySender extends ClientSender
{
	private boolean ready;
	
	public ReadySender(boolean ready)
	{
		this.ready = ready;
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, ready);
	}

	public static void send(JsonGenerator generator, boolean ready) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.LOBBY_READY_TYPE);
		generator.writeBooleanField("ready", ready);
		generator.writeEndObject();
	}
}
