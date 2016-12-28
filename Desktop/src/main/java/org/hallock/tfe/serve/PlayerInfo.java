/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.serve.Lobby.PlayerPlaceHolder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 *
 * @author trever
 */
public class PlayerInfo implements Jsonable
{
	public PlayerSpec type;
	public String name;
	public String status;
	public int lobbyNumber;
	public int gameNumber;
	public boolean admin;

	public PlayerInfo(PlayerPlaceHolder p, int idx)
	{
		type = p.spec;
		lobbyNumber = idx;
		gameNumber = p.playerNumber;
		
		switch (p.spec)
		{
		case Computer:
			name = "Computer";
			status = "ready";
			admin = false;
			break;
		case HumanPlayer:
			if (p.connected == null)
			{
				name = "empty";
				status = "waiting";
				admin = false;
			}
			else
			{
				name = p.connected.playerName;
				status = p.connected.ready ? "ready" : "not ready";
				admin = p.connected.admin;
			}
			break;
		default:
			throw new RuntimeException("Uh oh");

		}
	}

	public PlayerInfo(JsonParser parser) throws IOException
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
				case "lnumber":
					lobbyNumber = parser.getIntValue();
					break;
				case "gnumber":
					gameNumber = parser.getIntValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_STRING:
				switch (currentName)
				{
				case "name":
					name = parser.getValueAsString();
					break;
				case "status":
					status = parser.getValueAsString();
					break;
				case "type":
					type = PlayerSpec.valueOf(parser.getValueAsString());
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_FALSE:
				switch (currentName)
				{
				case "admin":
					admin = false;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_TRUE:
				switch (currentName)
				{
				case "admin":
					admin = true;
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
	public void write(JsonGenerator writer) throws IOException
	{
		writer.writeStartObject();
		writer.writeStringField("type", type.name());
		writer.writeStringField("name", name);
		writer.writeStringField("status", status);
		writer.writeNumberField("lnumber", lobbyNumber);
		writer.writeNumberField("gnumber", gameNumber);
		writer.writeBooleanField("admin", admin);
		writer.writeEndObject();
	}
}
