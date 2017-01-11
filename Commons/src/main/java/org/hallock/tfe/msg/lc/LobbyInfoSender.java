package org.hallock.tfe.msg.lc;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;
import org.hallock.tfe.serve.LobbyInfo;

import com.fasterxml.jackson.core.JsonGenerator;

public class LobbyInfoSender extends ClientSender
{
	boolean isHost;
	LobbyInfo info;
	
	public LobbyInfoSender(boolean isAdmin, LobbyInfo info)
	{
		this.isHost = isAdmin;
		this.info = info;
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, isHost, info);
	}
	
	public static void send(JsonGenerator generator, boolean isHost, LobbyInfo info) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.CLIENT_LOBBY_INFO_TYPE);
		generator.writeBooleanField("isHost", isHost);
		generator.writeFieldName("info");
		info.write(generator);
		generator.writeEndObject();
	}
}
