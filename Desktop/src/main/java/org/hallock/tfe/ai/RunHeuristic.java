package org.hallock.tfe.ai;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.cmn.util.SinglePanelFrame;
import org.hallock.tfe.dsktp.gui.DesktopTileBoardViewer;

public class RunHeuristic
{
	public static void main(String[] args) throws FileNotFoundException, InterruptedException
	{
//		Heuristic<Integer> h = new Heuristic<Integer>()
//		{
//			@Override
//			public Integer assess(int depth, TileBoard board)
//			{
//				return depth;
//			}
//		};
//		LeftRightHueristic h2 = new LeftRightHueristic();
		Heuristic h;
		int size = 4;
		int count = 0;
		while (true)
		{
			count++;
			if (size == 4)
			{
				h = LocationalHeuristic.createHalfSnake();
				testHeuristic(String.valueOf(count) + "_" + size + "x" + size + "_halfsnake_" + System.currentTimeMillis(), h, size);
			}
			h = LocationalHeuristic.createRealV(size);
			testHeuristic(String.valueOf(count) + "_" + size + "x" + size + "_real_V_" + System.currentTimeMillis(), h, size);
			h = LocationalHeuristic.createV(size);
			testHeuristic(String.valueOf(count) + "_" + size + "x" + size + "_V_" + System.currentTimeMillis(), h, size);
			h = LocationalHeuristic.createDiags(size);
			testHeuristic(String.valueOf(count) + "_" + size + "x" + size + "_diags_" + System.currentTimeMillis(), h, size);
			h = LocationalHeuristic.createRows(size);
			testHeuristic(String.valueOf(count) + "_" + size + "x" + size + "_rows_" + System.currentTimeMillis(), h, size);
			h = LocationalHeuristic.createSnake(size);
			testHeuristic(String.valueOf(count) + "_" + size + "x" + size + "_snake_" + System.currentTimeMillis(), h, size);
			h = new LocationalHeuristic(size, size);
			testHeuristic(String.valueOf(count) + "_" + size + "x" + size + "_random_" + System.currentTimeMillis(), h, size);
			if (size == 4)
			{
				h = LocationalHeuristic.createSunnys();
				testHeuristic(String.valueOf(count) + size + "x" + size + "_snake_" + System.currentTimeMillis(), h, size);
			}
		}
	}
	
	
	private static void testHeuristic(String name, Heuristic h, int size)
	{
		GameOptions options = new GameOptions();
		options.newTileDistribution = new DiscreteDistribution(new int[] {1}, new double[] {1});
		options.numCols = size;
		options.numRows = size;
		TileBoard board = new TileBoard(size, size);
		board.tiles[size-1][size-1] = 1;

		Search search = new Search(h);
		DesktopTileBoardViewer viewer = new DesktopTileBoardViewer("Computer", 0);
		SinglePanelFrame showPanel = SinglePanelFrame.showPanel(viewer, new Rectangle(50, 50, 750, 750), "name");
		viewer.start();
		System.out.println(board);
		ComputerGame game = new ComputerGame(options, viewer, board, search, name);
		game.run();
		viewer.stop();
		showPanel.dispose();
		
		try (PrintStream ps = new PrintStream("statistics/"  + name + ".alldone");)
		{
			ps.println("yup");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private static void someTest()
	{


//		int num = -3;
//		board.tiles[0][0] = Math.max(0, num++);
//		board.tiles[1][0] = Math.max(0, num++);
//		board.tiles[2][0] = Math.max(0, num++);
//		board.tiles[2][1] = Math.max(0, num++);
//		board.tiles[1][1] = Math.max(0, num++);
//		board.tiles[0][1] = Math.max(0, num++);
//		board.tiles[0][2] = Math.max(0, num++);
//		board.tiles[1][2] = Math.max(0, num++);
//		board.tiles[2][2] = Math.max(0, num++);
//		int numThreads = 1;
//		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(numThreads);
//		for (int i = 0; i < numThreads; i++)
//		{
//			newFixedThreadPool.submit(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					search.run();
//				}
//			});
//		}
		
//		board.print(System.out, 1);
//		TileboardIterator tileboardIterator = new TileboardIterator(board);
//		tileboardIterator.initLeftThenUp();
//		
//		do
//		{
//			System.out.println(tileboardIterator.current());
//		} while (tileboardIterator.leftThenUp());
//		
	}
}
