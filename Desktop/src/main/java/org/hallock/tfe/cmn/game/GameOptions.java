package org.hallock.tfe.cmn.game;

import org.hallock.tfe.cmn.util.DiscreteDistribution;

public class GameOptions
{
	public int numberOfNewTilesPerTurn = 1;
	public int startingTiles = 3;
	public boolean skipIsAnOption;
	public DiscreteDistribution newTileDistribution = new DiscreteDistribution();
	public int numRows = 6;
	public int numCols = 6;
}
