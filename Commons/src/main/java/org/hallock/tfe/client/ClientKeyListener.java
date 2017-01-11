package org.hallock.tfe.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.msg.g.PlayerActionSender;

public class ClientKeyListener implements KeyListener
{
	private Connection connection;

	public ClientKeyListener(Connection connection)
	{
		this.connection = connection;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0)
	{
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_LEFT:
			try
			{
				connection.sendMessageAndFlush(new PlayerActionSender(PossiblePlayerActions.Left));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			break;
		case KeyEvent.VK_RIGHT:
			try
			{
				connection.sendMessageAndFlush(new PlayerActionSender(PossiblePlayerActions.Right));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			break;
		case KeyEvent.VK_UP:
			try
			{
				connection.sendMessageAndFlush(new PlayerActionSender(PossiblePlayerActions.Up));
			}
			catch (IOException e4)
			{
				e4.printStackTrace();
			}
			break;
		case KeyEvent.VK_DOWN:
			try
			{
				connection.sendMessageAndFlush(new PlayerActionSender(PossiblePlayerActions.Down));
			}
			catch (IOException e3)
			{
				e3.printStackTrace();
			}
			break;
		case KeyEvent.VK_R:
			try
			{
				connection.sendMessageAndFlush(new PlayerActionSender(PossiblePlayerActions.Redo));
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
			}
			break;
		case KeyEvent.VK_Z:
			try
			{
				connection.sendMessageAndFlush(new PlayerActionSender(PossiblePlayerActions.Undo));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			try
			{
				connection.sendMessageAndFlush(new PlayerActionSender(PossiblePlayerActions.Quit));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
}
