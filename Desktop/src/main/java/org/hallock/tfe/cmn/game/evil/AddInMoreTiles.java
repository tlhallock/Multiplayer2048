package org.hallock.tfe.cmn.game.evil;

import java.io.IOException;

import org.hallock.tfe.cmn.util.DiscreteDistribution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AddInMoreTiles extends EvilAction
{
	DiscreteDistribution dist;
	int number;
	
	public AddInMoreTiles(DiscreteDistribution d, int n)
	{
		this.dist = d;
		this.number = n;
	}

	public AddInMoreTiles(JsonParser parser) throws IOException
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
				case "number":
					this.number = parser.getIntValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_OBJECT:
				switch (currentName)
				{
				case "distribution":
					dist = new DiscreteDistribution(parser);
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
		generator.writeNumberField("number", number);
		generator.writeFieldName("distribution");
		dist.write(generator);
		generator.writeEndObject();
	}

	@Override
	public EvilActionType getType()
	{
		return EvilActionType.AddInMoreTiles;
	}

	public DiscreteDistribution getDistribution()
	{
		return dist;
	}

	public int getNumberOfNewTiles()
	{
		return number;
	}

	@Override
	boolean optionsAreEqual(EvilAction other)
	{
		return number == ((AddInMoreTiles) other).number && dist.equals(((AddInMoreTiles) other).dist);
	}

}
