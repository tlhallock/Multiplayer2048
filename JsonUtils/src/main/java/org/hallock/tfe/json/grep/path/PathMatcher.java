package org.hallock.tfe.json.grep.path;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.hallock.tfe.cmn.util.Jsonable;

import com.fasterxml.jackson.core.JsonGenerator;

public class PathMatcher implements Jsonable
{
	LinkedList<PathElementMatcher> matchers = new LinkedList<>();
	
	public PathMatcher(JsonObject object)
	{
		JsonArray jsonArray = object.getJsonArray("matchers");
		for (int i = 0; i < jsonArray.size(); i++)
		{
			matchers.add(PathElementMatcher.read(jsonArray.getJsonObject(i)));
		}
	}
	
	public PathMatcher() {}
	
	public PathMatchResult matches(JsonPath path)
	{
		Iterator<PathElementMatcher> matcherIterator = matchers.iterator();
		Iterator<JsonPathElement> pathIterator = path.iterator();
		
		while (matcherIterator.hasNext())
		{
			if (!pathIterator.hasNext())
			{
				return PathMatchResult.CouldContainChild;
			}
			
			if (!matcherIterator.next().matches(pathIterator.next()))
				return PathMatchResult.No;
		}
		
		if (pathIterator.hasNext())
		{
			return PathMatchResult.No;
		}
		else
		{
			return PathMatchResult.Yes;
		}
	}

	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof PathMatcher))
			return false;
		PathMatcher o = (PathMatcher) other;
		if (matchers.size() != o.matchers.size())
			return false;
		
		Iterator<PathElementMatcher> it1 = matchers.iterator();
		Iterator<PathElementMatcher> it2 = o.matchers.iterator();
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
		generator.writeFieldName("matchers");
		generator.writeStartArray();
		for (PathElementMatcher matcher : matchers)
			matcher.write(generator);
		generator.writeEndArray();
		generator.writeEndObject();
	}
	
	public PathMatcher add(PathElementMatcher matcher)
	{
		matchers.add(matcher);
		return this;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		for (PathElementMatcher element : matchers)
		{
			builder.append("/").append(element);
		}
		
		return builder.toString();
	}
	
	
	
	
	public PathMatcher exactField(String str)
	{
		return add(new ExactFieldElementMatcher(str));
	}
	
	public PathMatcher array(int index)
	{
		return add(new ExactArrayElementMatcher(index));
	}
	
	public PathMatcher any()
	{
		return add(new AnyElementMatcher());
	}
	
	public PathMatcher anyArray()
	{
		return add(new AnyArrayElementMatcher());
	}
	
	public PathMatcher cutFields(String str)
	{
		for (String s : str.split("/"))
		{
			exactField(s);
		}
		return this;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public enum PathMatchResult
	{
		No,
		CouldContainChild,
		Yes,
		
		;

		public boolean isExact()
		{
			switch (this)
			{
			case CouldContainChild:
			case No:
				return false;
			case Yes:
				return true;
			default:
				throw new RuntimeException();
			}
		}
	}
}
