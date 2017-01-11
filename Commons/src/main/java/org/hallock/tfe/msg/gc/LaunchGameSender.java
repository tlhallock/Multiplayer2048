package org.hallock.tfe.msg.gc;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;
import org.hallock.tfe.sys.GameUpdateInfo;

import com.fasterxml.jackson.core.JsonGenerator;

public class LaunchGameSender extends ClientSender
{
	int playerNumber;
	GameUpdateInfo info;
	
	public LaunchGameSender(int playerNumber, GameUpdateInfo info)
	{
		super();
		this.playerNumber = playerNumber;
		this.info = info;
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, playerNumber, info);
	}
	
	public static void send(JsonGenerator writer, int playerNumber, GameUpdateInfo info) throws IOException
	{
		writer.writeStartObject();
		writer.writeStringField(Message.TYPE_FIELD, Message.CLIENT_CONNECTION_LAUNCH_TYPE);
		writer.writeNumberField("number", playerNumber);
		writer.writeFieldName("info");
		info.write(writer);
		writer.writeEndObject();
	}
}
