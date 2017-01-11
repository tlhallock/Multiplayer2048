package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.msg.Message;

import com.fasterxml.jackson.core.JsonGenerator;

public class JoinLobbySender extends ClientSender
{
	private String lobbyId;
	
	public JoinLobbySender(String lobbyId)
	{
		this.lobbyId = lobbyId;
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, lobbyId);
	}

	public static void send(JsonGenerator generator, String lobbyId) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.SERVER_JOIN_LOBBY_TYPE);
		generator.writeStringField("id", lobbyId);
		generator.writeEndObject();
	}
}
