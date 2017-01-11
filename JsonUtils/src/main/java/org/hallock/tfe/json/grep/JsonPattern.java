package org.hallock.tfe.json.grep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.json.grep.path.JsonPath;
import org.hallock.tfe.json.grep.results.MatchListener;
import org.hallock.tfe.json.grep.results.MultiMatchListener;
import org.hallock.tfe.json.grep.search.SearchResult;
import org.hallock.tfe.json.grep.search.Searcher;

import com.fasterxml.jackson.core.JsonGenerator;

public class JsonPattern implements Jsonable
{
	private ArrayList<Searcher> visitors = new ArrayList<>();
	private MultiMatch match = new MultiMatch();
	
	private LinkedList<MultiMatchListener> multiMatchers = new LinkedList<>();
	private LinkedList<MatchListener> matchers = new LinkedList<>();
	
	public JsonPattern() {}
	
	public JsonPattern(JsonObject object)
	{
		JsonArray jsonArray = object.getJsonArray("visitors");
		for (int i = 0; i < jsonArray.size(); i++)
		{
			add(Searcher.read(jsonArray.getJsonObject(i)));
		}
	}
	
	public void addListener(MultiMatchListener listener)
	{
		multiMatchers.add(listener);
	}
	
	public void addListener(MatchListener listener)
	{
		matchers.add(listener);
	}
	
	public JsonPattern add(Searcher e)
	{
		e.register(match);
		visitors.add(e);
		return this;
	}
	
	public void visit(JsonPath path, JsonValue value)
	{
		for (Searcher e : visitors)
		{
			match.clear();
			SearchResult visit = e.visit(path, value, match);
			if (!visit.matched)
				return;
			
			for (MultiMatchListener listener : multiMatchers)
			{
				try
				{
					listener.found(match);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			
			for (MatchListener listener : matchers)
			{
				match.enumerate(path, listener);
			}
		}
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("visitors");
		generator.writeStartArray();
		for (Searcher s : visitors)
			s.write(generator);
		generator.writeEndArray();
		
		generator.writeEndObject();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof JsonPattern))
		{
			return false;
		}
		JsonPattern pattern = (JsonPattern) other;
		if (visitors.size() != pattern.visitors.size())
			return false;

		for (int i = 0; i < visitors.size(); i++)
			if (!visitors.get(i).equals(pattern.visitors.get(i)))
				return false;
		
		return true;
	}
}