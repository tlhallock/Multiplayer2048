package org.hallock.tfe.json.grep.find;

import java.io.IOException;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.ResultName;
import org.hallock.tfe.json.grep.search.SearchResult;

import com.fasterxml.jackson.core.JsonGenerator;

public class CaptureFinder implements Finder
{
	ResultName name;

	public CaptureFinder(ResultName name)
	{
		this.name = name;
	}

	public CaptureFinder(JsonObject jsonObject)
	{
		name = new ResultName(jsonObject.getJsonObject("name"));
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof CaptureFinder))
		{
			return false;
		}
		CaptureFinder o = (CaptureFinder) other;
		return name.equals(o.name);
	}
	
	@Override
	public void register(MultiMatch matcher)
	{
		matcher.seeks(name);
	}

	@Override
	public SearchResult found(JsonValue value, MultiMatch results)
	{
		results.foundMatch(name, value.toString());
		return new SearchResult(true);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("name");
		name.write(generator);
		
		generator.writeEndObject();
	}
}
