package org.hallock.tfe.cmn.game;

import org.hallock.tfe.serve.PointsCounter;

public interface PlayerState
{
	void setPoints(PointsCounter counter);
	PointsCounter getPoints();
	
	void setBoard(TileBoard board);
	TileBoard getTileBoard();
}
