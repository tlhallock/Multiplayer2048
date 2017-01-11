package org.hallock.tfe.cmn.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JsonCopier
{
	JsonParser parser;
	JsonGenerator generator;

	public JsonCopier(JsonParser parser, JsonGenerator generator)
	{
		this.parser = parser;
		this.generator = generator;
	}

	public void copy() throws IOException
	{
		JsonToken token;
		while ((token = parser.nextToken()) != null)
		{
			switch (token)
			{
			case END_ARRAY:
				generator.writeEndArray();
				break;
			case END_OBJECT:
				generator.writeEndObject();
				break;
			case FIELD_NAME:
				generator.writeFieldName(parser.getCurrentName());
				break;
			case START_ARRAY:
				generator.writeStartArray();
				break;
			case START_OBJECT:
				generator.writeStartObject();
				break;
			case VALUE_FALSE:
				generator.writeBoolean(false);
				break;
			case VALUE_TRUE:
				generator.writeBoolean(true);
				break;
			case VALUE_NULL:
				generator.writeNull();
				break;
			case VALUE_NUMBER_FLOAT:
			case VALUE_NUMBER_INT:
				generator.writeNumber(parser.getDecimalValue());
				break;
			case VALUE_STRING:
				generator.writeString(parser.getValueAsString());
				break;
			case NOT_AVAILABLE:
			case VALUE_EMBEDDED_OBJECT:
			default:
				throw new RuntimeException();

			}
		}
	}
}
