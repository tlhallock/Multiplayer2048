package org.hallock.tfe.serve;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.hallock.tfe.cmn.game.InGamePlayer;
import org.hallock.tfe.cmn.game.evil.AddHighTile;
import org.hallock.tfe.cmn.game.evil.AddInMoreTiles;
import org.hallock.tfe.cmn.game.evil.AddRandomMove;
import org.hallock.tfe.cmn.game.evil.BlockCell;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionType;
import org.hallock.tfe.cmn.game.evil.RemovePoints;
import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.sys.GameConstants;

public class EvilActionsAwarder
{
	private static final EvilActionType[] TYPES = new EvilActionType[]
	{
		EvilActionType.AddHighTile,
		EvilActionType.BlockCell,
		EvilActionType.AddInMoreTiles,
		EvilActionType.AddRandomMove,
		EvilActionType.RemovePoints,
	};
	
	TreeMap<BigDecimal, DiscreteDistribution> distributions = new TreeMap<>();
	double awardPercent = .01;
	
	
	{
		distributions.put(new BigDecimal("-100000000000"), new DiscreteDistribution(new double[] {.1, .1, .1, .1, .1}));
		distributions.put(new BigDecimal("0"            ), new DiscreteDistribution(new double[] {.1, .1, .1, .1, .1}));
		distributions.put(new BigDecimal("50"           ), new DiscreteDistribution(new double[] {.1, .1, .1, .1, .1}));
		distributions.put(new BigDecimal("500"          ), new DiscreteDistribution(new double[] {.1, .1, .1, .1, .1}));
	}

	public EvilAction awardEvilAction(InGamePlayer player)
	{
		if (GameConstants.random.nextDouble() >= awardPercent)
		{
			return null;
		}

		DiscreteDistribution ceilingEntry;
		Entry<BigDecimal, DiscreteDistribution> ceilingEntry2 = distributions.ceilingEntry(player.getPoints().getPoints());
		if (ceilingEntry2 == null)
		{
			System.out.println("It was null!!!!!");
			ceilingEntry = distributions.firstEntry().getValue();
		}
		else
		{
			ceilingEntry = ceilingEntry2.getValue();
		}
		switch (TYPES[ceilingEntry.sample()])
		{
		case AddHighTile:
			return new AddHighTile(player.getBoard().getHighestTile() + 2);
		case BlockCell:
			return new BlockCell();
		case AddInMoreTiles:
			return new AddInMoreTiles(
					new DiscreteDistribution(new int[] {1}, new double[]{1}),
					(int) (.3 * GameConstants.random.nextDouble() * player.getBoard().getNumCells()));
		case AddRandomMove:
			return new AddRandomMove(5);
		case RemovePoints:
			return new RemovePoints(
					multiplyIntegers(
							player.getPoints().getPoints(), 
							new BigDecimal(String.format("%17.17f", GameConstants.random.nextDouble() / 3))));
		default:
			throw new RuntimeException("not valid.");
		}
	}
	
	public static BigDecimal multiplyIntegers(BigDecimal d1, BigDecimal d2)
	{
		String val = d1.multiply(d2, new MathContext(
				Math.max(d1.toPlainString().length(), d2.toPlainString().length())
				)).toPlainString();
		int indexOf = val.indexOf('.');
		if (indexOf < 0)
			return new BigDecimal(val);
		val = val.substring(0, indexOf);
		return new BigDecimal(val);
		
	}
}
