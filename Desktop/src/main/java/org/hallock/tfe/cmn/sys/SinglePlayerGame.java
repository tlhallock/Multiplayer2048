package org.hallock.tfe.cmn.sys;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.sys.SinglePlayerGame.HumanSinglePlayer;
import org.hallock.tfe.dsktp.gui.DesktopTileBoardViewer;

public class SinglePlayerGame extends SimpleGuiGame<HumanSinglePlayer> implements KeyListener
{
	public SinglePlayerGame(GameOptions options, DesktopTileBoardViewer view) throws IOException
	{
		super(options, view);
		view.addKeyListener(this);
		add(createPlayer());
	}

	@Override
	protected HumanSinglePlayer createPlayer()
	{
		return new HumanSinglePlayer();
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
	
	static class HumanSinglePlayer extends SimpleGuiGame.SinglePlayer
	{
		@Override
		public void createWriter() throws IOException
		{
			
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		PossiblePlayerActions action = translateKey(arg0);
		if (action == null)
			return;
		play(action);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
}
