package org.hallock.tfe.cmn.game;

import org.hallock.tfe.cmn.util.DiscreteDistribution;

public class GameOptions
{
	public int numberOfNewTilesPerTurn = 1;
	public boolean skipIsAnOption;
	public DiscreteDistribution newTileDistribution;
	public int numRows;
	public int numCols;
}
