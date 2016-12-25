package org.hallock.tfe.client;

import java.util.Map;
import java.util.TreeMap;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.dsktp.gui.TileView;

public class GameClient
{
	ClientConnection connection;

	Map<Integer, TileView> views = new TreeMap<Integer, TileView>();
	
	public void updatePlayer(int playerNum, TileBoard newState)
	{
		TileView stateView = views.get(playerNum);
		if (stateView == null)
		{
			System.out.println("Player unknown");
		}
		else
		{
			stateView.setTileBoard(newState);
		}
	}

	public void add(TileView myself)
	{
		views.put(views.size(), myself);
	}
}
