package org.hallock.tfe.other;

import java.util.ArrayList;
import java.util.Random;

public class Shuffle
{

	private static double count(ArrayList<Integer> x, ArrayList<Integer> y)
	{
		int count1 = 0;
		int count2 = 0;
		
		int sum1 = 0;
		int sum2 = 0;

		for (int i = 0; i < x.size(); i++)
		{
			if (x.get(i) == 0)
			{
				sum1 += y.get(i);
				count1++;
			}
			else
			{
				sum2 += y.get(i);
				count2++;
			}
		}
		
		
		return (sum1 / (double) count1) - (sum2 / (double) count2);
	}
	
	public static void main(String[] args)
	{
		int bigCount = 0;
		int observed = 12;
		
		
		int N = 100000;
		int M = 500;

		double obs = Math.abs(observed) / M;
		for (int i=0;i<N;i++)
		{
			int count = 0;
			for (int j=0;j<M;j++)
			{
				if (Math.random() < .5)
				{
					count++;
				}
			}
			double delta = Math.abs(count / (double) M - .5);
			if (obs < delta)
			{
				bigCount++;
			}
		}

		System.out.println(bigCount / (double) N);
		
		
		
		
		
		
		if (true)
			return;
		
		
		Random random = new Random(1776);
		int size = 10;
		ArrayList<Integer> x = new ArrayList<>();
		ArrayList<Integer> y = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			x.add(i > (size / 2) ? 1 : 0);
			y.add(i + (int)(random.nextDouble() * 3));
		}

		for (int i = 0; i < size; i++)
		{
			System.out.println(x.get(i) + ": " + y.get(i));
		}
//		double observed = count(x, y);
//		System.out.println(observed);
//		
//		int nIters = 5000000;
//		int count = 0;
//		for (int j = 0; j < nIters; j++)
//		{
////			System.out.println("---------------------");
//			Collections.shuffle(y, random);
////			for (int i = 0; i < size; i++)
////			{
////				System.out.println(x.get(i) + ": " + y.get(i));
////			}
////			System.out.println(count(x, y));
//			if (Math.abs(observed) < Math.abs(count(x,y)))
//			{
//				count++;
//			}
//		}
//
//		System.out.println("---------------------");
//
//		System.out.println(count / (double) nIters);
	}
}
