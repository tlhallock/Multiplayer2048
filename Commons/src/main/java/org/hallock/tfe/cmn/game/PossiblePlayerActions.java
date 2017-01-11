package org.hallock.tfe.cmn.game;

public enum PossiblePlayerActions
{
	Left, 
	Right,
	Up, 
	Down, 
	Redo,
	Undo, 
	Quit,
	Disconnect,
	ShowAllTileBoards,
	
	
	
	;
	
	
	public TileChanges perform(TileBoard board, TileChanges changes)
	{
		switch (this)
		{
		case Left:
			return board.left(changes);
		case Right:
			return board.right(changes);
		case Up:
			return board.up(changes);
		case Down:
			return board.down(changes);
		default:
			throw new RuntimeException();
		}
	}
	public boolean perform(History history, InGamePlayer board)
	{
		switch (this)
		{
		case Redo:
			return history.redo(board);
		case Undo:
			return history.undo(board);
		default:
			throw new RuntimeException();
		}
	}
}
