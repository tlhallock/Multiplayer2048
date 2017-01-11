package org.hallock.tfe.json.grep.path;

import java.io.IOException;

import javax.json.JsonObject;

import com.fasterxml.jackson.core.JsonGenerator;

public class AnyElementMatcher extends PathElementMatcher
{
	public AnyElementMatcher() {}
	
	public AnyElementMatcher(JsonObject jsonObject) {}

	@Override
	public boolean matches(JsonPathElement jsonPathElement)
	{
		return true;
	}

	@Override
	public String toString()
	{
		return "*";
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
		if (!(other instanceof AnyElementMatcher))
			return false;
		AnyElementMatcher o = (AnyElementMatcher) other;
		
		return true;
	}
}