package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.dsktp.gui.LobbyViewer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LCLaunchGame extends LCLobbyClientMessage
{
	public LCLaunchGame() {}
	
	public LCLaunchGame(JsonParser parser) throws IOException
	{
		while (!parser.nextToken().equals(JsonToken.END_OBJECT))
			;
	}
	
	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField(TYPE_FIELD, TYPE);
		generator.writeEndObject();
	}

	public static final String TYPE = "open_game";

	@Override
	public void perform(LobbyViewer lobbyViewer) throws IOException
	{
		lobbyViewer.startGame();
	}
}
