package org.hallock.tfe.msg.gv;

import java.io.IOException;

import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.sys.GameUpdateInfo;

import com.fasterxml.jackson.core.JsonParser;

public class StateChangedHandler extends GameClientHandler
{
	public StateChangedHandler(GameViewer client)
	{
		super(client);
	}

	@Override
	public String getType()
	{
		return Message.GAME_STATE_CHANGED_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		SimpleKnownValue<GameUpdateInfo> info = handler.listenForObject("/info", GameUpdateInfo.READER);
		SimpleParser.parseAllOfCurrentObject(handler, parser);
		
		client.updateInfo(info.getValue());
	}

}
