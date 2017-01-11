package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.MessageSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class SetPlayerInfoSender extends MessageSender
{
	private String name;

	public SetPlayerInfoSender(String name)
	{
		this.name = name;
	}
	
	public static void send(JsonGenerator generator, String name) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.SERVER_SET_PLAYER_INFO_TYPE);
		generator.writeStringField("name", name);
		generator.writeEndObject();
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, name);
	}

}
