package org.other.gen;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;

import org.hallock.tfe.cmn.util.AnotherUtils;

public class Nonterminal implements erminal
{
	String name;
	ArrayList<Rule> rules = new ArrayList<>();
	DiscreteDistribution[] ruleExpansions;

	Nonterminal(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Rule addRule(Rule rule)
	{
		rules.add(rule);
		return rule;
	}

	public BigInteger count(int num)
	{
		if (ruleExpansions[num] == null)
		{
//			System.out.println("Counting nonterminal " + name + "[" + num + "]");
			BigInteger[] dist = new BigInteger[rules.size()];
			for (int i = 0; i < dist.length; i++)
				dist[i] = rules.get(i).count(num);
			ruleExpansions[num] = new DiscreteDistribution(dist);
		}
		return ruleExpansions[num].sum;
	}

	public void allocate(int max)
	{
		ruleExpansions = new DiscreteDistribution[max];
		for (Rule rule : rules)
			rule.allocate(max);
	}

	public void sample(StringBuilder stringBuilder, int num)
	{
		rules.get(ruleExpansions[num].sample()).sample(stringBuilder, num);
	}

	public PrintStream append(PrintStream builder, int cdepth, int numTerms)
	{
		AnotherUtils.indent(builder, cdepth).append("Nonterminal ").append(name).append('\n');
		AnotherUtils.indent(builder, cdepth).append("num terms=").append(String.valueOf(numTerms));
		builder.append(" #=").append(String.valueOf(ruleExpansions[numTerms].sum)).append('\n');
		
		for (Rule rule : rules)
		{
			rule.append(builder, cdepth + 1, numTerms);
		}
		return builder;
	}
}
