package org.hallock.tfe.cmn.util;

import java.io.IOException;
import java.util.HashMap;

import org.hallock.tfe.msg.SimpleParser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JsonCensorer
{
	JsonParser parser;
	JsonGenerator generator;
	
	HashMap<String, String> censored = new HashMap<>();

	public JsonCensorer(JsonParser parser, JsonGenerator generator)
	{
		this.parser = parser;
		this.generator = generator;
	}
	
	public void censor(String fieldName, String newValue)
	{
		censored.put(fieldName, newValue);
	}

	public void copy() throws IOException
	{
		JsonToken token;
		while ((token = parser.nextToken()) != null)
		{
			switch (token)
			{
			case FIELD_NAME:
				generator.writeFieldName(parser.getCurrentName());
				censorIfNecessary(parser.getCurrentName());
				break;
			case START_ARRAY:
				generator.writeStartArray();
				break;
			case START_OBJECT:
				generator.writeStartObject();
				break;
			case END_ARRAY:
				generator.writeEndArray();
				break;
			case END_OBJECT:
				generator.writeEndObject();
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
				
				
				// read errors
			case NOT_AVAILABLE:
			case VALUE_EMBEDDED_OBJECT:
			default:
				throw new RuntimeException();
			}
		}
	}
	
	private void censorIfNecessary(String fieldName) throws IOException
	{
		String replacementValue = censored.get(fieldName);
		if (replacementValue == null)
			return;
		generator.writeString(replacementValue);
		skipNextValue();
	}
	
	private void skipNextValue() throws IOException
	{
		switch (parser.nextToken())
		{
		case START_ARRAY:
			SimpleParser.finishArray(parser);
			break;
		case START_OBJECT:
			SimpleParser.finishObject(parser);
			break;
		case VALUE_FALSE:
		case VALUE_TRUE:
		case VALUE_NULL:
		case VALUE_NUMBER_FLOAT:
		case VALUE_NUMBER_INT:
		case VALUE_STRING:
			break;
		case FIELD_NAME:
		case NOT_AVAILABLE:
		case VALUE_EMBEDDED_OBJECT:
		case END_ARRAY:
		case END_OBJECT:
		default:
			throw new RuntimeException();
		}
	}
}
