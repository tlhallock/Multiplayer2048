package org.hallock.tfe.msg.lc;

import java.io.IOException;
import java.util.ArrayList;

import org.hallock.tfe.client.LobbyClient;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.serve.LobbyInfo;

import com.fasterxml.jackson.core.JsonParser;

public class LobbiesListHandler extends ClientLobbyHandler
{
	public LobbiesListHandler(LobbyClient lobbyViewer)
	{
		super(lobbyViewer);
	}

	@Override
	public String getType()
	{
		return Message.CLIENT_LOBBY_LOBBIES_LIST_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		
		SimpleKnownValue<ArrayList<LobbyInfo>> info = handler.listenForArray("/lobbies", LobbyInfo.READER);
		
		SimpleParser.parseAllOfCurrentObject(handler, parser);

		lobbyViewer.setLobbies(info.getValue());
	}

}
