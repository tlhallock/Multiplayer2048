package org.other.gen;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;

import org.hallock.tfe.cmn.util.AnotherUtils;


public class Rule
{
	ArrayList<erminal> expansion = new ArrayList<>();
	
	DiscreteDistribution[] counts;
	CompiledRuleNode[][] countInterps;

	ArrayList<Integer> nonterminalMapping;
	int countNonTerminals;
	int countTerminals;

	String name;
	
	public Rule(String name)
	{
		this.name = name;
	}

	public Rule add(erminal expr)
	{
		expansion.add(expr);
		return this;
	}

	public BigInteger count(int num)
	{
		if (num < 0)
		{
			return BigInteger.ZERO;
		}
		if (this.counts[num] != null)
		{
			return this.counts[num].sum;
		}

//		System.out.println("Counting rule " + name + " [" + num + "]");

		int[] assignment = new int[this.countNonTerminals];
		int max = num - countTerminals;
		
		LinkedList<CompiledRuleNode> nodes = new LinkedList<>();
		if (num < expansion.size())
		{
			// do nothing in this case...
		}
		if (countNonTerminals == 0 && num == countTerminals)
		{
			nodes.add(new CompiledRuleNode(new BigInteger[0]));
		}
		if (countNonTerminals > 0 && max >= 0)
		{
			assignment[assignment.length - 1] = max;
			do
			{
				BigInteger[] assignmentCopy = new BigInteger[assignment.length];
				for (int i = 0; i < assignment.length; i++)
					assignmentCopy[i] = new BigInteger(String.valueOf(assignment[i]));
				nodes.add(new CompiledRuleNode(assignmentCopy));
			}
			while (inc(assignment, max, assignment.length - 1));
		}
		
		
		countInterps[num] = new CompiledRuleNode[nodes.size()];
		BigInteger[] counts = new BigInteger[nodes.size()];
		int countsIndex = 0;
		for (CompiledRuleNode c : nodes)
		{
			counts[countsIndex] = c.count;
			countInterps[num][countsIndex] = c;
			countsIndex++;
		}
		this.counts[num] = new DiscreteDistribution(counts);
		

		return this.counts[num].sum;
	}
	
	public void allocate(int max)
	{
		nonterminalMapping = new ArrayList<>();
		countNonTerminals = 0;
		int nontermIndex=0;
		for (erminal e : expansion)
		{
			if (e instanceof Nonterminal)
			{
				countNonTerminals++;
				nonterminalMapping.add(nontermIndex);
			}
			nontermIndex++;
		}
		countTerminals = expansion.size() - countNonTerminals;
		
		counts = new DiscreteDistribution[max];
		countInterps = new CompiledRuleNode[max][];
	}
	
	private final class CompiledRuleNode
	{
		BigInteger count;
		BigInteger[] counts;

		public CompiledRuleNode(BigInteger[] counts2)
		{
			count = BigInteger.ONE;
			this.counts = counts2;
			for (int i = 0; i < counts.length; i++)
			{
				count = count.multiply(((Nonterminal) expansion.get(nonterminalMapping.get(i))).count(counts[i].intValue()));
			}
			for (erminal t : expansion)
			{
				if (t instanceof Terminal)
				{
					count = count.multiply(((Terminal) t).num);
				}
			}
		}

		public void sample(StringBuilder stringBuilder)
		{
			int idx = 0;
			for (erminal e : expansion)
			{
				if (e instanceof Terminal)
				{
					((Terminal)e).sample(stringBuilder);
				}
				else
				{
					((Nonterminal)e).sample(stringBuilder, counts[idx++].intValue());
				}
			}
		}

		public void append(PrintStream builder, int cdepth, int numTerms)
		{
			AnotherUtils.indent(builder, cdepth).append("expansion: ");
			builder.append("#=").append(AnotherUtils.lfill(count, EBNF.NUMDIGITS)).append(' ');

			int idx = 0;
			for (erminal e : expansion)
			{
				if (e instanceof Terminal)
				{
					builder.append('\'').append(e.toString()).append("' ");
				}
				else
				{
					builder.append(((Nonterminal)e).getName()).append('{').append(AnotherUtils.lfill(counts[idx], 5)).append("}:");
					builder.append(AnotherUtils.lfill(((Nonterminal)e).ruleExpansions[counts[idx].intValue()].sum, EBNF.NUMDIGITS)).append(' ');
					idx++;
				}
			}
			builder.append('\n');
		}
	}
	
	private static boolean inc(int[] counts, int max, int index)
	{
		if (max == 0 || counts.length == 0)
		{
			return false;
		}
		int search = 0;
		for (int i = counts.length - 1; i >= 0; i--)
		{
			if (counts[i] <= 0)
			{
				continue;
			}
			if (i == 0)
			{
				return false;
			}
			else
			{
				search = i;
				break;
			}
		}
		
		counts[search - 1]++;

		int countSoFar = 0;
		for (int j = 0; j < search; j++)
			countSoFar += counts[j];

		for (int i = search; i < counts.length - 1; i++)
			counts[i] = 0;
		counts[counts.length - 1] = max - countSoFar;
		return true;
	}

	public void sample(StringBuilder stringBuilder, int num)
	{
		int sample = counts[num].sample();
		countInterps[num][sample].sample(stringBuilder);
	}

	public PrintStream append(PrintStream builder, int cdepth, int numTerms)
	{
		AnotherUtils.indent(builder, cdepth).append("Rule ").append(name).append('\n');
		AnotherUtils.indent(builder, cdepth).append("num terms=").append(String.valueOf(numTerms)).append(" #=")
			.append(String.valueOf(counts[numTerms].sum)).append('\n');

		for (int i = 0; i < countInterps[numTerms].length; i++)
		{
			countInterps[numTerms][i].append(builder, cdepth + 1, numTerms);
		}
		return builder;
	}
}
