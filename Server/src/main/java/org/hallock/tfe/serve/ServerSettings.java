package org.hallock.tfe.serve;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.sys.GameConstants;

public class ServerSettings
{
	public static final int NUM_THREADS = 3;

	public static PlayerSpec DEFAULT_NEW_PLAYER = PlayerSpec.HumanPlayer;
	
	public static int LOBBY_PORT = GameConstants.LOBBY_PORT;
	
	public static GameOptions DEFAULT_GAME_OPTIONS = new GameOptions();
}
