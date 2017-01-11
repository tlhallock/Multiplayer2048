package org.hallock.tfe.msg.svr;

import java.io.IOException;
import java.math.BigDecimal;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.msg.ls.UpdatePlayer.UpdateAction;
import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonParser;

public class UpdatePlayerHandler extends LobbyHandler
{
	protected UpdatePlayerHandler(Lobby server, PlayerConnection connection)
	{
		super(server, connection);
	}

	@Override
	public String getType()
	{
		return Message.LOBBY_UPDATE_PLAYER_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		
		SimpleKnownValue<BigDecimal> number = handler.listenForNumber("/number");
		SimpleKnownValue<String> action = handler.listenForString("/action");
		
		SimpleParser.parseAllOfCurrentObject(handler, parser);

		lobby.performAction(connection, 
				number.getValue().intValue(),
				UpdateAction.valueOf(action.getValue()));
	}
}
