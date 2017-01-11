package org.other.gen;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;


public class DiscreteDistribution
{
	private static final MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
	
	
	private double[] dist;
	int[] indices;
	BigInteger sum;
	


	public DiscreteDistribution(BigInteger[] counts)
	{
		sum = BigInteger.ZERO;
		
		int countNonzeros = 0;
		for (int i=0;i<counts.length;i++)
		{
			sum = sum.add(counts[i]);
			if (!counts[i].equals(BigInteger.ZERO))
			{
				countNonzeros++;
			}
		}
		
		BigDecimal denominator = new BigDecimal(sum);
		
//		if (denominator.compareTo(new BigDecimal(String.valueOf(Double.MAX_VALUE))) >= 0)
//		{
//			throw new RuntimeException("Too big for a double!");
//		}

		int idx = 0;
		dist    = new double[countNonzeros];
		indices = new int   [countNonzeros];
		for (int i = 0; i < counts.length; i++)
		{
			if (counts[i].equals(BigInteger.ZERO))
			{
				continue;
			}
			
			BigDecimal numerator = new BigDecimal(counts[i]);
			dist[idx] = numerator.divide(denominator, mc).doubleValue();
			indices[idx] = i;
			idx++;
		}
	}
	
//	public BigInteger getCount(int index)
//	{
//		return dist[index];
	// }

	public int sample()
	{
		double val = Math.random();
		for (int i = 0; i < dist.length; i++)
		{
			val -= dist[i];
			if (val < 0)
			{
				return indices[i];
			}
		}
		throw new RuntimeException();
	}
	
	@Override
	public String toString()
	{
		throw new RuntimeException();
	}
}
