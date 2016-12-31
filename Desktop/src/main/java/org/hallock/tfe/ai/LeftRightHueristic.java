package org.hallock.tfe.ai;

import org.hallock.tfe.cmn.game.TileBoard;

public class LeftRightHueristic implements Heuristic<SnakeSequence>
{
	@Override
	public SnakeSequence assess(int depth, TileBoard board)
	{
		int[] cells = new int[board.getNumCells()];
		
		TileboardIterator it = new TileboardIterator(board);
		it.initLeftThenUp();
		int idx = 0;
		do
		{
			cells[idx] = it.current();
		} while (++idx < cells.length && it.leftThenUp());
		
		SnakeSequence snakeSequence = new SnakeSequence(cells);

//		System.out.println(snakeSequence);
//		board.print(System.out, 5);
		
		return snakeSequence;
	}
}
