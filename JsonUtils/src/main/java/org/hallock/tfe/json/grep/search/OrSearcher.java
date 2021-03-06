package org.hallock.tfe.json.grep.search;

import java.io.IOException;
import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.path.JsonPath;

import com.fasterxml.jackson.core.JsonGenerator;

public class OrSearcher extends Searcher
{
	private ArrayList<Searcher> searchers = new ArrayList<>();
	
	public OrSearcher() {}
	
	public OrSearcher(JsonObject object)
	{
		JsonArray jsonArray = object.getJsonArray("children");
		for (int i = 0; i < jsonArray.size(); i++)
		{
			searchers.add(Searcher.read(jsonArray.getJsonObject(i)));
		}
	}
	
	public OrSearcher add(Searcher s)
	{
		searchers.add(s);
		return this;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof OrSearcher))
		{
			return false;
		}
		OrSearcher pattern = (OrSearcher) other;
		if (searchers.size() != pattern.searchers.size())
			return false;

		for (int i = 0; i < searchers.size(); i++)
			if (!searchers.get(i).equals(pattern.searchers.get(i)))
				return false;
		
		return true;
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("children");
		generator.writeStartArray();
		for (Searcher searcher : searchers)
			searcher.write(generator);
		generator.writeEndArray();
		
		generator.writeEndObject();
	}

	@Override
	public void register(MultiMatch match)
	{
		for (Searcher searcher : searchers)
		{
			searcher.register(match);
		}
	}

	@Override
	public SearchResult visit(JsonPath currentPath, JsonValue valueSearchResults, MultiMatch results)
	{
		SearchResult result = new SearchResult();
		for (Searcher searcher : searchers)
		{
			result.or(searcher.visit(currentPath, valueSearchResults, results));
		}
		return result;
	}
}
