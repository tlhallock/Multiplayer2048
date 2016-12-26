package org.hallock.tfe.cmn.sys;
import java.awt.Rectangle;
import java.io.IOException;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.util.SinglePanelFrame;
import org.hallock.tfe.dsktp.gui.LobbyViewer;
import org.hallock.tfe.dsktp.gui.TileView;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.LobbyServer;


public class Driver {
	public static void main(String[] args) throws IOException, InterruptedException {
		// add an option: combine multiple collapses
                
                // make the lobby browser
                // convert the print writers to json
                // make a window that can show any number of players
                // add in multiplayer rules
                // split up the projects...
                
		LobbyServer.launch();
		
		
		LobbyViewer lobbyViewer = new LobbyViewer();
		SinglePanelFrame.showPanel(lobbyViewer, new Rectangle(50,50,500,500), "Testing");
		
	}

	private static void playAGame() throws InterruptedException
	{
		new Thread(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					GameServer.launchServer();
				}
				catch (InterruptedException | IOException e)
				{
					e.printStackTrace();
				}
			}}).start();
		Thread.sleep(1000);

		new Thread(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					ClientConnection.launch();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}}).start();

		new Thread(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					ClientConnection.launch();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}}).start();
	}
	
	private static void play()
	{
		TileView view = new TileView();
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
