package org.hallock.tfe.dsktp.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.client.ClientKeyListener;
import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.serve.Lobby.LobbyInfo;
import org.hallock.tfe.serve.PlayerInfo;

public class DesktopGameViewer implements GameViewer, WindowListener
{
	int playerNumber;
	sdflkjsdf others = new sdflkjsdf();
	JFrame frame;
	DesktopTileBoardViewer myself;
	private ClientConnection client;

	ArrayList<DesktopTileBoardViewer> viewers = new ArrayList<>();
	HashMap<Integer, DesktopTileBoardViewer> mapping = new HashMap<>();
	HashMap<Integer, PlayerInfo> players = new HashMap<>();
	
	
	
	
	
	
	@Override
	public void updatePlayer(int playerNum, TileBoard newState, TileChanges changes, int turnId)
	{
		DesktopTileBoardViewer stateView;
		if (playerNum == this.playerNumber)
		{
			stateView = myself;
		}
		else
		{
			stateView = mapping.get(playerNum);
		}
		if (stateView == null)
		{
			System.out.println("Player unknown");
		}
		else
		{
			stateView.setTileBoard(newState, changes, turnId);
		}
	}
	
	@Override
	public void die()
	{
		frame.dispose();
	}

	
	void add(int player, PlayerInfo info)
	{
		DesktopTileBoardViewer desktopTileBoardViewer = new DesktopTileBoardViewer(info.name);
		mapping.put(player, desktopTileBoardViewer);
		players.put(player, info);
		viewers.add(desktopTileBoardViewer);
		desktopTileBoardViewer.start();
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		myself.stop();
		for (DesktopTileBoardViewer v : mapping.values())
		{
			v.stop();
		}
		client.died(this);
	}
	


	public static GameViewer launchGameGui(ClientConnection connection, int playerNumber, LobbyInfo info) throws IOException
	{
		DesktopGameViewer viewer = new DesktopGameViewer();
		viewer.client = connection;
		viewer.myself = new DesktopTileBoardViewer(null);
		viewer.myself.start();
		viewer.others.addComponentListener(viewer.others);
		viewer.others.updateUi();
		
		viewer.frame = new JFrame();
		viewer.frame.setBounds(50, 50, 500, 500);
		viewer.frame.setTitle("Viewer");
		viewer.frame.addWindowListener(viewer);

		// This will be the list of available evil actions...
		JPanel left = new JPanel();
		left.setBackground(Color.black);
		
		JSplitPane inner = new JSplitPane();
		inner.setLeftComponent(viewer.others);
		inner.setRightComponent(viewer.myself);
		inner.setDividerLocation(200);

		JSplitPane outer = new JSplitPane();
		outer.setLeftComponent(left);
		outer.setRightComponent(inner);
		
		Utils.attach(viewer.frame.getContentPane(), outer);

		viewer.playerNumber = playerNumber;
		for (PlayerInfo pinfo : info.players)
		{
			if (pinfo.gameNumber != playerNumber)
				viewer.add(pinfo.gameNumber, pinfo);
		}
		
		ClientKeyListener listener = new ClientKeyListener(connection.getConnection());
		viewer.myself.addKeyListener(listener);
		viewer.myself.setFocusable(true);
		viewer.myself.requestFocus();

		viewer.frame.setVisible(true);
		
		return viewer;
	}
	
	private class sdflkjsdf extends JPanel implements ComponentListener
	{
		
		JPanel innerPanel;
		JScrollPane pane;
		
		{
			innerPanel = new JPanel();
			innerPanel.setBounds(0, 0, 50, 50);
			
			pane = new JScrollPane();
			Utils.attach(this, pane);
			pane.setViewportView(innerPanel);
		}

		@Override
		public void componentHidden(ComponentEvent e) {}
		@Override
		public void componentMoved(ComponentEvent e) {}
		@Override
		public void componentShown(ComponentEvent e) {}

		@Override
		public void componentResized(ComponentEvent e)
		{
			updateUi();
		}

		private void updateUi()
		{
			innerPanel.removeAll();
			innerPanel.setLayout(null);
			int width = getWidth();
			
			int buffer = 30;
			int cW = width - buffer;
			int cH = width - buffer;
			
			int y = 0;
			for (DesktopTileBoardViewer viewer : viewers)
			{
				innerPanel.add(viewer);
				viewer.setBounds(0, y, cW, cH);
				viewer.setPreferredSize(new Dimension(cW, cH));
				viewer.repaint();
				y += cH + 50;
			}
			// bad guess for the width...
			innerPanel.setPreferredSize(new Dimension(cW, y));
			revalidate();
			repaint();
		}
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent e)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}
