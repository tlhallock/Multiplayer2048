package org.hallock.tfe.json.grep.find;

import java.io.IOException;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.search.SearchResult;

import com.fasterxml.jackson.core.JsonGenerator;

public class ExistsFinder implements Finder
{
	public ExistsFinder() {}
	
	public ExistsFinder(JsonObject jsonObject) {}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof ExistsFinder;
	}

	@Override
	public void register(MultiMatch matcher) {}

	@Override
	public SearchResult found(JsonValue value, MultiMatch results)
	{
		return new SearchResult(true);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		generator.writeEndObject();
	}
}
