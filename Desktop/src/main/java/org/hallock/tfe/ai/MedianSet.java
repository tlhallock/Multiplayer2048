package org.hallock.tfe.ai;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class MedianSet<T extends Comparable<T>, K>
{
	private LinkedList<Pair> greater = new LinkedList<>();
	private LinkedList<Pair> less    = new LinkedList<>();
	
	public Pair remove(K k)
	{
		for (Pair p : less)
		{
			if (p.k.equals(k))
			{
				less.remove(p);
				return p;
			}
		}
		for (Pair p : greater)
		{
			if (p.k.equals(k))
			{
				greater.remove(p);
				return p;
			}
		}
		return null;
	}
	
	public boolean changed(K k, T t)
	{
		Pair oldCenter = getCenter();
		MedianSet<T, K>.Pair remove = remove(k);
		if (remove == null)
		{
			return false;
		}
		remove.t = t;
		add(remove);
		Pair newCenter = getCenter();
		return oldCenter == null || newCenter == null || !oldCenter.equals(newCenter);
	}

	public Pair getCenter()
	{
		if (less.isEmpty())
			return null;
		return less.getFirst();
	}

	public void add(T t, K k)
	{
		add(new Pair(t, k));
		validate();
	}
	private void validate()
	{
		for (Pair p : less)
		{
			for (Pair p2 : greater)
			{
				if (p.compareTo(p2) > 0)
					System.out.println("Problems");
			}
		}
	}

	public void add(Pair p)
	{
		less.add(p);
		recompose();
	}

	private void recompose()
	{
		// not very efficient
		less.addAll(greater);
		greater.clear();
		Collections.sort(less);
		while (less.size() > greater.size() + 1)
		{
			greater.addFirst(less.removeLast());
		}

		// necessary?
		Collections.sort(greater);
	}
	
	public void addAll(MedianSet<T, K> other)
	{
		less.addAll(other.less);
		less.addAll(other.greater);
		recompose();
	}
	
	
	
	public final class Pair implements Comparable<Pair>
	{
		T t;
		K k;

		public Pair(T t2, K k2)
		{
			this.t = t2;
			this.k = k2;
		}

		@Override
		public int compareTo(MedianSet<T, K>.Pair o)
		{
			return t.compareTo(o.t);
		}
	}



	public int size()
	{
		return less.size() + greater.size();
	}

	public Iterator<K> iterator()
	{
		return new Iterator<K>() {
			Iterator<MedianSet<T, K>.Pair> lessIt    = less.iterator();
			Iterator<MedianSet<T, K>.Pair> greaterIt = greater.iterator();

			@Override
			public boolean hasNext()
			{
				return lessIt.hasNext() || greaterIt.hasNext();
			}

			@Override
			public K next()
			{
				if (lessIt.hasNext())
				{
					return lessIt.next().k;
				}
				return greaterIt.next().k;
			}};
	}
}
