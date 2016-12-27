/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.tfe.serve;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 *
 * @author trever
 */
public class PlayerInfo
{
	public PlayerSpec type;
	public String name;
	public String status;
	public int playerNumber;

	public PlayerInfo() {}

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
				case "number":
					playerNumber = parser.getIntValue();
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
			default:
				throw new RuntimeException("Unexpected.");
			}
		}
	}

	public void print(JsonGenerator writer) throws IOException
	{
		writer.writeStartObject();
		writer.writeStringField("type", type.name());
		writer.writeStringField("name", name);
		writer.writeStringField("status", status);
		writer.writeNumberField("number", playerNumber);
		writer.writeEndObject();
	}
}
