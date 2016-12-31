package org.hallock.tfe.dsktp.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.hallock.tfe.client.ClientConnection;
import org.hallock.tfe.client.ClientKeyListener;
import org.hallock.tfe.client.GameViewer;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionType;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.serve.GamePlayerInfo;
import org.hallock.tfe.serve.GameUpdateInfo;

public class DesktopGameViewer implements GameViewer, WindowListener, ComponentListener, MouseListener
{
	private static final int SCROLL_BAR_BUFFER = 30;
	
	int playerNumber;
	sdflkjsdf others = new sdflkjsdf();
	JFrame frame;
	DesktopTileBoardViewer myself;
	private ClientConnection client;

	ArrayList<DesktopTileBoardViewer> viewers = new ArrayList<>();
	HashMap<Integer, DesktopTileBoardViewer> mapping = new HashMap<>();
	HashMap<Integer, GamePlayerInfo> players = new HashMap<>();
	private JPanel actionsView;
	
	LinkedList<EvilAction> actions = new LinkedList<>();
	EvilAction currentActionDragged;
	
	
	
	@Override
	public void updatePlayer(GamePlayerInfo info)
	{
		players.put(info.playerNumber, info);

		DesktopTileBoardViewer stateView;
		if (info.playerNumber == this.playerNumber)
		{
			stateView = myself;
			synchronized (actions)
			{
				actions.clear();
				actions.addAll(info.availableActions);
			}
			updateUi();
		}
		else
		{
			stateView = mapping.get(info.playerNumber);
		}
		if (stateView == null)
		{
			System.out.println("Player unknown");
		}
		else
		{
			stateView.setTileBoard(info);
		}
	}
	
	@Override
	public void die()
	{
		frame.dispose();
	}

	@Override
	public void award(EvilAction newAction)
	{
		synchronized (actions)
		{
			actions.add(newAction);
		}
		updateUi();
	}
	
	public void sendAction(EvilAction a, int otherPlayer)
	{
		client.sendEvilAction(a, otherPlayer);
	}

	public void setAction(EvilAction action)
	{
		currentActionDragged = action;
	}
	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		DesktopTileBoardViewer viewer = getViewer(arg0);
		if (viewer == null)
		{
			return;
		}
		EvilAction a = currentActionDragged;
		if (a == null)
		{
			return;
		}

		synchronized (actions)
		{
			actions.remove(a);
			updateUi();
		}
		sendAction(a, viewer.getIndex());
	}

	private DesktopTileBoardViewer getViewer(MouseEvent arg0)
	{
		Point releaseLocation = arg0.getLocationOnScreen();
		for (DesktopTileBoardViewer viewer : viewers)
		{
			Point locationOnScreen = viewer.getLocationOnScreen();
			Rectangle positionOnScreen = new Rectangle(locationOnScreen.x, locationOnScreen.y, viewer.getWidth(), viewer.getHeight());
			if (positionOnScreen.contains(releaseLocation))
			{
				return viewer;
			}
		}

		Point locationOnScreen = myself.getLocationOnScreen();
		Rectangle positionOnScreen = new Rectangle(locationOnScreen.x, locationOnScreen.y, myself.getWidth(), myself.getHeight());
		if (positionOnScreen.contains(releaseLocation))
		{
			return myself;
		}
		return null;
	}
	

	private void updateUi()
	{
		actionsView.removeAll();
		actionsView.setLayout(null);
		
		HashMap<EvilActionType, ActionView> views = new HashMap<>();
		
		int width = actionsView.getWidth();
		int height = 200;
		int y = 0;
		
		synchronized (actions)
		{
			Collections.sort(actions, new Comparator<EvilAction>() {
				@Override
				public int compare(EvilAction o1, EvilAction o2)
				{
					return o1.getType().name().compareTo(o2.getType().name());
				}});
			for (EvilAction a : actions)
			{
				ActionView view = views.get(a.getType());
				if (view != null)
				{
					view.increment();
					continue;
				}
				view = new ActionView(this, a);
				view.setBounds(new Rectangle(0, y, width, height));
				view.addMouseListener(this);
				view.addMouseMotionListener(view);
				views.put(a.getType(), view);
				actionsView.add(view);
				y += height + 20;
			}
		}
		
		actionsView.setPreferredSize(new Dimension(width - SCROLL_BAR_BUFFER, y));
		actionsView.revalidate();
		actionsView.repaint();
	}

	
	void add(int player, GamePlayerInfo info)
	{
		DesktopTileBoardViewer desktopTileBoardViewer = new DesktopTileBoardViewer(info.name, info.playerNumber);
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

	@Override
	public void updateInfo(GameUpdateInfo info)
	{
		for (GamePlayerInfo p : info.changedPlayers)
		{
			updatePlayer(p);
		}
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

	public static GameViewer launchGameGui(ClientConnection connection, int playerNumber, GameUpdateInfo info) throws IOException
	{
		DesktopGameViewer viewer = new DesktopGameViewer();
		viewer.client = connection;
		viewer.myself = new DesktopTileBoardViewer(null, playerNumber);
		viewer.myself.start();
		viewer.others.addComponentListener(viewer.others);
		viewer.others.updateUi();
		
		viewer.frame = new JFrame();
		viewer.frame.setBounds(50, 50, 500, 500);
		viewer.frame.setTitle("Viewer");
		viewer.frame.addWindowListener(viewer);


		JPanel left = new JPanel();
		left.setBackground(Color.black);
		left.addComponentListener(viewer);
		
		// This will be the list of available evil actions...
		viewer.actionsView = new JPanel();
		viewer.actionsView.setBackground(Color.black);
		left.addComponentListener(viewer);
		
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(viewer.actionsView);
		Utils.attach(left, pane);
		
		JSplitPane inner = new JSplitPane();
		inner.setLeftComponent(viewer.others);
		inner.setRightComponent(viewer.myself);
		inner.setDividerLocation(200);

		JSplitPane outer = new JSplitPane();
		outer.setLeftComponent(left);
		outer.setRightComponent(inner);
		
		Utils.attach(viewer.frame.getContentPane(), outer);

		viewer.playerNumber = playerNumber;
		for (GamePlayerInfo pinfo : info.changedPlayers)
		{
			if (pinfo.playerNumber != playerNumber)
				viewer.add(pinfo.playerNumber, pinfo);
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
			
			int cW = width - SCROLL_BAR_BUFFER;
			int cH = cW;
			
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

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
}
