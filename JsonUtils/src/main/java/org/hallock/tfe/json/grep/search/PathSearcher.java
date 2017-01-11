package org.hallock.tfe.json.grep.search;

import java.io.IOException;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.path.JsonPath;
import org.hallock.tfe.json.grep.path.PathMatcher;

import com.fasterxml.jackson.core.JsonGenerator;

public class PathSearcher extends Searcher
{
	private PathMatcher matcher;
	private Searcher child;
	
	public PathSearcher(Searcher child)
	{
		matcher = new PathMatcher();
		this.child = child;
	}
	
	public PathSearcher(JsonObject jsonObject)
	{
		matcher = new PathMatcher(jsonObject.getJsonObject("path"));
		child = Searcher.read(jsonObject.getJsonObject("child"));
	}
	
	public PathMatcher path()
	{
		return matcher;
	}

	@Override
	public void register(MultiMatch match)
	{
		child.register(match);
	}

	@Override
	public SearchResult visit(JsonPath currentPath, JsonValue valueSearchResults, MultiMatch results)
	{
		if (matcher.matches(currentPath).isExact())
			return child.visit(currentPath, valueSearchResults, results);
		return new SearchResult();
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("path");
		matcher.write(generator);
		
		generator.writeFieldName("child");
		child.write(generator);
		
		generator.writeEndObject();
	}

	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof PathSearcher))
		{
			return false;
		}
		PathSearcher pattern = (PathSearcher) other;
		
		if (!matcher.equals(pattern.matcher))
			return false;
		if (!child.equals(pattern.child))
			return false;
		
		return true;
	}
}
