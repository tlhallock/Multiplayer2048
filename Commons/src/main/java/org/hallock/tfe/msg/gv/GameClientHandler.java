package org.hallock.tfe.msg.gv;

import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.msg.MessageHandler;

public abstract class GameClientHandler extends MessageHandler
{
	GameViewer client;

	public GameClientHandler(GameViewer client)
	{
		this.client = client;
	}
}
