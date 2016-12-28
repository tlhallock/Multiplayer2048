package org.hallock.tfe.msg.g;

import java.io.IOException;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.serve.Game;
import org.hallock.tfe.serve.PlayerConnection;

public abstract class GameMessage extends Message
{
	public abstract void perform(Game server, PlayerConnection player) throws IOException;
}
