package org.hallock.tfe.cmn.game;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import org.hallock.tfe.serve.PointsCounter;

public class History
{
	private static final int MAX_SIZE = 100;
	LinkedList<Event> past = new LinkedList<>();
	LinkedList<Event> future = new LinkedList<>();

	public void clear()
	{
		future.clear();
		past.clear();
	}

	public void updated(PlayerState state)
	{
		future.clear();
		past.addFirst(new Event(state.getTileBoard(), state.getPoints()));
		while (MAX_SIZE > 0 && past.size() > MAX_SIZE)
		{
			past.removeLast();
		}
	}

	public boolean undo(PlayerState player)
	{
		if (past.isEmpty())
			return false;
		Event removeFirst = past.removeFirst();
		future.addFirst(removeFirst);
		if (past.isEmpty())
			return false;
		
		player.setBoard(new TileBoard(past.getFirst().state));
		player.setPoints(past.getFirst().counter);
		return true;
	}

	public boolean redo(PlayerState player)
	{
		if (future.isEmpty())
			return false;
		Event removeFirst = future.removeFirst();
		past.addFirst(removeFirst);

		player.setBoard(new TileBoard(removeFirst.state));
		player.setPoints(removeFirst.counter);
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		LinkedList<Event> l = (LinkedList<Event>) past.clone();
		Collections.reverse(l);
		for (Event e : l)
		{
			builder.append(e);
			builder.append("===================================\n");
		}

		builder.append("size = ").append(past.size()).append('\n');
		return builder.toString();
	}

	private static final class Event
	{
		TileBoard state;
		PointsCounter counter;
		long time;

		public Event(TileBoard newState, PointsCounter counter)
		{
			this.state = new TileBoard(newState);
			counter = new PointsCounter(counter);
			time = System.currentTimeMillis();
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();

			builder.append(state).append('\n');
			builder.append('"').append(counter.getPoints()).append('"').append('\n');
			builder.append(new Date(time)).append('\n');

			return builder.toString();
		}
	}
}
