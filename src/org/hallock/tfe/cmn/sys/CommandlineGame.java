package org.hallock.tfe.cmn.sys;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.TileBoard;

public class CommandlineGame {

	private static void play() throws IOException
	{
		int num = 6;
		TileBoard state = new TileBoard(num, num);
		state.randomlyFill(10);
		History history = new History();
		BigDecimal turns = BigDecimal.ZERO;
		String[] possibleMoves = new String[]{"w", "a", "s", "d"};
		try (Scanner scanner = new Scanner(System.in)) {
			int numToFill = 1;
			while (!state.isFinished()) {
//				System.out.println(state);
//				String line = scanner.next();
				String line = possibleMoves[Constants.random.nextInt(4)];
				if (line.startsWith("a")) {
					if (state.left())
						state.randomlyFill(numToFill);
					history.updated(state, line);
					turns = turns.add(BigDecimal.ONE);
				} else if (line.startsWith("d")) {
					if (state.right())
						state.randomlyFill(numToFill);
					history.updated(state, line);
					turns = turns.add(BigDecimal.ONE);
				} else if (line.startsWith("w")) {
					if (state.up())
						state.randomlyFill(numToFill);
					history.updated(state, line);
					turns = turns.add(BigDecimal.ONE);
				} else if (line.startsWith("s")) {
					if (state.down())
						state.randomlyFill(numToFill);
					history.updated(state, line);
					turns = turns.add(BigDecimal.ONE);
				} else if (line.startsWith("z")) {
					TileBoard newState = history.undo();
					if (newState == null)
						continue;
					state = newState;
				} else if (line.startsWith("r")) {
					TileBoard newState = history.redo();
					if (newState == null)
						continue;
					state = newState;
				} else if (line.startsWith("q")) {
					break;
				}
//				System.out.println("=================================================================================");
			}
			System.out.println("you lose.");
		}
		System.out.println(history);
		System.out.println("# turns = " + turns);
	}
}
