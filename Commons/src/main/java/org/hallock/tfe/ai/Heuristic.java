package org.hallock.tfe.ai;

import org.hallock.tfe.cmn.game.TileBoard;

public interface Heuristic<T extends Comparable<T>>
{
	public T assess(int depth, TileBoard board);
}
