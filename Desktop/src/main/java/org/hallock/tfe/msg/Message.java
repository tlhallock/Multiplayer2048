package org.hallock.tfe.msg;

import java.io.PrintWriter;
import java.util.Scanner;

public abstract class Message
{
	public abstract void write(PrintWriter writer);
	
	public static Message parse(Scanner scanner)
	{
		String next = scanner.next();
		System.out.println("Reading " + next);
		switch (next)
		{
		case GameStateChanged.TYPE:
			return new GameStateChanged(scanner);
		case PlayerAction.TYPE:
			return new PlayerAction(scanner);
		default:
			throw new RuntimeException("Unrecognized type: " + next);
		}
	}
}
