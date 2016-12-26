package org.hallock.tfe.cmn.util;

import java.io.IOException;
import java.net.Socket;

import org.hallock.tfe.cmn.sys.Registry;
import org.hallock.tfe.msg.GCClientMessage;
import org.hallock.tfe.msg.GSServerMessage;
import org.hallock.tfe.msg.LCLobbyClientMessage;
import org.hallock.tfe.msg.LSLobbyServerMessage;
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
		Registry.getLogger().log("Sending message " + message);
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
	
	
	

	
	public GSServerMessage readMessageGS() throws IOException
	{
		Message parse = readMessage();
		if (parse instanceof GSServerMessage)
			return (GSServerMessage) parse;
		System.out.println("Ignoring " + parse);
		return null;
	}
	public GCClientMessage readMessageGC() throws IOException
	{
		Message parse = readMessage();
		if (parse instanceof GCClientMessage)
			return (GCClientMessage) parse;
		System.out.println("Ignoring " + parse);
		return null;
	}
	public LSLobbyServerMessage readMessageLS() throws IOException
	{
		Message parse = readMessage();
		if (parse instanceof LSLobbyServerMessage)
			return (LSLobbyServerMessage) parse;
		System.out.println("Ignoring " + parse);
		return null;
	}
	public LCLobbyClientMessage readMessageLC() throws IOException
	{
		Message parse = readMessage();
		if (parse instanceof LCLobbyClientMessage)
			return (LCLobbyClientMessage) parse;
		System.out.println("Ignoring " + parse);
		return null;
	}

	public void flush() throws IOException
	{
		generator.flush();
	}
}
