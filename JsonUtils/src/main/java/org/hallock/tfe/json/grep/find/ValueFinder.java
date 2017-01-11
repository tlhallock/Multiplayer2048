package org.hallock.tfe.json.grep.find;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.search.SearchResult;

import com.fasterxml.jackson.core.JsonGenerator;

public class ValueFinder implements Finder
{
	private LinkedList<String> list = new LinkedList<>();
	
	public ValueFinder() {}

	public ValueFinder(String... possibleValues)
	{
		for (String string : possibleValues)
			add(string);
	}
	
	public ValueFinder(JsonObject jsonObject)
	{
		JsonArray jsonArray = jsonObject.getJsonArray("possibles");
		for (int i = 0; i < jsonArray.size(); i++)
			add(jsonArray.getString(i));
	}
	
	public ValueFinder add(String possibleValue)
	{
		list.add(possibleValue);
		return this;
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof ValueFinder))
			return false;
		
		ValueFinder o = (ValueFinder) other;

		if (list.size() != o.list.size())
			return false;
		
		Iterator<String> iterator = o.list.iterator();
		Iterator<String> iterator2 = list.iterator();
		
		while (iterator.hasNext())
			if (!iterator.next().equals(iterator2.next()))
				return false;
		
		return true;
	}

	@Override
	public void register(MultiMatch matcher) {}

	@Override
	public SearchResult found(JsonValue value, MultiMatch results)
	{
		for (String possible : list)
		{
			if (value.toString().equals(possible))
				return new SearchResult(true);
		}
		return new SearchResult();
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("possibles");
		generator.writeStartArray();
		for (String possibleValue : list)
			generator.writeString(possibleValue);
		generator.writeEndArray();
		
		generator.writeEndObject();
	}
}
