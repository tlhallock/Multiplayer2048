package org.hallock.tfe.ai;

import java.util.ArrayList;
import java.util.Collections;
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
		Random random = new Random(1776);
		int size = 10;
		ArrayList<Integer> x = new ArrayList<>();
		ArrayList<Integer> y = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			x.add(i > (size / 2) ? 1 : 0);
			y.add(i);
		}

		for (int i = 0; i < size; i++)
		{
			System.out.println(x.get(i) + ": " + y.get(i));
		}
		double observed = count(x, y);
		System.out.println(observed);
		
		int nIters = 100000;
		int count = 0;
		for (int j = 0; j < nIters; j++)
		{
//			System.out.println("---------------------");
			Collections.shuffle(y, random);
//			for (int i = 0; i < size; i++)
//			{
//				System.out.println(x.get(i) + ": " + y.get(i));
//			}
//			System.out.println(count(x, y));
			if (Math.abs(observed) < Math.abs(count(x,y)))
			{
				count++;
			}
		}

		System.out.println("---------------------");

		System.out.println(count / (double) nIters);
	}
}
