package org.hallock.tfe.ai;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.SwingWorker;

import org.hallock.tfe.ai.Search.SearchRunnable;
import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.sys.SimpleGuiGame;
import org.hallock.tfe.dsktp.gui.DesktopTileBoardViewer;

public class ComputerGame extends SimpleGuiGame implements Runnable
{
	private long freq = 1000;
	private Search search;
	protected String name;
	
	int count = 0;

	public ComputerGame(GameOptions options,
			DesktopTileBoardViewer view,
			TileBoard start,
			Search search,
			String name)
	{
		super(options, start, view);
		this.search = search;
		this.name = name;
	}


	@Override
	public void run()
	{
		Robot robot = null;
		try
		{
			robot = new Robot();
		}
		catch (AWTException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (player.state.hasMoreMoves())
		{
			Point location = MouseInfo.getPointerInfo().getLocation();
			int delta = 10;
			robot.mouseMove(
					Math.max(0, Math.min(500, location.x + delta / 2 - Constants.random.nextInt(delta))), 
					Math.max(0, Math.min(500, location.y + delta / 2 - Constants.random.nextInt(delta))));
			
			SearchRunnable current = search.setBoard(player.state);

			if (true)
			{
				runAllOfIt(current);
			}
			else
			{
				runSomeOfIt(current);
			}

			String filename = "statistics/" + name + "_play_" + count++ + ".txt";
			
			try (PrintStream log = new PrintStream(filename);)
			{
				TileBoard oldState = new TileBoard(player.state);
				PossiblePlayerActions play = current.getPlay();
				play(play);
				TileBoard newState = new TileBoard(player.state);
				TileBoard n = new TileBoard(oldState);
				switch (play)
				{
				case Left:
					n.left(new TileChanges());
					break;
				case Right:
					n.right(new TileChanges());
					break;
				case Up:
					n.up(new TileChanges());
					break;
				case Down:
					n.down(new TileChanges());
					break;
				}

				System.out.println("Printing into " + filename);
				log.println("Play " + play);
				log.println("From:");
				oldState.print(log, 2);
				log.println("To:");
				n.print(log, 2);
				log.println("Actually:");
				newState.print(log, 2);
				log.println("Thought about moves ahead: " + current.root.maxDepth);
				current.print(log);
				System.out.println("Done");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	private void runSomeOfIt(final SearchRunnable foobar)
	{
		foobar.run();
	}

	private void runAllOfIt(final SearchRunnable foobar)
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
