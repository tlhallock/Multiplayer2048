package org.hallock.tfe.cmn.util;

import java.awt.Color;

import org.hallock.tfe.cmn.sys.Constants;

public class Utils {

	public static final String ensureLength(String foo, char c, int depth)
	{
		StringBuilder builder = new StringBuilder();
		for (int i=foo.length(); i < depth; i++)
			builder.append(c);
		builder.append(foo);
		return builder.toString();
	}
	
	
	
	
	
	
	
	public static final String display(int number)
	{
		if (number <= 0 && !Constants.DISPLAY_ZEROS)
			return " ";
		return String.valueOf(number);
	}
	
	
	
	
	
	public static Color invertColor(Color c)
	{
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}
}
