package org.hallock.tfe.msg.g;

import java.io.IOException;

import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionContainer;
import org.hallock.tfe.serve.Game;
import org.hallock.tfe.serve.PlayerConnection;
import org.hallock.tfe.serve.PlayerConnection.PlayerRole;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class PlayEvilAction extends GameMessage
{
	EvilActionContainer action;
	int player;
	
	public PlayEvilAction(EvilAction action, int player)
	{
		this.player = player;
		this.action = new EvilActionContainer(action);
	}
	
	public PlayEvilAction(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case VALUE_NUMBER_INT:
				switch (currentName)
				{
				case "player":
					player = parser.getIntValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_OBJECT:
				switch (currentName)
				{
				case "container":
					action = new EvilActionContainer(parser);
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			default:
				throw new RuntimeException("Unexpected.");
			}
		}
	
	}
	
	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeNumberField("player", player);
		generator.writeFieldName("container");
		action.write(generator);
		generator.writeEndObject();
	}

	@Override
	public void perform(Game server, PlayerConnection player) throws IOException
	{
		if (!PlayerRole.isInGame(player))
			return;
		server.playAction(player.getRole().getIndex(), action.getAction(), this.player);
	}

	public static final String TYPE = "evil action";
}
