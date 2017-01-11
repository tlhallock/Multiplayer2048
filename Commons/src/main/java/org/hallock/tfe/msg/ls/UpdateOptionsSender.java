package org.hallock.tfe.msg.ls;

import java.io.IOException;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;

import com.fasterxml.jackson.core.JsonGenerator;


public class UpdateOptionsSender extends ClientSender
{
	GameOptions options;
	
	public UpdateOptionsSender(GameOptions options)
	{
		this.options = options;
	}

	@Override
	public void sendMessage(JsonGenerator generator) throws IOException
	{
		send(generator, options);
	}
	
	public static void send(JsonGenerator generator, GameOptions options) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, Message.LOBBY_UPDATE_OPTIONS_TYPE);
		generator.writeFieldName("options");
		options.write(generator);
		generator.writeEndObject();
	}
}
