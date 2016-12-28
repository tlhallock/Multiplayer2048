package org.hallock.tfe.msg.gv;

import java.io.IOException;

import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.msg.Message;

public abstract class GameViewerMessage extends Message
{
	public abstract void perform(GameViewer client) throws IOException;
}
