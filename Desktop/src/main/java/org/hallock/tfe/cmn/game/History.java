package org.hallock.tfe.cmn.game;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;


public class History 
{
	private static final int MAX_SIZE = 100;
	LinkedList<Event> past = new LinkedList<>();
	LinkedList<Event> future = new LinkedList<>();
	
	public void updated(TileBoard newState, String message)
	{
		future.clear();
		past.addFirst(new Event(newState, message));
		while (MAX_SIZE > 0 && past.size() > MAX_SIZE)
		{
			past.removeLast();
		}
	}
	
	public TileBoard undo()
	{
		if (past.isEmpty())
			return null;
		Event removeFirst = past.removeFirst();
		future.addFirst(removeFirst);
		if (past.isEmpty())
			return null;
		return new TileBoard(past.getFirst().state);
	}
	public TileBoard redo()
	{
		if (future.isEmpty())
			return null;
		Event removeFirst = future.removeFirst();
		past.addFirst(removeFirst);
		return new TileBoard(removeFirst.state);
	}

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
		String message;
		long time;
		
		Event(TileBoard state, String message)
		{
			this.state = new TileBoard(state);
			this.message = message;
			time = System.currentTimeMillis();
		}
		
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			
			builder.append(state).append('\n');
			builder.append('"').append(message).append('"').append('\n');
			builder.append(new Date(time)).append('\n');
			
			return builder.toString();
		}
	}
}
