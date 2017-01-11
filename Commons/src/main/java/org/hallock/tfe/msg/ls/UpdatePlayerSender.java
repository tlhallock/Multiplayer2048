package org.hallock.tfe.msg.ls;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.ls.UpdatePlayer.UpdateAction;
import org.hallock.tfe.msg.svr.ClientSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class UpdatePlayerSender extends ClientSender
{
	int playerNumber;
	UpdateAction action;
	
	public UpdatePlayerSender(int playerNumber, UpdateAction action)
	{
		this.playerNumber = playerNumber;
		this.action = action;
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, playerNumber, action);
	}
	
	public static void send(JsonGenerator generator, int number, UpdateAction action) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.LOBBY_UPDATE_PLAYER_TYPE);
		generator.writeNumberField("number", number);
		generator.writeStringField("action", action.name());
		generator.writeEndObject();
	}
}
