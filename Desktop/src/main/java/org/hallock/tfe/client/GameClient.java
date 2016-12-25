package org.hallock.tfe.client;

import java.util.Map;
import java.util.TreeMap;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.dsktp.gui.StateView;

public class GameClient
{
	ClientConnection connection;

	Map<Integer, StateView> views = new TreeMap<Integer, StateView>();
	
	void updatePlayer(int playerNum, TileBoard newState)
	{
		StateView stateView = views.get(playerNum);
		if (stateView == null)
		{
			System.out.println("Player unknown");
		}
		else
		{
			stateView.setTileBoard(newState);
		}
	}
}
