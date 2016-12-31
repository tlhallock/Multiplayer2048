/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.tfe.serve;

/**
 *
 * @author trever
 */
public enum PlayerSpec
{
    Computer("Computer Player"),
    HumanPlayer("Human Player"),
    
    ;
    
    String display;
    
    PlayerSpec(String s)
    {
        this.display = s;
    }
    
    public String getDisplay()
    {
        return display;
    }
    
    public WaitingPlayer createPlayer()
	{
		switch (this)
		{
		case Computer:
			return new WaitingPlayer.ComputerWaitingPlayer();
		case HumanPlayer:
			return new WaitingPlayer.EmptyHumanPlayer();
		default:
			throw new RuntimeException();
		}
    }
    
    public PlayerSpec getPlayerSpec(String name)
    {
        for (PlayerSpec spec : PlayerSpec.values())
        {
            if (spec.display.equals(name))
                return spec;
        }
        return null;
    }
}
