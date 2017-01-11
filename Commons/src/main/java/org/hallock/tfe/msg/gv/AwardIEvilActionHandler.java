package org.hallock.tfe.msg.gv;

import java.io.IOException;

import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionContainer;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;

import com.fasterxml.jackson.core.JsonParser;

public class AwardIEvilActionHandler extends GameClientHandler
{
	public AwardIEvilActionHandler(GameViewer client)
	{
		super(client);
	}

	@Override
	public String getType()
	{
		return Message.GAME_AWARD_EVIL_ACTION_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		SimpleKnownValue<EvilActionContainer> action = handler.listenForObject("/container", EvilActionContainer.READER);
		SimpleParser.parseAllOfCurrentObject(handler, parser);
		client.award(action.getValue().getAction());
	}

}
