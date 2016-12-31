package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.sys.Constants;

public class ComputerAI implements Runnable
{
	
	int number;
	Game game;
	boolean stop;
	
	private PossiblePlayerActions[] possibles = new PossiblePlayerActions[] {
			PossiblePlayerActions.Down,
			PossiblePlayerActions.Up,
			PossiblePlayerActions.Left,
			PossiblePlayerActions.Right
	};

	public ComputerAI(Game game)
	{
		this.game = game;
	}

	@Override
	public void run()
	{
		while (!stop)
		{
			try
			{
				Thread.sleep(ServerSettings.AI_WAIT);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			try
			{
				game.play(number, getNextMove(), true);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private PossiblePlayerActions getNextMove()
	{
		return possibles[Constants.random.nextInt(possibles.length)];
	}

	public void start()
	{
		new Thread(this).start();
	}

	public void stop()
	{
		stop = true;
	}
}
