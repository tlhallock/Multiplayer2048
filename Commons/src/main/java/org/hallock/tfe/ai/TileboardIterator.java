package org.hallock.tfe.ai;

import org.hallock.tfe.cmn.game.TileBoard;

public class TileboardIterator
{
	int i, j;
	int maxI, maxJ;
	TileBoard board;
	
	public TileboardIterator(TileBoard tileBoard)
	{
		maxI = tileBoard.tiles.length;
		maxJ = tileBoard.tiles[0].length;
		this.board = tileBoard;
	}
	
	public void initLeftThenUp()
	{
		j = maxJ - 1;
		i = maxI - 1;
	}
	public boolean leftThenUp()
	{
		if (--j < 0)
		{
			if (--i < 0)
				return false;
			if (i % 2 == 1)
			{
				j = maxJ - 1;
			}
			else
			{
				j = 0;
			}
		}
		return true;
	}
	
	public int current()
	{
		return board.tiles[i][j];
	}
}
