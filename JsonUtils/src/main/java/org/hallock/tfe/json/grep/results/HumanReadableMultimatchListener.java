package org.hallock.tfe.json.grep.results;

import java.io.IOException;
import java.io.PrintStream;

import org.hallock.tfe.json.grep.MultiMatch;

import com.fasterxml.jackson.core.JsonGenerator;

public class HumanReadableMultimatchListener extends MultiMatchListener
{
	private PrintStream stream;
	private int count;

	public HumanReadableMultimatchListener(PrintStream stream)
	{
		this.stream = stream;
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
		stream.println("Matcher:");
		stream.println(match);
		count++;
	}
}
