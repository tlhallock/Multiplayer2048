//package org.hallock.tfe.ai;
//
//import java.io.FileNotFoundException;
//import java.io.PrintStream;
//
//import javax.swing.SwingWorker;
//
//import org.hallock.tfe.ai.Search.SearchRunnable;
//import org.hallock.tfe.cmn.game.GameOptions;
//import org.hallock.tfe.cmn.game.PossiblePlayerActions;
//import org.hallock.tfe.cmn.game.TileBoard;
//import org.hallock.tfe.cmn.game.TileChanges;
//import org.hallock.tfe.cmn.sys.SimpleGuiGame;
//
//public class HeadlessEvaluator extends SimpleGuiGame
//{
//	private long freq = 1000;
//	private Search search;
//	SearchRunnable current;
//	
//	int count = 0;
//
//	public HeadlessEvaluator(GameOptions options,
//			PrintStream ps, 
//			TileBoard start,
//			Search search)
//	{
//		super(options, start, null);
//		this.search = search;
//	}
//
//
//	@Override
//	public void run()
//	{
//		while (true)
//		{
//			if (!player.state.hasMoreMoves())
//				return;
//
//			final SearchRunnable oldRunnable = current;
//			if (oldRunnable != null)
//			{
//				oldRunnable.stop();
//
//				String filename = "output/play_" + count++ + ".txt";
//				try (PrintStream log = new PrintStream(filename);)
//				{
//					TileBoard oldState = new TileBoard(player.state);
//					PossiblePlayerActions play = oldRunnable.getPlay();
//					play(play);
//					TileBoard newState = new TileBoard(player.state);
//					TileBoard n = new TileBoard(oldState);
//					switch (play)
//					{
//					case Left:
//						n.left(new TileChanges());
//						break;
//					case Right:
//						n.right(new TileChanges());
//						break;
//					case Up:
//						n.up(new TileChanges());
//						break;
//					case Down:
//						n.down(new TileChanges());
//						break;
//					}
//
//					System.out.println("Printing into " + filename);
//					log.println("Play " + play);
//					log.println("From:");
//					oldState.print(log, 2);
//					log.println("To:");
//					n.print(log, 2);
//					log.println("Actually:");
//					newState.print(log, 2);
//					log.println("Thought about moves ahead: " + oldRunnable.root.maxDepth);
//					oldRunnable.print(log);
//					System.out.println("Done");
//				}
//				catch (FileNotFoundException e)
//				{
//					e.printStackTrace();
//					return;
//				}
//
//			}
//
//			current = search.setBoard(player.state);
//			final SearchRunnable foobar = current;
//			new SwingWorker<Void, Void>()
//			{
//				@Override
//				protected Void doInBackground() throws Exception
//				{
//					foobar.run();
//					return null;
//				}
//			}.execute();
//			
//			try
//			{
//				Thread.sleep(freq);
//			}
//			catch (Throwable t)
//			{
//				t.printStackTrace();
//			}
//		}
//	}
//}
