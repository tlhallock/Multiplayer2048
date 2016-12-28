package org.hallock.tfe.client;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;

public interface GameViewer
{
	void updatePlayer(int playerNumber, TileBoard board, TileChanges changes, int turnId);
	void die();
}
