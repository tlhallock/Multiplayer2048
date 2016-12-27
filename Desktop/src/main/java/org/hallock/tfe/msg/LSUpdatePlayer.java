package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.LobbyServer;
import org.hallock.tfe.serve.PlayerInfo;
import org.hallock.tfe.serve.WaitingPlayer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LSUpdatePlayer extends LSLobbyServerMessage
{
	int playerNumber;
	UpdateAction action;
	
	public LSUpdatePlayer(PlayerInfo player, UpdateAction action)
	{
		playerNumber = player.playerNumber;
		this.action = action;
	}

	public LSUpdatePlayer(JsonParser parser) throws IOException
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
	public void perform(LobbyServer server, WaitingPlayer player) throws IOException
	{
		Lobby lobby = player.getLobby();
		if (lobby != null)
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
