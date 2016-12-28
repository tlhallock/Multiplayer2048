package org.hallock.tfe.msg.ls;

import java.io.IOException;

import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;
import org.hallock.tfe.serve.PlayerInfo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class UpdatePlayer extends LobbyMessage
{
	int playerNumber;
	UpdateAction action;
	
	public UpdatePlayer(PlayerInfo player, UpdateAction action)
	{
		playerNumber = player.lobbyNumber;
		this.action = action;
	}

	public UpdatePlayer(JsonParser parser) throws IOException
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
				case "number":
					playerNumber = parser.getNumberValue().intValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_STRING:
				switch (currentName)
				{
				case "action":
					action = UpdateAction.valueOf(parser.getValueAsString());
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
	public void perform(Lobby lobby, PlayerConnection player) throws IOException
	{
		lobby.performAction(action, playerNumber, player);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeNumberField("number", playerNumber);
		generator.writeStringField("action", action.name());
		generator.writeEndObject();
	}

	public static final String TYPE = "update_player";
	
	public enum UpdateAction
	{
		Kick,
		SetComputer,
		SetHuman,
	}
}
