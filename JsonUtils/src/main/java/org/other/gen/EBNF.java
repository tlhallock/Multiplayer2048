package org.other.gen;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;

import org.hallock.tfe.cmn.util.AnotherUtils;


public class EBNF
{
	public static final int NUMDIGITS = 20;
	
	ArrayList<Terminal> terminals = new ArrayList<>();
	ArrayList<Nonterminal> nonterminals = new ArrayList<>();
	
	DiscreteDistribution[] nondists;
	
	Nonterminal root;

	public EBNF()
	{

	}
	
	public void setRoot(Nonterminal root)
	{
		this.root = root;
	}

	Terminal addTerminal(Terminal terminal)
	{
		terminals.add(terminal);
		return terminal;
	}

	Nonterminal addNonterminal(Nonterminal terminal)
	{
		nonterminals.add(terminal);
		return terminal;
	}

	public void compile(int max)
	{
		for (Nonterminal nt : nonterminals)
		{
			nt.allocate(max);
		}
		
		nondists = new DiscreteDistribution[max];
		for (int i = 0; i < max; i++)
		{
			System.out.println("Compiling " + i);
			BigInteger[] counts = new BigInteger[nonterminals.size()];
			for (int j = 0; j < nonterminals.size(); j++)
			{
				counts[j] = nonterminals.get(j).count(i);
			}
			nondists[i] = new DiscreteDistribution(counts);
		}
	}

	public PrintStream append(PrintStream builder)
	{
		builder.append("EBNF generator:\n");
		append(builder, 1);
		return builder;
	}
	public PrintStream append(PrintStream builder, int cdepth)
	{
		AnotherUtils.indent(builder, cdepth);
		builder.append("EBNF");
		for (int i = nondists.length - 1; i >= 0; i--)
		{
			AnotherUtils.indent(builder, cdepth).append("num terms=")
				.append(String.valueOf(i))
				.append(" #=")
				.append(String.valueOf(nondists[i].sum))
				.append('\n');
			for (Nonterminal nt : nonterminals)
			{
				nt.append(builder, cdepth+1, i);
			}
		}
		return builder;
	}

	public StringBuilder sample(StringBuilder stringBuilder, int num)
	{
		Nonterminal startFrom;
		if (root == null)
		{
			startFrom = nonterminals.get(nondists[num].sample());
		}
		else
		{
			startFrom = root;
		}
		startFrom.sample(stringBuilder, num);
		return stringBuilder;
	}
}
