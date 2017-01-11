package org.hallock.tfe.json.grep.search;

import java.io.IOException;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.path.JsonPath;

import com.fasterxml.jackson.core.JsonGenerator;

public class FieldSearcher extends Searcher
{
	Searcher finder;
	String key;
	
	public FieldSearcher(Searcher finder, String key)
	{
		this.finder = finder;
		this.key = key;
	}

	public FieldSearcher(JsonObject jsonObject)
	{
		finder = Searcher.read(jsonObject.getJsonObject("finder"));
		key = jsonObject.getString("key");
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof FieldSearcher))
		{
			return false;
		}
		FieldSearcher pattern = (FieldSearcher) other;
		
		if (!finder.equals(pattern.finder))
			return false;
		
		if (!key.equals(pattern.key))
			return false;
		
		return true;
	}

	@Override
	public SearchResult visit(JsonPath currentPath, JsonValue value, MultiMatch results)
	{
		if (!(value instanceof JsonObject))
			return new SearchResult();
		if (!((JsonObject)value).containsKey(key))
			return new SearchResult();
		return finder.visit(currentPath, ((JsonObject)value).get(key), results);
	}

	@Override
	public void register(MultiMatch match)
	{
		finder.register(match);
	}


	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("finder");
		finder.write(generator);
		
		generator.writeStringField("key", key);
		
		generator.writeEndObject();
	}
}