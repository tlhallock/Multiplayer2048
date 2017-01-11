package org.hallock.tfe.msg.lc;

import java.io.IOException;

import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.serve.LobbyInfo;

import com.fasterxml.jackson.core.JsonParser;

public class LobbyInfoHandler extends ClientLobbyHandler
{
	public LobbyInfoHandler(LobbyClient lobbyViewer)
	{
		super(lobbyViewer);
	}

	@Override
	public String getType()
	{
		return Message.CLIENT_LOBBY_INFO_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		
		SimpleKnownValue<LobbyInfo> info = handler.listenForObject("/info", LobbyInfo.READER);
		SimpleKnownValue<Boolean> isAdmin = handler.listenForBoolean("/isHost");
		
		SimpleParser.parseAllOfCurrentObject(handler, parser);

		lobbyViewer.setLobby(isAdmin.getValue(), info.getValue());
	}
}
