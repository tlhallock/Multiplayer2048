package org.hallock.tfe.cmn.sys;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.dsktp.gui.DesktopTileBoardViewer;

public class SimpleGuiGame implements KeyListener
{
	DesktopTileBoardViewer view;
	TileBoard state;

	GameOptions options = new GameOptions();
	History history = new History();
	BigDecimal turns = BigDecimal.ZERO;
	int turnId;

	public SimpleGuiGame(DesktopTileBoardViewer view2, TileBoard tb)
	{
		this.view = view2;
		this.state = tb;
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		TileChanges changes = new TileChanges();
		System.out.println("Here");
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_LEFT:
			if (state.left(changes).changed())
				state.fillTurn(options, changes);
			history.updated(state, "");
			turns = turns.add(BigDecimal.ONE);
			view.setTileBoard(state, changes, turnId++);
			System.out.println("# turns = " + turns);
			break;

		case KeyEvent.VK_RIGHT:
			if (state.right(changes).changed())
				state.fillTurn(options, changes);
			history.updated(state, "");
			turns = turns.add(BigDecimal.ONE);
			view.setTileBoard(state, changes, turnId++);
			System.out.println("# turns = " + turns);
			break;

		case KeyEvent.VK_UP:
			if (state.up(changes).changed())
				state.fillTurn(options, changes);
			history.updated(state, "");
			turns = turns.add(BigDecimal.ONE);
			view.setTileBoard(state, changes, turnId++);
			System.out.println("# turns = " + turns);
			break;

		case KeyEvent.VK_DOWN:
			if (state.down(changes).changed())
				state.fillTurn(options, changes);
			history.updated(state, "");
			turns = turns.add(BigDecimal.ONE);
			view.setTileBoard(state, changes, turnId++);
			System.out.println("# turns = " + turns);
			break;
		case KeyEvent.VK_R:
		{
			TileBoard newState = history.redo();
			if (newState == null)
				break;
			state = newState;
			view.setTileBoard(state, null, 0);
		}
			break;

		case KeyEvent.VK_Z:
		{
			TileBoard newState = history.undo();
			if (newState == null)
				break;
			state = newState;
			view.setTileBoard(state, null, 0);
		}
			break;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.out.println("you lose.");
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
