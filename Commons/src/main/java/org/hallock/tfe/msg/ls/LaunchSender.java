package org.hallock.tfe.msg.ls;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class LaunchSender extends ClientSender
{
	public static LaunchSender SENDER = new LaunchSender();
	
	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator);
	}
	
	public static void send(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.LOBBY_LAUNCH_TYPE);
		generator.writeEndObject();
	}
}
