package org.hallock.tfe.msg;

import java.io.PrintWriter;
import java.util.Scanner;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.serve.GameServer;

public class PlayerAction extends ServerMessage
{
	PossiblePlayerActions swipe;
	
	public PlayerAction(PossiblePlayerActions swipe)
	{
		this.swipe = swipe;
	}
	
	PlayerAction(Scanner scanner)
	{
		String next = scanner.next();
		for (PossiblePlayerActions s : PossiblePlayerActions.values())
			if (s.name().equals(next))
			{
				swipe = s;
				break;
			}
		if (swipe == null)
			throw new RuntimeException("Unrecognized: " + next);
	}

	@Override
	public void write(PrintWriter writer)
	{
		writer.print(TYPE + " ");
		writer.print(swipe.name() + " ");
	}
	
	public static final String TYPE = "playeraction";

	@Override
	public void perform(int playerNum, GameServer server)
	{
		server.play(playerNum, swipe);
	}
}
