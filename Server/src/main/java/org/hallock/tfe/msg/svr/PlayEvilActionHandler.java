package org.hallock.tfe.msg.svr;

import java.io.IOException;
import java.math.BigDecimal;

import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionContainer;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.serve.Game;
import org.hallock.tfe.serve.PlayerConnection;

import com.fasterxml.jackson.core.JsonParser;

public class PlayEvilActionHandler extends GameHandler
{
	public PlayEvilActionHandler(Game game, PlayerConnection connection)
	{
		super(game, connection);
	}

	@Override
	public String getType()
	{
		return Message.GAME_SERVER_PLAY_ACTION_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		
		SimpleKnownValue<BigDecimal> otherPlayer = handler.listenForNumber("/player");
		SimpleKnownValue<EvilActionContainer> action = handler.listenForObject("/container",
				EvilActionContainer.READER);
		
		SimpleParser.parseAllOfCurrentObject(handler, parser);

		game.playAction(player.getRole().getIndex(), 
				action.getValue().getAction(), 
				otherPlayer.getValue().intValue());
	}
}
