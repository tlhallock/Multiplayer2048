
package org.hallock.tfe.cmn.util;

import java.util.ArrayList;


public class DiscreteDistribution
{
	ArrayList<Integer> outcomes = new ArrayList<>();
	ArrayList<Double> probabilities = new ArrayList<>();
	
	public DiscreteDistribution(int[] o, double[] probs)
	{
		
	}
	
	public DiscreteDistribution()
	{
	}

    public DiscreteDistribution(DiscreteDistribution newTileDistribution) {
    }

	public int sample()
	{
		return 1;
	}
}
