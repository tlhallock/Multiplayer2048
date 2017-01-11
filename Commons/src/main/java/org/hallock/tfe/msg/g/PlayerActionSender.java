package org.hallock.tfe.msg.g;

import java.io.IOException;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class PlayerActionSender extends ClientSender
{
	PossiblePlayerActions swipe;
	
	public PlayerActionSender(PossiblePlayerActions swipe)
	{
		this.swipe = swipe;
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, swipe);
	}

	public static void send(JsonGenerator writer, 
			PossiblePlayerActions swipe) throws IOException
	{
		writer.writeStartObject();
		writer.writeStringField(Message.TYPE_FIELD, Message.GAME_SERVER_PLAY_TYPE);
		writer.writeStringField("action", swipe.name());
		writer.writeEndObject();
	}
}
