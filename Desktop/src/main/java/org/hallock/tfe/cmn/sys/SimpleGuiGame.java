package org.hallock.tfe.cmn.sys;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;

import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.dsktp.gui.TileView;

public class SimpleGuiGame implements KeyListener
{
	TileView view;
	TileBoard state;

	int numToFill = 1;
	History history = new History();
	BigDecimal turns = BigDecimal.ZERO;

	public SimpleGuiGame(TileView view2, TileBoard tb)
	{
		this.view = view2;
		this.state = tb;
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		System.out.println("Here");
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_LEFT:
			if (state.left())
				state.randomlyFill(numToFill);
			history.updated(state, "");
			turns = turns.add(BigDecimal.ONE);
			view.setTileBoard(state);
			System.out.println("# turns = " + turns);
			break;

		case KeyEvent.VK_RIGHT:
			if (state.right())
				state.randomlyFill(numToFill);
			history.updated(state, "");
			turns = turns.add(BigDecimal.ONE);
			view.setTileBoard(state);
			System.out.println("# turns = " + turns);
			break;

		case KeyEvent.VK_UP:
			if (state.up())
				state.randomlyFill(numToFill);
			history.updated(state, "");
			turns = turns.add(BigDecimal.ONE);
			view.setTileBoard(state);
			System.out.println("# turns = " + turns);
			break;

		case KeyEvent.VK_DOWN:
			if (state.down())
				state.randomlyFill(numToFill);
			history.updated(state, "");
			turns = turns.add(BigDecimal.ONE);
			view.setTileBoard(state);
			System.out.println("# turns = " + turns);
			break;
		case KeyEvent.VK_R:
		{
			TileBoard newState = history.redo();
			if (newState == null)
				break;
			state = newState;
			view.setTileBoard(state);
		}
			break;

		case KeyEvent.VK_Z:
		{
			TileBoard newState = history.undo();
			if (newState == null)
				break;
			state = newState;
			view.setTileBoard(state);
		}
			break;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.out.println("you lose.");
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}
}
