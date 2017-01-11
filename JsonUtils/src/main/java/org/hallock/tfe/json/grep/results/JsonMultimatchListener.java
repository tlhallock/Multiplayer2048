package org.hallock.tfe.json.grep.results;

import java.io.IOException;

import org.hallock.tfe.json.grep.MultiMatch;

import com.fasterxml.jackson.core.JsonGenerator;

public class JsonMultimatchListener extends MultiMatchListener
{
	private JsonGenerator generator;
	private int count;

	public JsonMultimatchListener(JsonGenerator stream)
	{
		this.generator = stream;
	}
	
	public void writeStart() throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		generator.writeFieldName("output");
		generator.writeStartArray();
	}

	public void writeEnd() throws IOException
	{
		generator.writeEndArray();
		generator.writeEndObject();
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		generator.writeEndObject();
	}
	
	@Override
	public int getNumberOfResults()
	{
		return count;
	}

	@Override
	public void found(MultiMatch match) throws IOException
	{
		match.write(generator);
		count++;
	}
}
