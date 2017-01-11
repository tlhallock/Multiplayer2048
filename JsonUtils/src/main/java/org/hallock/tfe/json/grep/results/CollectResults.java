package org.hallock.tfe.json.grep.results;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.hallock.tfe.json.grep.Match;

import com.fasterxml.jackson.core.JsonGenerator;

public final class CollectResults extends MatchListener
{
	LinkedList<Match> matches = new LinkedList<>();
	
	public CollectResults() {}
	
	public CollectResults(JsonObject object)
	{
		JsonArray jsonArray = object.getJsonArray("matches");
		for (int i = 0; i < jsonArray.size(); i++)
		{
			found(new Match(jsonArray.getJsonObject(i)));
		}
	}
	
	public void print(PrintStream ps)
	{
		for (Match match : matches)
			match.printHumanReadable(ps);
	}

	@Override
	public void found(Match match)
	{
		matches.add(match);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof CollectResults))
			return false;
		CollectResults o = (CollectResults) other;
		if (matches.size() != o.matches.size())
			return false;
		
		Iterator<Match> it1 = matches.iterator();
		Iterator<Match> it2 = o.matches.iterator();
		
		while (it1.hasNext())
		{
			if (!it1.next().equals(it2.next()))
				return false;
		}
		
		return true;
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeNumberField("number of matches", matches.size());
		
		generator.writeFieldName("matches");
		generator.writeStartArray();
		for (Match match : matches)
			match.write(generator);
		generator.writeEndArray();
		
		generator.writeEndObject();
	}

	@Override
	public int getNumberOfResults()
	{
		return matches.size();
	}

	public Match get(int i)
	{
		return matches.get(i);
	}
}