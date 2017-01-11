package org.hallock.tfe.json.grep.results;

import java.io.IOException;

import org.hallock.tfe.json.grep.Match;

import com.fasterxml.jackson.core.JsonGenerator;

public class JsonResults extends MatchListener
{
	private int count;
	private JsonGenerator generator;

	public JsonResults(JsonGenerator generator)
	{
		this.generator = generator;
	}
	
	public void writeStart() throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		generator.writeFieldName("output");
		generator.writeStartArray();
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		generator.writeEndObject();
	}

	@Override
	public void found(Match match) throws IOException
	{
		count++;
		match.write(generator);
	}

	@Override
	public int getNumberOfResults()
	{
		return 0;
	}

	public void writeEnd() throws IOException
	{
		generator.writeEndArray();
		generator.writeEndObject();
	}
}
