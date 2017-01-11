package org.hallock.tfe.json.grep.path;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.hallock.tfe.cmn.util.Jsonable;

import com.fasterxml.jackson.core.JsonGenerator;

public class JsonPath implements Jsonable
{
	LinkedList<JsonPathElement> elements = new LinkedList<>();

	public JsonPath() {}

	public JsonPath(JsonObject jsonObject)
	{
		JsonArray array = jsonObject.getJsonArray("elements");
		for (int i = 0; i < array.size(); i++)
		{
			elements.add(JsonPathElement.read(array.getJsonObject(i)));
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof JsonPath))
			return false;
		JsonPath o = (JsonPath) other;
		if (elements.size() != o.elements.size())
			return false;
		
		Iterator<JsonPathElement> it1 = elements.iterator();
		Iterator<JsonPathElement> it2 = o.elements.iterator();
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
		generator.writeFieldName("elements");
		generator.writeStartArray();
		for (JsonPathElement matcher : elements)
			matcher.write(generator);
		generator.writeEndArray();
		generator.writeEndObject();
	}

	public Iterator<JsonPathElement> iterator()
	{
		return elements.iterator();
	}

	public JsonPath add(JsonPathElement jsonFieldElement)
	{
		elements.addLast(jsonFieldElement);
		return this;
	}
	
	public void push(JsonPathElement e)
	{
		elements.addLast(e);
	}
	public void pop()
	{
		elements.removeLast();
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		for (JsonPathElement element : elements)
		{
			builder.append("/").append(element);
		}
		
		return builder.toString();
	}

	public JsonPath dup()
	{
		JsonPath returnValue = new JsonPath();
		returnValue.elements.addAll(elements);
		return returnValue;
	}
}
