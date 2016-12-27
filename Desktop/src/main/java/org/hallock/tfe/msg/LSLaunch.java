package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.LobbyServer;
import org.hallock.tfe.serve.WaitingPlayer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LSLaunch extends LSLobbyServerMessage
{
	public LSLaunch() {}
	
	public LSLaunch(JsonParser parser) throws IOException
	{
		while (!parser.nextToken().equals(JsonToken.END_OBJECT))
			;
	}
	
	@Override
	public void perform(LobbyServer server, WaitingPlayer player) throws IOException
	{
		Lobby lobby = player.getLobby();
		if (lobby == null)
			return;
		server.startGame(lobby);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeEndObject();
	}

	public static final String TYPE = "launch";
}
