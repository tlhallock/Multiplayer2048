package org.hallock.tfe.cmn.sys;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.util.SinglePanelFrame;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.dsktp.gui.StateView;


public class Driver {
	public static void main(String[] args) throws IOException {
		// add an option: combine multiple collapses
		
		StateView myself = new StateView();
		StateView other  = new StateView();
		
		
		JFrame frame = new JFrame();
		frame.setBounds(50, 50, 500, 500);
		frame.setTitle("Viewer");

		// This will be the list of available evil actions...
		JPanel left = new JPanel();
		left.setBackground(Color.black);
		
		JSplitPane inner = new JSplitPane();
		inner.setLeftComponent(other);
		inner.setRightComponent(myself);
		inner.setDividerLocation(200);

		JSplitPane outer = new JSplitPane();
		outer.setLeftComponent(left);
		outer.setRightComponent(inner);
		
		Utils.attach(frame.getContentPane(), outer);
		
		frame.setVisible(true);
		
		
		
		
		
		Socket socket = new Socket();
	}
	
	private static void play()
	{
		StateView view = new StateView();
		SinglePanelFrame.showPanel(view, new Rectangle(50,50,500,500), "Test");
		int num = 3;
		TileBoard state = new TileBoard(num, num);
		state.randomlyFill(1);
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
