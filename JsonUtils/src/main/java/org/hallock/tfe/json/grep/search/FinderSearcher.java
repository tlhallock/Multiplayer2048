package org.hallock.tfe.json.grep.search;

import java.io.IOException;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.find.Finder;
import org.hallock.tfe.json.grep.path.JsonPath;

import com.fasterxml.jackson.core.JsonGenerator;

public class FinderSearcher extends Searcher
{
	Finder t;
	
	public FinderSearcher(Finder t)
	{
		this.t = t;
	}
	
	public FinderSearcher(JsonObject object)
	{
		t = Finder.read(object.getJsonObject("finder"));
	}

	@Override
	public void register(MultiMatch match)
	{
		t.register(match);
	}

	@Override
	public SearchResult visit(JsonPath currentPath, JsonValue valueSearchResults, MultiMatch results)
	{
		return t.found(valueSearchResults, results);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("finder");
		t.write(generator);
		
		generator.writeEndObject();
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof FinderSearcher))
		{
			return false;
		}
		FinderSearcher pattern = (FinderSearcher) other;
		
		if (!t.equals(pattern.t))
			return false;
		
		return true;
	}
}
