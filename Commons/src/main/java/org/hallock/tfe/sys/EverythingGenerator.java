package org.hallock.tfe.sys;

public class EverythingGenerator
{
	private int[] current;
	private int highest;

	public EverythingGenerator(int length, int highest)
	{
		current = new int[length];
		this.highest = highest;
	}
	
	public void zero()
	{
		for (int i=0;i<current.length;i++)
			current[i] = 0;
	}
	
	public boolean next()
	{
		return increment(current.length-1);
	}
	private boolean increment(int i)
	{
		if (i < 0)
			return false;
		
		if (current[i] >= highest - 1)
		{
			if (!increment(i-1))
				return false;
			current[i] = 0;
			return true;
		}
		current[i]++;
		return true;
	}


	public void current(int[] assignment)
	{
		for (int i = 0; i < current.length; i++)
			assignment[i] = current[i];
	}
	
	
	
	public static void main(String[] args)
	{
		int N = 4;
		int k = 4;
		
		int[] values = new int[N];
		EverythingGenerator gen = new EverythingGenerator(N, k);
		do
		{
			gen.current(values);
			print(values);
		}
		while (gen.next());
	}
	
	
	private static void print(int[] vals)
	{
		for (int i=0;i<vals.length;i++)
			System.out.print(vals[i] + " ");
		System.out.println();
	}
	
}
