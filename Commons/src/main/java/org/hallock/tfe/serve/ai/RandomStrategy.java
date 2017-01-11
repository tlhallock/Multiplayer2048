package org.hallock.tfe.serve.ai;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.serve.GameThing;
import org.hallock.tfe.sys.GameConstants;

public class RandomStrategy implements ComputerAiStrategy
{
	private PossiblePlayerActions[] possibles = new PossiblePlayerActions[] {
			PossiblePlayerActions.Down,
			PossiblePlayerActions.Up,
			PossiblePlayerActions.Left,
			PossiblePlayerActions.Right,
	};
	
	private int number;
	
	public RandomStrategy(int number)
	{
		this.number = number;
	}

	@Override
	public PossiblePlayerActions getNextMove()
	{
		return possibles[GameConstants.random.nextInt(possibles.length)];
	}

	@Override
	public void award(EvilAction awardEvilAction, GameThing game) throws IOException
	{
		LinkedList<Integer> others = new LinkedList<>();
		for (int i = 0; i < game.getNumberOfPlayers(); i++)
		{
			if (i != number)
				others.add(i);
		}
		if (others.isEmpty())
			return;
		Collections.shuffle(others, GameConstants.random);
		game.playAction(number, awardEvilAction, others.removeFirst());
	}

	@Override
	public void setNewBoard(TileBoard board) {}
}
