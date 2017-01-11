package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;

import com.fasterxml.jackson.core.JsonGenerator;

public class CreateLobbySender extends ClientSender
{
	public static CreateLobbySender SENDER = new CreateLobbySender();
	
	public static void send(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.SERVER_CREATE_LOBBY_TYPE);
		generator.writeEndObject();
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator);
	}
}
