package org.hallock.tfe.ai;

public class SnakeSequence implements Comparable<SnakeSequence>
{
	int[] comparison;
	
	SnakeSequence(int[] comp)
	{
		this.comparison = comp;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < comparison.length; i++)
			builder.append(comparison[i]).append(',');
		return builder.toString();
	}

	@Override
	public int compareTo(SnakeSequence arg0)
	{
		for (int i = 0; i < comparison.length; i++)
		{
			int compare = Integer.compare(comparison[i], arg0.comparison[i]);
			if (compare != 0)
				return compare;
		}
		return 0;
	}
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof SnakeSequence))
			return false;
		SnakeSequence s = (SnakeSequence) other;
		if (comparison.length != s.comparison.length)
			return false;
		for (int i = 0; i < comparison.length; i++)
			if (comparison[i] != s.comparison[i])
				return false;
		return true;
	}
}
