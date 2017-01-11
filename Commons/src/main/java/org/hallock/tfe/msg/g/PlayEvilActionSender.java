package org.hallock.tfe.msg.g;

import java.io.IOException;

import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionContainer;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class PlayEvilActionSender extends ClientSender
{
	EvilAction action;
	int otherPlayer;
	
	public PlayEvilActionSender(EvilAction swipe, int otherPlayer)
	{
		this.action = swipe;
		this.otherPlayer = otherPlayer;
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, action, otherPlayer);
	}

	public static void send(JsonGenerator generator, 
			EvilAction action,
			int otherPlayer) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD,
				Message.GAME_SERVER_PLAY_ACTION_TYPE);
		generator.writeNumberField("player", otherPlayer);
		generator.writeFieldName("container");
		new EvilActionContainer(action).write(generator);
		generator.writeEndObject();
	
	}
}
