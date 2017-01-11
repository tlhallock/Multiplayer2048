package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.MessageSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class ListLobbiesSender extends MessageSender
{
	public static ListLobbiesSender SENDER = new ListLobbiesSender();
	
	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator);
	}
	
	public static void send(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.SERVER_LIST_LOBBIES_TYPE);
		generator.writeEndObject();		
	}
}
