package org.hallock.tfe.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.msg.PlayerAction;

public class ClientKeyListener implements KeyListener
{
	private ClientConnection connection;

	ClientKeyListener(ClientConnection connection)
	{
		this.connection = connection;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0)
	{
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_LEFT:
			connection.sendMessage(new PlayerAction(PossiblePlayerActions.Left));
			break;
		case KeyEvent.VK_RIGHT:
			connection.sendMessage(new PlayerAction(PossiblePlayerActions.Right));
			break;
		case KeyEvent.VK_UP:
			connection.sendMessage(new PlayerAction(PossiblePlayerActions.Up));
			break;
		case KeyEvent.VK_DOWN:
			connection.sendMessage(new PlayerAction(PossiblePlayerActions.Down));
			break;
		case KeyEvent.VK_R:
			connection.sendMessage(new PlayerAction(PossiblePlayerActions.Redo));
			break;
		case KeyEvent.VK_Z:
			connection.sendMessage(new PlayerAction(PossiblePlayerActions.Undo));
			break;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			connection.sendMessage(new PlayerAction(PossiblePlayerActions.Quit));
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
}
