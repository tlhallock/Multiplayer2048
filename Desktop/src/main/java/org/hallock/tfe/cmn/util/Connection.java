package org.hallock.tfe.cmn.util;

import java.io.IOException;
import java.net.Socket;

import org.hallock.tfe.msg.Message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Connection
{
	Socket socket;
	JsonGenerator generator;
	JsonParser parser;
	
	public Connection(
			Socket socket,
			JsonGenerator generator,
			JsonParser parser)
	{
		this.socket = socket;
		this.generator = generator;
		this.parser = parser;
	}
	
	public void sendMessageAndFlush(Message message) throws IOException
	{
		sendMessageWithoutFlushing(message);
		generator.flush();
	}
	public void sendMessageWithoutFlushing(Message message) throws IOException
	{
//		System.out.println("Sending message " + message);
//		Registry.getLogger().log("Sending message " + message);
		message.write(generator);
	}
	
	
	public Message readMessage() throws IOException
	{
		JsonToken nextToken = parser.nextToken();
		switch (nextToken)
		{
		case END_ARRAY:
			// finish closing connection
			if (!parser.nextToken().equals(JsonToken.END_OBJECT))
				throw new RuntimeException("Unexpected.");
			return null;
		case START_OBJECT:
			return Message.parse(parser);
		default:
			throw new RuntimeException("Unexpected.");
		}
	}
	
	
	public void readOpen() throws IOException
	{
		if (!parser.nextToken().equals(JsonToken.START_OBJECT))
			throw new RuntimeException("Unexpected.");
		if (!parser.nextToken().equals(JsonToken.FIELD_NAME))
			throw new RuntimeException("Unexpected.");
		if (!parser.getCurrentName().equals("messages"))
			throw new RuntimeException("Unexpected.");
		if (!parser.nextToken().equals(JsonToken.START_ARRAY))
			throw new RuntimeException("Unexpected.");
	}
	
	public static JsonGenerator opened(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeFieldName("messages");
		generator.writeStartArray();
		return generator;
	}
	public void sendClose() throws IOException
	{
		generator.writeEndArray();
		generator.writeEndObject();
		generator.flush();
	}
	
	
	
//
//	
//	public GameMessage readMessageGS() throws IOException
//	{
//		Message parse = readMessage();
//		if (parse instanceof GameMessage)
//			return (GameMessage) parse;
//		System.out.println("Ignoring " + parse);
//		return null;
//	}
//	public GameViewerMessage readMessageGC() throws IOException
//	{
//		Message parse = readMessage();
//		if (parse instanceof GameViewerMessage)
//			return (GameViewerMessage) parse;
//		System.out.println("Ignoring " + parse);
//		return null;
//	}
//	public LobbyMessage readMessageLS() throws IOException
//	{
//		Message parse = readMessage();
//		if (parse instanceof LobbyMessage)
//			return (LobbyMessage) parse;
//		System.out.println("Ignoring " + parse);
//		return null;
//	}
//	public LobbyClientMessage readMessageLC() throws IOException
//	{
//		Message parse = readMessage();
//		if (parse instanceof LobbyClientMessage)
//			return (LobbyClientMessage) parse;
//		System.out.println("Ignoring " + parse);
//		return null;
//	}

	public void flush() throws IOException
	{
		generator.flush();
	}

	public String getConnectionInfo()
	{
		return socket.getRemoteSocketAddress().toString();
	}
}
