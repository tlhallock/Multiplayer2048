package org.hallock.tfe.msg.gc;

import java.io.IOException;
import java.math.BigDecimal;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.sys.GameUpdateInfo;

import com.fasterxml.jackson.core.JsonParser;

public class LaunchGameHandler extends ClientConnectHandler
{
	public LaunchGameHandler(ClientConnection client)
	{
		super(client);
	}

	@Override
	public String getType()
	{
		return Message.CLIENT_CONNECTION_LAUNCH_TYPE;
	}

	@Override
	public void handle(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		
		SimpleKnownValue<GameUpdateInfo> info = handler.listenForObject("/info", GameUpdateInfo.READER);
		SimpleKnownValue<BigDecimal> number = handler.listenForNumber("/number");
		
		SimpleParser.parseAllOfCurrentObject(handler, parser);
		
		client.launchGameGui(number.getValue().intValue(), info.getValue());
	}

}
