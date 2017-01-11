package org.hallock.tfe.msg.lc;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class KickSender extends ClientSender
{
	public static KickSender SENDER = new KickSender();

	@Override
	public void sendMessage(JsonGenerator writer) throws IOException
	{
		send(writer);
	}

	public static void send(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.LOBBY_CLIENT_KICK_TYPE);
		generator.writeEndObject();
	}
}
