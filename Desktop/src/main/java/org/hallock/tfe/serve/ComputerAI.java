package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.sys.Constants;

public class ComputerAI implements Runnable
{
	
	int number;
	Game game;
	
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
		while (true)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			try
			{
				game.play(number, getNextMove());
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
}
