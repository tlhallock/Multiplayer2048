package org.hallock.tfe.serve;

import java.math.BigDecimal;

public class PointsCounter
{
	BigDecimal points = BigDecimal.ZERO;
	
	private int combinesThisTurn;
	private int streakLength;
	
	public PointsCounter() {}
	
	public PointsCounter(PointsCounter counter)
	{
		this.points = counter.points;
		this.combinesThisTurn = 0;
		this.streakLength = counter.streakLength;
	}

	public BigDecimal getPoints()
	{
		return points;
	}

	public synchronized void countCombine(int toNum)
	{
		combinesThisTurn++;
		points = points.add(pointsAwardedForCombine(toNum));
	}

	public synchronized void startTurn()
	{
		combinesThisTurn = 0;
	}

	public synchronized void stopTurn()
	{
		if (combinesThisTurn > 0)
			streakLength++;
		else
			streakLength = 0;

		points = points.add(pointsAwardedForCombinations(combinesThisTurn));
		points = points.add(pointsAwardedForStreak(streakLength));
	}

	public synchronized void subtract(BigDecimal numToRemove)
	{
		points = points.subtract(numToRemove);
	}
	
	
	private static BigDecimal pointsAwardedForCombine(int toNum)
	{
		return new BigDecimal(toNum);
	}
	private static BigDecimal pointsAwardedForCombinations(int numberOfCombines)
	{
		return new BigDecimal(2 * numberOfCombines);
	}
	private static BigDecimal pointsAwardedForStreak(int length)
	{
		return new BigDecimal(5 * length);
	}
}
