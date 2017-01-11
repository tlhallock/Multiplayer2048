package org.hallock.tfe.json.grep.results;

import java.io.IOException;
import java.io.PrintStream;

import org.hallock.tfe.json.grep.Match;

import com.fasterxml.jackson.core.JsonGenerator;

public class HumanReadableResults extends MatchListener
{
	PrintStream ps;
	public int count;
	
	public HumanReadableResults(PrintStream ps)
	{
		this.ps = ps;
	}
	
	@Override
	public void found(Match match)
	{
		match.printHumanReadable(ps);
		count++;
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
}