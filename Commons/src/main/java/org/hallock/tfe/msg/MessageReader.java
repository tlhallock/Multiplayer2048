package org.hallock.tfe.msg;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class MessageReader
{
	private HashMap<String, MessageHandler> handlers = new HashMap<>();
	
	public boolean handleNextMessage(JsonParser parser) throws IOException
	{
		JsonToken nextToken = parser.nextToken();
		switch (nextToken)
		{
		case END_ARRAY:
			if (!parser.nextToken().equals(JsonToken.END_OBJECT))
				throw new RuntimeException("Unexpected.");
			return false;
		case START_OBJECT:
			break;
		default:
			throw new RuntimeException("Unexpected: " + nextToken);

		}
		nextToken = parser.nextToken();
		if (!nextToken.equals(JsonToken.FIELD_NAME))
		{
			throw new RuntimeException("Unexpected.");
		}
		if (!parser.getCurrentName().equals(Message.TYPE_FIELD))
		{
			throw new RuntimeException("Unexpected.");
		}
		nextToken = parser.nextToken();
		if (!nextToken.equals(JsonToken.VALUE_STRING))
		{
			throw new RuntimeException("Unexpected.");
		}
		
		
		String nextMessageId = parser.getValueAsString();
		
		MessageHandler messageHandler = handlers.get(nextMessageId);
		if (messageHandler == null)
		{
			System.out.println("Ignoring message of type: " + nextMessageId);
			SimpleParser.finishObject(parser);
			return true;
		}
		messageHandler.handle(parser);
		return true;
	}
	
	public void removeAllTypes()
	{
		for (String string : Message.ALL_MESSAGE_TYPES)
			remove(string);
	}
	
	public void add(MessageHandler handler)
	{
		MessageHandler put = handlers.put(handler.getType(), handler);
		if (put != null)
			throw new RuntimeException(handler.getClass().getName() + " is replacing " + put.getClass().getName() + " under " + handler.getType());
	}
	
	public void remove(String type)
	{
		handlers.remove(type);
	}
}
