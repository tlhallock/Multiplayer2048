package org.hallock.tfe.msg.svr;

import org.hallock.tfe.msg.MessageHandler;
import org.hallock.tfe.serve.Game;
import org.hallock.tfe.serve.PlayerConnection;

public abstract class GameHandler extends MessageHandler
{
	Game game;
	PlayerConnection player;
	
	public GameHandler(Game game, PlayerConnection connection)
	{
		this.game = game;
		this.player = connection;
	}
}
