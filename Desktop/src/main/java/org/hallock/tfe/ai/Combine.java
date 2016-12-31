package org.hallock.tfe.ai;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class Combine
{
	public static void main(String[] args) throws IOException
	{
		String start = "C:\\cygwin64\\home\\trever\\Documents\\Source\\Multiplayer2048\\Desktop\\statistics.2\\";
		try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(Paths.get(
				"C:\\cygwin64\\home\\trever\\Documents\\Source\\Multiplayer2048\\Desktop\\statistics.2\\"));)
		{
			HashMap<String, Integer> games = new HashMap<>();
			
			for (Path p : newDirectoryStream)
			{
				String string = p.toString();
				if (!string.startsWith(start))
				{
					continue;
				}
				string = string.substring(start.length());
				
				int indexOf = string.indexOf("_play_");
				if (indexOf < 0)
					continue;
				String game_name = string.substring(0, indexOf);
				String game_info = string.substring(indexOf);
				String rest = string.substring(indexOf + "_play_".length(), string.length() - ".txt".length());
				
				int newLength = Integer.parseInt(rest);
				Integer old = games.get(game_name);
				if (old == null || old < newLength)
					games.put(game_name, newLength);
			}

			LinkedList<String> results = new LinkedList<>();
			for (Entry<String, Integer> entry : games.entrySet())
			{
				results.add(entry.getKey() + "=" + entry.getValue());
			}

			Collections.sort(results);
			for (String result : results)
				System.out.println(result);
			
			System.out.println("number of games: " + games.size());
		}
	}
}
