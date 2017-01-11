package org.hallock.tfe.sys;

import java.awt.Point;

public class CombinationGenerator<T>
{
	private int[] current;
	private T[] values;

	public CombinationGenerator(T[] values, int k)
	{
		k = Math.max(0, Math.min(values.length, k));

		this.values = values;
		this.current = new int[k];
		for (int i = 0; i < current.length; i++)
			current[i] = i;
	}
	
	public int getOutputSize()
	{
		return current.length;
	}

	public boolean next()
	{
		return increment(current.length - 1);
	}

	private boolean increment(int i)
	{
		if (i < 0)
			return false;

		if (++current[i] >= values.length - (current.length - i - 1))
		{
			if (!increment(i - 1))
				return false;
			current[i] = current[i - 1] + 1;
			if (current[i] >= values.length)
				return false;
		}

		return true;
	}

	public void current(T[] copyInto)
	{
		for (int i = 0; i < current.length; i++)
			copyInto[i] = values[current[i]];
	}
	
	public static class TileAssignment implements Comparable<TileAssignment>
	{
		Point[] points;
		int[] values;
		
		public TileAssignment(Point[] points, int[] values)
		{
			this.points = points;
			this.values = values;
			
			if (points.length != values.length)
				throw new RuntimeException();
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < points.length; i++)
			{
				builder.append("{" + points[i].x + "," + points[i].y + "->" + values[i] + "}");
			}

			return builder.toString();
		}
		
		@Override
		public int hashCode()
		{
			return toString().hashCode();
		}
		@Override
		public boolean equals(Object other)
		{
			if (!(other instanceof TileAssignment))
				return false;
			TileAssignment o = (TileAssignment) other;
			if (points.length != o.points.length)
				return false;
			for (int i = 0; i < points.length; i++)
			{
				if (!points[i].equals(o.points[i]))
					return false;
				if (values[i] != o.values[i])
					return false;
			}
			return true;
		}
		
		@Override
		public int compareTo(TileAssignment other)
		{
			int cmp;
			
			cmp = Integer.compare(points.length, other.points.length);
			if (cmp != 0)
				return cmp;

			for (int i = 0; i < points.length; i++)
			{
				cmp = Integer.compare(values[i], other.values[i]);
				if (cmp != 0)
					return cmp;
				cmp = Integer.compare(points[i].x, other.points[i].x);
				if (cmp != 0)
					return cmp;
				cmp = Integer.compare(points[i].y, other.points[i].y);
				if (cmp != 0)
					return cmp;
			}
			return 0;
		}

		public int length()
		{
			return points.length;
		}
		
		public Point getPoint(int index)
		{
			return points[index];
		}
		
		public int getValue(int index)
		{
			return values[index];
		}
	}
}
