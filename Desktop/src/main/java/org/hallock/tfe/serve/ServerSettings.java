package org.hallock.tfe.serve;

import org.hallock.tfe.cmn.game.GameOptions;

public class ServerSettings
{
	public static final int NUM_THREADS = 3;

	public static final long AI_WAIT = 1000;
	
	public static PlayerSpec DEFAULT_NEW_PLAYER = PlayerSpec.HumanPlayer;
	
	public static int LOBBY_PORT = 8181;
	
	public static GameOptions DEFAULT_GAME_OPTIONS = new GameOptions();
}
