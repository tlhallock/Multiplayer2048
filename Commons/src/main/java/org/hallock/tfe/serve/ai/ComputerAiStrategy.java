package org.hallock.tfe.serve.ai;

import java.io.IOException;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.serve.GameThing;

public interface ComputerAiStrategy
{
	public PossiblePlayerActions getNextMove();
	public void award(EvilAction awardEvilAction, GameThing game) throws IOException;
	public void setNewBoard(TileBoard board);
}
