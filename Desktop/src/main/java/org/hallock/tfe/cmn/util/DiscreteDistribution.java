package org.hallock.tfe.cmn.util;

import java.io.IOException;
import java.util.ArrayList;

import org.hallock.tfe.cmn.sys.Constants;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/*
 * Could do alias method thing
 */
public class DiscreteDistribution implements Jsonable
{
	ArrayList<Integer> outcomes = new ArrayList<>();
	ArrayList<Double> probabilities = new ArrayList<>();
	double sum = -1;

	public DiscreteDistribution(int[] o, double[] probs)
	{
		if (o.length != probs.length)
			throw new RuntimeException("uh oh!");
		for (int i = 0; i < o.length; i++)
		{
			outcomes.add(o[i]);
			probabilities.add(probs[i]);
		}
	}


	public DiscreteDistribution(DiscreteDistribution d)
	{
		for (int i = 0; i < d.outcomes.size(); i++)
		{
			add(d.outcomes.get(i),
				d.probabilities.get(i));
		}
		sum = -1;
	}

	public DiscreteDistribution()
	{
	}
	
	public DiscreteDistribution(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case START_ARRAY:
				switch (currentName)
				{
				case "probs":
					while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
					{
						switch (next)
						{
						case VALUE_NUMBER_FLOAT:
						case VALUE_NUMBER_INT:
							probabilities.add(parser.getNumberValue().doubleValue());
							break;
						default:
							throw new RuntimeException("Unexpected.");
						}
					}
					break;
				case "vals":
					while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
					{
						switch (next)
						{
						case VALUE_NUMBER_FLOAT:
						case VALUE_NUMBER_INT:
							outcomes.add(parser.getNumberValue().intValue());
							break;
						default:
							throw new RuntimeException("Unexpected.");
						}
					}
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
	
	public synchronized void add(int value, double prob)
	{
		outcomes.add(value);
		probabilities.add(prob);
		sum = -1;
	}
	
	private void checkSum()
	{
		if (sum > 0)
		{
			return;
		}
		
		sum = 0;
		for (Double d : probabilities)
			sum += d;
		
		if (sum <= 0)
		{
			throw new RuntimeException("bad probability distribution");
		}
	}

	public synchronized int sample()
	{
		checkSum();
		
		double value = Constants.random.nextDouble() * sum;
		
		int idx = 0;
		do
		{
			value -= probabilities.get(idx);
			if (value <= 0)
				return idx;
			idx++;
		} while (idx < probabilities.size());

		throw new RuntimeException("Uh oh");
	}

	@Override
	public synchronized void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeFieldName("probs");
		generator.writeStartArray();
		for (Double d : probabilities)
			generator.writeNumber(d);
		generator.writeEndArray();
		generator.writeFieldName("vals");
		generator.writeStartArray();
		for (Integer d : outcomes)
			generator.writeNumber(d);
		generator.writeEndArray();
		generator.writeEndObject();
	}
}
