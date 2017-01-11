package org.hallock.tfe.cmn.util;

import java.io.IOException;
import java.net.Socket;

import org.hallock.tfe.msg.MessageSender;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

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
	
	public void sendMessageAndFlush(MessageSender message) throws IOException
	{
		sendMessageWithoutFlushing(message);
		generator.flush();
	}
	
	public void sendMessageWithoutFlushing(MessageSender message) throws IOException
	{
		message.sendMessage(generator);
	}
	
	public void sendClose() throws IOException
	{
		generator.writeEndArray();
		generator.writeEndObject();
		generator.flush();
	}
	
	
	public void flush() throws IOException
	{
		generator.flush();
	}

	public JsonGenerator getGenerator()
	{
		return generator;
	}

	public String getConnectionInfo()
	{
		return socket.getRemoteSocketAddress().toString();
	}
}
