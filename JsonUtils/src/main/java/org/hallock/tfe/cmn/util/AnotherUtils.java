package org.hallock.tfe.cmn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class AnotherUtils
{
	public static Random RANDOM = new Random();
	
	
	public static String readFile(String fileName) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		try (BufferedReader newBufferedReader = Files.newBufferedReader(Paths.get(fileName));)
		{
			String line;
			while ((line = newBufferedReader.readLine()) != null)
			{
				builder.append(line).append('\n');
			}
		}
		return builder.toString();
	}
	
	
	
	
	
	public static PrintStream indent(PrintStream ps, int depth)
	{
		return indent(ps, depth, "\t");
	}
	public static PrintStream indent(PrintStream ps, int depth, String indent)
	{
		while (depth --> 0)
		{
			ps.print(indent);
		}
		return ps;
	}
	public static StringBuilder indent(StringBuilder ps, int depth)
	{
		while (depth --> 0)
		{
			ps.append('\t');
		}
		return ps;
	}
	public static CharSequence lfill(Object value, int numdigits)
	{
		String current = value.toString();
		StringBuilder builder = new StringBuilder(numdigits);
		while (current.length() + builder.length() < numdigits)
			builder.append(' ');
		return builder.append(current).toString();
	}
	

	private static final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // + "`~!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
	public static String createRandomString(int length)
	{
		StringBuilder builder = new StringBuilder(length);
		
		for (int i = 0; i < length; i++)
		{
			builder.append(characters.charAt(
					RANDOM.nextInt(characters.length())));
		}
		
		return builder.toString();
	}
}
