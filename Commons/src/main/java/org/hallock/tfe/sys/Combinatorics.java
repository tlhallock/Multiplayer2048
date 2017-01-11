package org.hallock.tfe.sys;

import java.awt.Point;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.sys.CombinationGenerator.TileAssignment;

public class Combinatorics
{
	public static <G> void getRandomPermutation(G[] possible, G[] output)
	{
		if (2 * output.length < possible.length)
		{
			HashSet<Integer> hashSet = new HashSet<>();

			int index = 0;
			while (hashSet.size() < output.length)
			{
				int nextInt = GameConstants.random.nextInt(possible.length);
				if (hashSet.add(nextInt))
					output[index++] = possible[nextInt];
			}
		}
		else
		{
			ArrayList<Integer> arrayList = new ArrayList<>(possible.length);
			for (int i = 0; i < possible.length; i++)
				arrayList.add(i);
			
			Collections.shuffle(arrayList, GameConstants.random);

			for (int i = 0; i < output.length; i++)
				output[i] = possible[arrayList.get(i)];
		}
	}

	public static BigDecimal nchoosek(int n, int k)
	{
		BigDecimal numerator   = BigDecimal.ONE;
		BigDecimal denominator = BigDecimal.ONE;
		
		int count = k;
		while (count --> 0)
		{
			numerator   = numerator.multiply(new BigDecimal(n--));
			denominator = denominator.multiply(new BigDecimal(k--));
		}
		
		return numerator.divide(denominator);
	}
	
	public static BigDecimal count(int numTiles, int numValues, int numPoints)
	{
		int pointsSelected = Math.min(numTiles, numPoints);
		return nchoosek(numPoints, numTiles).multiply(
				new BigDecimal(numValues).pow(pointsSelected));
	}
	
	public static LinkedList<TileAssignment> enumerateTileAssignments(
			int numberOfTiles,
			DiscreteDistribution dist,
			LinkedList<Point> points,
			int desiredNumber)
	{
		numberOfTiles = Math.min(numberOfTiles, points.size());
		
		int[]   possibleValues = dist.getPossibleOutputs();
		Point[] possiblePoints = points.toArray(new Point[0]);

		LinkedList<TileAssignment> returnValue = new LinkedList<>();

		BigDecimal count = count(numberOfTiles, possibleValues.length, possiblePoints.length);

		if (count.compareTo(new BigDecimal(2 * desiredNumber)) > 0)
		{
			
			HashSet<TileAssignment> returnValues = new HashSet<>();
			while (returnValues.size() < returnValues.size())
			{
				Point[] chosen = new Point[numberOfTiles];
				getRandomPermutation(possiblePoints, chosen);
				int[] values = new int[numberOfTiles];
				for (int i = 0; i < values.length; i++)
					values[i] = dist.sample();
				returnValues.add(new TileAssignment(chosen, values));
			}
			returnValue.addAll(returnValues);
			return returnValue;
		}

		CombinationGenerator<Point> pointGen = new CombinationGenerator<>(possiblePoints, numberOfTiles);
		EverythingGenerator valueGen = new EverythingGenerator(numberOfTiles, possibleValues.length);
		
		Point[] pAssignment = new Point[numberOfTiles];
		int[] values = new int[numberOfTiles];
		do
		{
			pointGen.current(pAssignment);
			valueGen.zero();
			do
			{
				valueGen.current(values);
				int[] newValues = new int[numberOfTiles];
				Point[] newPoints = new Point[numberOfTiles];
				for (int i = 0; i < numberOfTiles; i++)
				{
					newValues[i] = possibleValues[values[i]];
					newPoints[i] = pAssignment[i];
				}
				returnValue.add(new TileAssignment(newPoints, newValues));
			}
			while (valueGen.next());
		}
		while (pointGen.next());
		
		Collections.shuffle(returnValue, GameConstants.random);
		while (returnValue.size() > desiredNumber)
		{
			returnValue.removeLast();
		}

		return returnValue;
	}
	
	public static void main(String[] args)
	{
		DiscreteDistribution discreteDistribution = new DiscreteDistribution(new int[] { 2, 4 }, new double[] { .75, .25 });
		LinkedList<Point> points = new LinkedList<>();
		for (Point p : new Point[]{
				new Point(0,0),
				new Point(0,1),
				new Point(0,2),
				new Point(1,0),
				new Point(1,1),
				new Point(1,2),
		})
			points.add(p);
		
		for (TileAssignment t : enumerateTileAssignments(2, discreteDistribution, points, 50))
		{
			System.out.println(t);
		}
	}
	


//	public static void main(String[] args)
//	{
//		int N = 10;
//		int k = 9;
//		Integer[] values = new Integer[N];
//		for (int i = 0; i < N; i++)
//			values[i] = i + 1;
//
//		CombinationGenerator<Integer> gen = new CombinationGenerator<>(values, k);
//
//		Integer[] current = new Integer[Math.max(0, k)];
//		do
//		{
//			gen.current(current);
//			print(current);
//		}
//		while (gen.next());
//	}
}
