package org.other.gen;

import java.math.BigInteger;

import org.hallock.tfe.cmn.util.AnotherUtils;

public abstract class Terminal implements erminal
{
	String str;
	BigInteger num;

	public Terminal(String str, long count)
	{
		super();
		this.str = str;
		this.num = new BigInteger(String.valueOf(count));
	}

	@Override
	public String toString()
	{
		return str;
	}

	public String getName()
	{
		return str;
	}

	public abstract StringBuilder sample(StringBuilder stringBuilder);
	
	public static class StringTerminal extends Terminal
	{
		public StringTerminal(String str, long count)
		{
			super(str, count);
		}

		@Override
		public StringBuilder sample(StringBuilder stringBuilder)
		{
			return stringBuilder.append(str);
		}
	}
	public static class NumberTerminal extends Terminal
	{
		public NumberTerminal(String str, long count)
		{
			super(str, count);
		}

		@Override
		public StringBuilder sample(StringBuilder stringBuilder)
		{
			return stringBuilder.append((int) (Math.random() * 1000));
		}
	}
	public static class RandomStringTerminal extends Terminal
	{
		public RandomStringTerminal(String str, long count)
		{
			super(str, count);
		}

		@Override
		public StringBuilder sample(StringBuilder stringBuilder)
		{
			return stringBuilder.append(AnotherUtils.createRandomString(10));
		}
	}
	public static class QuotedRandomStringTerminal extends Terminal
	{
		public QuotedRandomStringTerminal(String str, long count)
		{
			super(str, count);
		}

		@Override
		public StringBuilder sample(StringBuilder stringBuilder)
		{
			return stringBuilder.append('"' + AnotherUtils.createRandomString(10) + '"');
		}
	}
}
