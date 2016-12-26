package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.serve.Lobby.LobbyInfo;
import org.hallock.tfe.serve.LobbyServer;
import org.hallock.tfe.serve.WaitingPlayer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LSCreateLobby extends LSLobbyServerMessage
{
	public LSCreateLobby() {}

	public LSCreateLobby(JsonParser parser) throws IOException
	{
		while (!parser.nextToken().equals(JsonToken.END_OBJECT))
			;
	}

	@Override
	public void perform(LobbyServer server, WaitingPlayer player) throws IOException
	{
		LobbyInfo info = server.createLobby(player);
		player.updateLobby(true, info);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(Message.TYPE_FIELD, TYPE);
		generator.writeEndObject();
	}

	public static final String TYPE = "create_lobby";
}
