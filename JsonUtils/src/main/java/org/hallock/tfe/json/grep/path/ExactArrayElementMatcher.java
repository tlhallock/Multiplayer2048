package org.hallock.tfe.json.grep.path;

import java.io.IOException;

import javax.json.JsonObject;

import org.hallock.tfe.json.grep.path.JsonPathElement.JsonArrayElement;

import com.fasterxml.jackson.core.JsonGenerator;

public class ExactArrayElementMatcher extends PathElementMatcher
{
	private int index;
	
	ExactArrayElementMatcher(int index)
	{
		this.index = index;
	}
	
	public ExactArrayElementMatcher(JsonObject jsonObject)
	{
		index = jsonObject.getInt("index");
	}

	@Override
	public boolean matches(JsonPathElement jsonPathElement)
	{
		if (!(jsonPathElement instanceof JsonArrayElement))
			return false;
		
		JsonArrayElement e = (JsonArrayElement) jsonPathElement;
		return index == e.index;
	}

	@Override
	public String toString()
	{
		return "$array[" + index + "]";
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		generator.writeNumberField("index", index);
		generator.writeEndObject();
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof ExactArrayElementMatcher))
			return false;
		ExactArrayElementMatcher o = (ExactArrayElementMatcher) other;
		return index == o.index;
	}
}