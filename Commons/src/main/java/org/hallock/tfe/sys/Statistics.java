package org.hallock.tfe.sys;

import java.util.HashMap;

import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.game.TileChanges.TileChange;

public class Statistics
{
	HashMap<Integer, Integer> highestTile = new HashMap<>();
	
	
	public void update(int player, TileChanges newState)
	{
		for (TileChange change : newState)
		{
			
		}
	}
	
	
	
}
