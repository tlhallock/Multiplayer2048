package org.hallock.tf.serve;

import java.util.LinkedList;

import org.hallock.tfe.cmn.game.GameOptions;


public class Lobby
{
	GameOptions options;
	LinkedList<PlayerConnection> connectedPlayers;
	PlayerConnection host;
}
