package org.hallock.tfe.json.grep.search;

import java.io.IOException;
import java.util.Map.Entry;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.path.JsonPath;
import org.hallock.tfe.json.grep.path.JsonPathElement.JsonArrayElement;
import org.hallock.tfe.json.grep.path.JsonPathElement.JsonFieldElement;
import org.hallock.tfe.json.grep.path.PathMatcher;

import com.fasterxml.jackson.core.JsonGenerator;

public class ChildSearcher extends Searcher
{
	PathMatcher childPathMatcher;
	Searcher child;
	
	public ChildSearcher(Searcher field)
	{
		childPathMatcher = new PathMatcher();
		this.child = field;
	}
	
	public ChildSearcher(JsonObject object)
	{
		childPathMatcher = new PathMatcher(object.getJsonObject("path"));
		child = Searcher.read(object.getJsonObject("child"));
	}
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof ChildSearcher))
		{
			return false;
		}
		ChildSearcher pattern = (ChildSearcher) other;
		
		if (!childPathMatcher.equals(pattern.childPathMatcher))
			return false;
		
		if (!child.equals(pattern.child))
			return false;
		
		return true;
	}
	
	public PathMatcher path()
	{
		return childPathMatcher;
	}

	@Override
	public void register(MultiMatch match)
	{
		child.register(match);
	}

	@Override
	public SearchResult visit(JsonPath currentPath, JsonValue value, MultiMatch results)
	{
		SearchResult result = new SearchResult();
		extract(currentPath, new JsonPath(), value, results, result);
		return result;
	}

	private void extract(JsonPath wholePath, JsonPath childPath, JsonValue value, MultiMatch results, SearchResult result)
	{
		switch (childPathMatcher.matches(childPath))
		{
		case No:
			break;
		case Yes:
			result = result.or(child.visit(wholePath, value, results));
			if (result.stopSearch)
				break;
		case CouldContainChild:
			if (value instanceof JsonObject)
			{
				JsonObject object = (JsonObject) value;
				for (Entry<String, JsonValue> child : object.entrySet())
				{
					JsonFieldElement jsonFieldElement = new JsonFieldElement(child.getKey());
					extract(
							wholePath.dup().add(jsonFieldElement),
							childPath.dup().add(jsonFieldElement),
							child.getValue(),
							results,
							result);
					if (result.stopSearch)
						break;;
				}
			}
			else if (value instanceof JsonArray)
			{
				JsonArray array = (JsonArray) value;
				for (int i = 0; i < array.size(); i++)
				{
					JsonArrayElement jsonFieldElement = new JsonArrayElement(i);
					extract(
							wholePath.dup().add(jsonFieldElement),
							childPath.dup().add(jsonFieldElement),
							array.get(i),
							results,
							result);
					if (result.stopSearch)
						break;
				}
			}
			break;
		default:
			throw new RuntimeException();
		}
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("path");
		childPathMatcher.write(generator);
		
		generator.writeFieldName("child");
		child.write(generator);
		
		generator.writeEndObject();
	}
}