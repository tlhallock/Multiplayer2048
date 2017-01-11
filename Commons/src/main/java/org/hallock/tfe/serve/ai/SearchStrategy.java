package org.hallock.tfe.serve.ai;

import java.io.IOException;

import javax.swing.SwingWorker;

import org.hallock.tfe.ai.Search;
import org.hallock.tfe.ai.Search.SearchRunnable;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.serve.GameThing;

public class SearchStrategy implements ComputerAiStrategy
{
	private long freq = 2000;
	
	private Search search;
	private SearchRunnable setBoard;
	Thread t;
	
	public SearchStrategy(Search search)
	{
		this.search = search;
	}

	@Override
	public synchronized PossiblePlayerActions getNextMove()
	{
		return setBoard.getPlay();
	}

	@Override
	public void award(EvilAction awardEvilAction, GameThing game) throws IOException {}

	@Override
	public synchronized void setNewBoard(TileBoard board)
	{
		runSomeOfIt(setBoard = search.setBoard(board));
	}

	public void runAllOfIt(SearchRunnable foobar)
	{
		foobar.run();
	}

	public void runSomeOfIt(final SearchRunnable foobar)
	{
		new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				foobar.run();
				return null;
			}
		}.execute();
		
		try
		{
			Thread.sleep(freq);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		foobar.stop();
	}
}
