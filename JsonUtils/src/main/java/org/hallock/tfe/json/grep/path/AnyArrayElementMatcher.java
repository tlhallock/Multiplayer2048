package org.hallock.tfe.json.grep.path;

import java.io.IOException;

import javax.json.JsonObject;

import org.hallock.tfe.json.grep.path.JsonPathElement.JsonArrayElement;

import com.fasterxml.jackson.core.JsonGenerator;

public class AnyArrayElementMatcher extends PathElementMatcher
{
	public AnyArrayElementMatcher() {}
	
	public AnyArrayElementMatcher(JsonObject jsonObject) {}

	@Override
	public boolean matches(JsonPathElement jsonPathElement)
	{
		return jsonPathElement instanceof JsonArrayElement;
	}

	@Override
	public String toString()
	{
		return "$*";
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		generator.writeEndObject();
	}
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof AnyArrayElementMatcher))
			return false;
		AnyArrayElementMatcher o = (AnyArrayElementMatcher) other;
		
		return true;
	}
}