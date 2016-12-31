package org.hallock.tfe.cmn.sys;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.ServerSocket;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.cmn.util.SinglePanelFrame;
import org.hallock.tfe.dsktp.gui.DesktopLobbyViewer;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.ServerSettings;


public class Driver {
	public static void main(String[] args) throws IOException, InterruptedException {
		// add an option: combine multiple collapses
                
                // add in multiplayer rules
                // split up the projects...
		// clean code some
		// finish the distribution of new tiles
		// penalty for impossible move
		// set speed of ai
		

		// test that others can't change things they shouldn't be able to
		
		
		// write a test around a whole bunch of things happening fast in the lobby
		
		ServerSocket serverSocket = new ServerSocket(ServerSettings.LOBBY_PORT);
		GameServer server = new GameServer(serverSocket, ServerSettings.NUM_THREADS);
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
