package org.hallock.tfe.cmn.game.evil;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AddHighTile extends EvilAction
{
	int highTile;
	
	public AddHighTile(int ht)
	{
		this.highTile = ht;
	}

	public AddHighTile(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (next = parser.nextToken())
			{
			case VALUE_NUMBER_INT:
				switch (currentName)
				{
				case "high_tile":
					highTile = parser.getValueAsInt();
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
		generator.writeNumberField("high_tile", highTile);
		generator.writeEndObject();
	}

	@Override
	public EvilActionType getType()
	{
		return EvilActionType.AddHighTile;
	}

	public int getHighTile()
	{
		return highTile;
	}

	@Override
	boolean optionsAreEqual(EvilAction other)
	{
		return highTile == ((AddHighTile) other).highTile;
	}

}
