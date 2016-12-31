//package org.hallock.tfe.cmn.sys;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Scanner;
//
//import org.hallock.tfe.cmn.game.GameOptions;
//import org.hallock.tfe.cmn.game.History;
//import org.hallock.tfe.cmn.game.TileBoard;
//import org.hallock.tfe.cmn.game.TileChanges;
//
//public class CommandlineGame
//{
//	private static void play() throws IOException
//	{
//		int num = 6;
//		TileBoard state = new TileBoard(num, num);
//		GameOptions options = new GameOptions();
//		TileChanges changes = new TileChanges();
//		state.fillTurn(options, changes);
//		History history = new History();
//		BigDecimal turns = BigDecimal.ZERO;
//		String[] possibleMoves = new String[] { "w", "a", "s", "d" };
//		try (Scanner scanner = new Scanner(System.in))
//		{
//			while (!state.isFinished())
//			{
//				changes.clear();
//				// System.out.println(state);
//				// String line = scanner.next();
//				String line = possibleMoves[Constants.random.nextInt(4)];
//				if (line.startsWith("a"))
//				{
//					if (state.left(changes).changed())
//						state.fillTurn(options, changes);
//					history.updated(state, line);
//					turns = turns.add(BigDecimal.ONE);
//				}
//				else if (line.startsWith("d"))
//				{
//					if (state.right(changes).changed())
//						state.fillTurn(options, changes);
//					history.updated(state, line);
//					turns = turns.add(BigDecimal.ONE);
//				}
//				else if (line.startsWith("w"))
//				{
//					if (state.up(changes).changed())
//						state.fillTurn(options, changes);
//					history.updated(state, line);
//					turns = turns.add(BigDecimal.ONE);
//				}
//				else if (line.startsWith("s"))
//				{
//					if (state.down(changes).changed())
//						state.fillTurn(options, changes);
//					history.updated(state, line);
//					turns = turns.add(BigDecimal.ONE);
//				}
//				else if (line.startsWith("z"))
//				{
//					TileBoard newState = history.undo();
//					if (newState == null)
//						continue;
//					state = newState;
//				}
//				else if (line.startsWith("r"))
//				{
//					TileBoard newState = history.redo();
//					if (newState == null)
//						continue;
//					state = newState;
//				}
//				else if (line.startsWith("q"))
//				{
//					break;
//				}
//				// System.out.println("=================================================================================");
//			}
//			System.out.println("you lose.");
//		}
//		System.out.println(history);
//		System.out.println("# turns = " + turns);
//	}
//}
