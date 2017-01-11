package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonParser;

public class UpdateOptionsHandler extends LobbyHandler
{
	protected UpdateOptionsHandler(Lobby server, PlayerConnection connection)
	{
		super(server, connection);
	}

	@Override
	public String getType()
	{
		return Message.LOBBY_UPDATE_OPTIONS_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		SimpleKnownValue<GameOptions> value = handler.listenForObject("/options", GameOptions.READER);
		SimpleParser.parseAllOfCurrentObject(handler, parser);
		lobby.setOptions(connection, value.getValue());
	}
}
