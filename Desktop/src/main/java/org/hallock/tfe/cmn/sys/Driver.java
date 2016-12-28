package org.hallock.tfe.cmn.sys;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.ServerSocket;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.cmn.util.SinglePanelFrame;
import org.hallock.tfe.dsktp.gui.DesktopLobbyViewer;
import org.hallock.tfe.serve.GameServer;


public class Driver {
	public static void main(String[] args) throws IOException, InterruptedException {
		// add an option: combine multiple collapses
                
                // add in multiplayer rules
                // split up the projects...
		// clean code some
		// finish the distribution of new tiles
		// penalty for impossible move
		// set speed of ai
		
                
		
		// remove any evil action you currently have
		// possible evil moves:
		// block one cell
		// add in more new tiles
		// add a higher tile
		// remove left/right/up/down
		// add extra tiles floating for distraction
		// slow their moves down
		// add a random move
		// remove the numbers

		
		
		// write a test around a whole bunch of things happening fast in the lobby
		
		ServerSocket serverSocket = new ServerSocket(Constants.LOBBY_PORT);
		GameServer server = new GameServer(serverSocket, 3);
		server.start();

		ClientConnection client1 = new ClientConnection();
		DesktopLobbyViewer lobbyViewer1 = new DesktopLobbyViewer(client1);
		client1.setViewer(lobbyViewer1);
		client1.start();
		SinglePanelFrame showPanel = SinglePanelFrame.showPanel(lobbyViewer1, new Rectangle(50, 50, 500, 500), "Testing 1");
		lobbyViewer1.setFrame(showPanel);

		ClientConnection client2 = new ClientConnection();
		DesktopLobbyViewer lobbyViewer2 = new DesktopLobbyViewer(client2);
		client2.setViewer(lobbyViewer2);
		client2.start();
		SinglePanelFrame showPanel2 = SinglePanelFrame.showPanel(lobbyViewer2, new Rectangle(550, 50, 500, 500), "Testing 2");
		lobbyViewer2.setFrame(showPanel2);
	}

//	
//	private static void play()
//	{
//		TileView view = new TileView();
//		SinglePanelFrame.showPanel(view, new Rectangle(50,50,500,500), "Test");
//		int num = 3;
//		TileBoard state = new TileBoard(num, num);
//		state.randomlyFill(1);
//		view.setTileBoard(state);
//		
//		SimpleGuiGame simpleGuiGame = new SimpleGuiGame(view, state);
//		view.addKeyListener(simpleGuiGame);
//		
//		view.setFocusable(true);
//		view.requestFocus();
//		
//		String[] possibleMoves = new String[]{"w", "a", "s", "d"};
//		String line = possibleMoves[Constants.random.nextInt(4)];
//		System.out.println(history);
//	}
}
