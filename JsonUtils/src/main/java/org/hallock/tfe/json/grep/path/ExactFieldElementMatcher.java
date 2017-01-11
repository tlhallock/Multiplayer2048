package org.hallock.tfe.json.grep.path;

import java.io.IOException;

import javax.json.JsonObject;

import org.hallock.tfe.json.grep.path.JsonPathElement.JsonFieldElement;

import com.fasterxml.jackson.core.JsonGenerator;

public class ExactFieldElementMatcher extends PathElementMatcher
{
	private String value;
	
	public ExactFieldElementMatcher(String value)
	{
		this.value = value;
	}

	public ExactFieldElementMatcher(JsonObject jsonObject)
	{
		value = jsonObject.getString("value");
	}

	@Override
	public boolean matches(JsonPathElement jsonPathElement)
	{
		if (!(jsonPathElement instanceof JsonFieldElement))
			return false;
		
		JsonFieldElement e = (JsonFieldElement) jsonPathElement;
		return value.equals(e.fieldName);
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());
		generator.writeStringField("value", value);
		generator.writeEndObject();
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof ExactFieldElementMatcher))
			return false;
		ExactFieldElementMatcher o = (ExactFieldElementMatcher) other;
		return value.equals(o.value);
	}
}
