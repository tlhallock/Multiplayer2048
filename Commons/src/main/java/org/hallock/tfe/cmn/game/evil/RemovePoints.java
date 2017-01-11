package org.hallock.tfe.cmn.game.evil;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class RemovePoints extends EvilAction
{
	BigDecimal numToRemove;
	
	public RemovePoints(BigDecimal d)
	{
		this.numToRemove = d;
	}

	public RemovePoints(JsonParser parser) throws IOException
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
				case "remove":
					numToRemove = parser.getDecimalValue();
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
		generator.writeNumberField("remove", numToRemove);
		generator.writeEndObject();
	}

	@Override
	public EvilActionType getType()
	{
		return EvilActionType.RemovePoints;
	}

	public BigDecimal getNumToRemove()
	{
		return numToRemove;
	}

	@Override
	boolean optionsAreEqual(EvilAction other)
	{
		return true;
	}
}
