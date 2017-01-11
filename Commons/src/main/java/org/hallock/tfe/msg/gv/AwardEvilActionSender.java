package org.hallock.tfe.msg.gv;

import java.io.IOException;

import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionContainer;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;

import com.fasterxml.jackson.core.JsonGenerator;

public class AwardEvilActionSender extends ClientSender
{
	EvilAction action;
	
	public AwardEvilActionSender(EvilAction action)
	{
		this.action = action;
	}
	
	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, action);
	}
	
	public static void send(JsonGenerator generator, EvilAction action) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.GAME_AWARD_EVIL_ACTION_TYPE);
		generator.writeFieldName("container");
		new EvilActionContainer(action).write(generator);
		generator.writeEndObject();
	}
}
