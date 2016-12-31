package org.hallock.tfe.cmn.sys;

import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.PlayerState;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.dsktp.gui.DesktopTileBoardViewer;
import org.hallock.tfe.serve.GamePlayerInfo;
import org.hallock.tfe.serve.PointsCounter;

public class SimpleGuiGame
{
	protected History history = new History();
	protected SinglePlayerState player;
	protected GameOptions options;
	protected BigDecimal turns = BigDecimal.ZERO;
	
	protected DesktopTileBoardViewer view;

	public SimpleGuiGame(
			GameOptions options,
			DesktopTileBoardViewer view)
	{
		this.options = options;
		this.view = view;
		TileBoard state  = new TileBoard(options.numRows, options.numCols);
		state.initialize(options);
		player = new SinglePlayerState(options, state);
	}
	public SimpleGuiGame(GameOptions options,
			TileBoard start,
			DesktopTileBoardViewer view)
	{
		this.options = options;
		this.view = view;
		player = new SinglePlayerState(options, start);
	}

	public void play(PossiblePlayerActions action)
	{
		TileChanges changes = new TileChanges();
		System.out.println("Here");
		switch (action)
		{
		case Left:
			if (!player.state.left(changes).changed())
				break;
			
			player.state.fillTurn(options, changes);
			player.turnId++;
			player.changes = changes;
			player.done = !player.state.hasMoreMoves();
			player.changes.countPoints(player.counter);
			history.updated(player);
			turns = turns.add(BigDecimal.ONE);
			if (view != null)
				view.setTileBoard(player.getInfo());
			System.out.println("# turns = " + turns);
			break;

		case Right:
			if (!player.state.right(changes).changed())
				break;
			
			player.state.fillTurn(options, changes);
			player.turnId++;
			player.changes = changes;
			player.done = !player.state.hasMoreMoves();
			player.changes.countPoints(player.counter);
			history.updated(player);
			turns = turns.add(BigDecimal.ONE);
			if (view != null)
				view.setTileBoard(player.getInfo());
			System.out.println("# turns = " + turns);
			break;

		case Up:
			if (!player.state.up(changes).changed())
				break;
			
			player.state.fillTurn(options, changes);
			player.turnId++;
			player.changes = changes;
			player.done = !player.state.hasMoreMoves();
			player.changes.countPoints(player.counter);
			history.updated(player);
			turns = turns.add(BigDecimal.ONE);
			if (view != null)
				view.setTileBoard(player.getInfo());
			System.out.println("# turns = " + turns);
			break;

		case Down:
			if (!player.state.down(changes).changed())
				break;
			
			player.state.fillTurn(options, changes);
			player.turnId++;
			player.changes = changes;
			player.done = !player.state.hasMoreMoves();
			player.changes.countPoints(player.counter);
			history.updated(player);
			turns = turns.add(BigDecimal.ONE);
			if (view != null)
				view.setTileBoard(player.getInfo());
			System.out.println("# turns = " + turns);
			break;
		case Redo:
		{
			if (!history.redo(player))
				break;
			if (view != null)
				view.setTileBoard(player.getInfo());
			break;
		}
		case Undo:
		{
			if (!history.undo(player))
				break;
			if (view != null)
				view.setTileBoard(player.getInfo());
			break;
		}
		case Quit:
			System.out.println("you lose.");
			break;
		}
	}

	protected static PossiblePlayerActions translateKey(KeyEvent arg0)
	{
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_LEFT:
			return PossiblePlayerActions.Left;
		case KeyEvent.VK_RIGHT:
			return PossiblePlayerActions.Right;
		case KeyEvent.VK_UP:
			return PossiblePlayerActions.Up;
		case KeyEvent.VK_DOWN:
			return PossiblePlayerActions.Down;
		case KeyEvent.VK_R:
			return PossiblePlayerActions.Redo;
		case KeyEvent.VK_Z:
			return PossiblePlayerActions.Undo;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			return PossiblePlayerActions.Quit;
		default:
			return null;
		}
	}
	
	
	protected static class SinglePlayerState implements PlayerState
	{
		public boolean done;
		public TileBoard state;
		public PointsCounter counter;
		public TileChanges changes;
		public int turnId;
		
		public SinglePlayerState(GameOptions options, TileBoard board)
		{
			this.state = board;
			counter = options.createPoints();
		}
		@Override
		public void setPoints(PointsCounter counter)
		{
			this.counter = counter;
		}
		@Override
		public PointsCounter getPoints()
		{
			return counter;
		}
		@Override
		public void setBoard(TileBoard board)
		{
			this.state = board;
		}
		@Override
		public TileBoard getTileBoard()
		{
			return state;
		}
		public GamePlayerInfo getInfo()
		{
			GamePlayerInfo info = new GamePlayerInfo();
			info.playerNumber = 0;
			info.board = new TileBoard(getTileBoard());
			info.changes = changes;
			info.turnId = turnId;
			info.name = "Single Player";
			info.points = getPoints().getPoints();
			return info;
		}
	}
}
