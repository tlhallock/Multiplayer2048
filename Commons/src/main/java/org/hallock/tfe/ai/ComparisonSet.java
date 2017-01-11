package org.hallock.tfe.ai;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

public abstract class ComparisonSet<T extends Comparable<T>, K, L extends Comparable<L>>
{
	protected TreeMap<L, Pair> mapping = new TreeMap<>();
	
	public abstract boolean changed(K k, T t, L l);
	public abstract Pair getCenter();

	protected abstract void add(Pair p);

	public Iterator<K> iterator()
	{
		Iterator<ComparisonSet<T, K, L>.Pair> iteratorP = iteratorP();
		return new Iterator<K>()
		{
			@Override
			public boolean hasNext()
			{
				return iteratorP.hasNext();
			}

			@Override
			public K next()
			{
				return iteratorP.next().k;
			}
		};
	}
	public Iterator<Pair> iteratorP()
	{
		return mapping.values().iterator();
	}
	

	public void add(T t, K k, L l)
	{
		Pair p = new Pair(t, k, l);
		mapping.put(l, p);
		
		add(p);
	}
	
	public Pair find(L l)
	{
		return mapping.get(l);
	}

	public void addAll(ComparisonSet<T, K, L> other)
	{
		Iterator<ComparisonSet<T, K, L>.Pair> iteratorP = other.iteratorP();
		while (iteratorP.hasNext())
		{
			add(iteratorP.next());
		}
	}



	public int size()
	{
		return mapping.size();
	}
	
	public final class Pair implements Comparable<Pair>
	{
		final T t; // heuristic value
		final L l; // event
		final K k; // original value

		public Pair(T t2, K k2, L l)
		{
			this.t = t2;
			this.k = k2;
			this.l = l;
		}

		@Override
		public int compareTo(ComparisonSet<T, K, L>.Pair o)
		{
			return t.compareTo(o.t);
		}
	}
	
	
	
	
	
	
	
	
	
	

	public static class Worst<T extends Comparable<T>, K, L extends Comparable<L>> extends ComparisonSet<T, K, L>
	{
		private Pair minimum;

		@Override
		public boolean changed(K k, T t, L l)
		{
			mapping.remove(l);
			
			Pair value = new Pair(t, k, l);
			
			mapping.put(l, value);
			
			if (minimum == null)
			{
				minimum = value;
				return true;
			}
			else if (t.compareTo(minimum.t) < 0)
			{
				minimum = value;
				return true;
			}
			else
			{
				return false;
			}
		}

		@Override
		public Pair getCenter()
		{
			return minimum;
		}

		@Override
		public void add(Pair p)
		{
			if (minimum == null)
			{
				minimum = p;
			}
			else if (p.t.compareTo(minimum.t) < 0)
			{
				minimum = p;
			}
		}
	}
	
	public static class MedianSet<T extends Comparable<T>, K, L extends Comparable<L>> extends ComparisonSet<T, K, L>
	{
		private LinkedList<Pair> greater = new LinkedList<>();
		private LinkedList<Pair> less = new LinkedList<>();

		private Pair remove(K k)
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

		@Override
		public boolean changed(K k, T t, L l)
		{
			mapping.remove(l);
			
			Pair oldCenter = getCenter();
			Pair remove = remove(k);
			if (remove == null)
			{
				return false;
			}
			Pair newOne = new Pair(t, remove.k, remove.l);
			add(newOne);
			Pair newCenter = getCenter();
			return oldCenter == null || newCenter == null || !oldCenter.equals(newCenter);
		}

		@Override
		public Pair getCenter()
		{
			if (less.isEmpty())
				return null;
			return less.getLast();
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

		@Override
		public void add(Pair p)
		{
			less.add(p);
			recompose();
			validate();
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

//		@Override
//		public Iterator<K> iterator()
//		{
//			return new Iterator<K>()
//			{
//				Iterator<MedianSet<T, K>.Pair> lessIt = less.iterator();
//				Iterator<MedianSet<T, K>.Pair> greaterIt = greater.iterator();
//
//				@Override
//				public boolean hasNext()
//				{
//					return lessIt.hasNext() || greaterIt.hasNext();
//				}
//
//				@Override
//				public K next()
//				{
//					if (lessIt.hasNext())
//					{
//						return lessIt.next().k;
//					}
//					return greaterIt.next().k;
//				}
//			};
//		}
	}
}
