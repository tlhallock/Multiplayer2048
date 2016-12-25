package org.hallock.tfe.serve;

import java.util.LinkedList;

import org.hallock.tfe.cmn.game.GameOptions;


public class Lobby
{
	GameOptions options;
	LinkedList<Player> connectedPlayers;
	Player host;
}
