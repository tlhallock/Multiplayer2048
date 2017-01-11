package org.hallock.tfe.msg.svr;

import java.io.IOException;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.serve.Game;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonParser;

public class PlayerActionHandler extends GameHandler
{

	public PlayerActionHandler(Game game, PlayerConnection connection)
	{
		super(game, connection);
	}

	@Override
	public String getType()
	{
		return Message.GAME_SERVER_PLAY_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		SimpleKnownValue<String> action = handler.listenForString("/action");
		SimpleParser.parseAllOfCurrentObject(handler, parser);
		game.play(player.getRole().getIndex(), 
				PossiblePlayerActions.valueOf(action.getValue()), true);
	}
}
