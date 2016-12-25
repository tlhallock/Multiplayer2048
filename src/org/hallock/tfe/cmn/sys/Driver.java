package org.hallock.tfe.cmn.sys;
import java.awt.Rectangle;
import java.io.IOException;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.util.SinglePanelFrame;
import org.hallock.tfe.dsktp.gui.StateView;


public class Driver {
	public static void main(String[] args) throws IOException {
		// add an option: combine multiple collapses
		play();
	}
	
	private static void play()
	{
		StateView view = new StateView();
		SinglePanelFrame.showPanel(view, new Rectangle(50,50,500,500), "Test");
		int num = 6;
		TileBoard state = new TileBoard(num, num);
		state.randomlyFill(10);
		view.setTileBoard(state);
		
		SimpleGuiGame simpleGuiGame = new SimpleGuiGame(view, state);
		view.addKeyListener(simpleGuiGame);
		
		view.setFocusable(true);
		view.requestFocus();
		
		
//		String[] possibleMoves = new String[]{"w", "a", "s", "d"};
//		String line = possibleMoves[Constants.random.nextInt(4)];
//		System.out.println(history);
	
	}
}
