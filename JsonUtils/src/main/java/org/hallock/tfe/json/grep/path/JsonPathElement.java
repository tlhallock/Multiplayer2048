package org.hallock.tfe.json.grep.path;

import java.io.IOException;

import javax.json.JsonObject;

import org.hallock.tfe.cmn.util.Jsonable;

import com.fasterxml.jackson.core.JsonGenerator;

public abstract class JsonPathElement implements Jsonable
{
	@Override
	public abstract String toString();
	
	public static class JsonFieldElement extends JsonPathElement
	{
		public final String fieldName;

		public JsonFieldElement(String fieldName)
		{
			this.fieldName = fieldName;
		}
		
		private JsonFieldElement(JsonObject object)
		{
			fieldName = object.getString("name");
		}

		@Override
		public String toString()
		{
			return fieldName;
		}

		@Override
		public void write(JsonGenerator generator) throws IOException
		{
			generator.writeStartObject();
			generator.writeStringField("type", getClass().getName());
			generator.writeStringField("name", fieldName);
			generator.writeEndObject();
		}
		
		@Override
		public boolean equals(Object other)
		{
			if (!(other instanceof JsonFieldElement))
				return false;
			JsonFieldElement o = (JsonFieldElement) other;
			
			return fieldName.equals(o.fieldName);
		}
	}
	
	public static class JsonArrayElement extends JsonPathElement
	{
		public final int index;

		public JsonArrayElement(int index)
		{
			this.index = index;
		}
		
		private JsonArrayElement(JsonObject object)
		{
			this.index = object.getInt("index");
		}

		@Override
		public String toString()
		{
			return "$" + index;
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
			if (!(other instanceof JsonArrayElement))
				return false;
			JsonArrayElement o = (JsonArrayElement) other;
			return index == o.index;
		}
	}
	
	public static JsonPathElement read(JsonObject object)
	{
		String type = object.getString("type");
		if (type.equals(JsonFieldElement.class.getName()))
		{
			return new JsonFieldElement(object);
		}
		else if (type.equals(JsonArrayElement.class.getName()))
		{
			return new JsonArrayElement(object);
		}
		throw new RuntimeException("Unexpected: " + type);
	}
}
