package org.hallock.tfe.msg;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public abstract class Message
{
	public static final String TYPE_FIELD = "type";

	public abstract void write(JsonGenerator generator) throws IOException;
	
	public static Message parse(JsonParser parser) throws IOException
	{
		JsonToken nextToken = parser.nextToken();
		switch (nextToken)
		{
		case END_ARRAY:
			break;
		case FIELD_NAME:
			break;
		default:
			throw new RuntimeException("Unexpected.");

		}
		if (!nextToken.equals(JsonToken.FIELD_NAME))
		{
			throw new RuntimeException("Unexpected.");
		}
		if (!parser.getCurrentName().equals(TYPE_FIELD))
		{
			throw new RuntimeException("Unexpected.");
		}
		nextToken = parser.nextToken();
		if (!nextToken.equals(JsonToken.VALUE_STRING))
		{
			throw new RuntimeException("Unexpected.");
		}
		String next = parser.getValueAsString();
		switch (next)
		{
		case GCGameStateChanged.TYPE:
			return new GCGameStateChanged(parser);
		case GSPlayerAction.TYPE:
			return new GSPlayerAction(parser);
			
			
			
			
		case LCLobbiesListMessage.TYPE:
			return new LCLobbiesListMessage(parser);
		case LSListLobbiesMessage.TYPE:
			return new LSListLobbiesMessage(parser);
		case LSCreateLobby.TYPE:
			return new LSCreateLobby(parser);
		case LCLobbyChanged.TYPE:
			return new LCLobbyChanged(parser);
			
			
		default:
			throw new RuntimeException("Unrecognized type: " + next);
		}
	}
	
	@Override
	public String toString()
	{
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		try (JsonGenerator generator = Json.createUnopenedGenerator(output);)
//		{
//			write(generator);
//			return new String(output.toByteArray());
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//			return "error";
//		}
		return "boring";
	}
}
