package org.hallock.tfe.json.grep.results;

import java.io.IOException;

import org.hallock.tfe.json.grep.Match;

import com.fasterxml.jackson.core.JsonGenerator;

public class SplitResults extends MatchListener
{
	MatchListener r1;
	MatchListener r2;
	
	public SplitResults(MatchListener r1, MatchListener r2)
	{
		this.r1 = r1;
		this.r2 = r2;
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
		r1.found(match);
		r2.found(match);
	}

	@Override
	public int getNumberOfResults()
	{
		return r1.getNumberOfResults();
	}
}
