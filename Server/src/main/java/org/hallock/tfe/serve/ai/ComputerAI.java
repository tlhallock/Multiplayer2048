package org.hallock.tfe.serve.ai;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

import org.hallock.tfe.ai.AiOptions;
import org.hallock.tfe.cmn.game.InGamePlayer;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.serve.Game;

public class ComputerAI implements Runnable
{
	Game game;
	boolean stop;
	HashMap<Integer, Entry> strategies = new HashMap<>();
	long aiWait;
	
	ExecutorService service;
	
	static class Entry
	{
		int number;
		AiOptions options;
		ComputerAiStrategy strategy;
		InGamePlayer player;
	}
	
	protected ComputerAI(Game game, long aiWait)
	{
		this.game = game;
		stop = false;
		this.aiWait = aiWait;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public void set(int number, AiOptions options, ComputerAiStrategy strategy, InGamePlayer player)
	{
		Entry entry = new Entry();
		entry.number = number;
		entry.options = options;
		entry.strategy = strategy;
		entry.player = player;
		
		synchronized (strategies)
		{
			strategies.put(number, entry);
		}
	}
	
	public void remove(int number)
	{
		synchronized (strategies)
		{
			strategies.remove(number);
		}
	}

	public void start()
	{
		new Thread(this).start();
	}

	public void quit()
	{
		if (stop)
			return;
		stop = true;
		synchronized (strategies)
		{
			for (int number : strategies.keySet())
				game.disconnect(number);
		}
	}
	
	@Override
	public void run()
	{
		while (!stop)
		{
			try
			{
				Thread.sleep(aiWait);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			Collection<Entry> values;

			synchronized (strategies)
			{
				values = new LinkedList<Entry>(strategies.values());
			}
			
			for (Entry entry : values)
			{
				try
				{
					game.play(entry.number, entry.strategy.getNextMove(), true);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static ComputerAI createComputerAi(Game game, long aiWait)
	{
		return new ComputerAI(game, aiWait);
	}

	public void award(int playerNumber, EvilAction awardEvilAction) throws IOException
	{
		synchronized (strategies)
		{
			Entry entry = strategies.get(playerNumber);
			if (entry == null)
				return;
			entry.strategy.award(awardEvilAction, game);
		}
	}

	public void updateBoard(int playerNumber)
	{
		synchronized (strategies)
		{
			Entry entry = strategies.get(playerNumber);
			if (entry == null)
				return;
			entry.strategy.setNewBoard(entry.player.getBoard());
		}
	}
}
