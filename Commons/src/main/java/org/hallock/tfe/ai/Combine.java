//package org.hallock.tfe.ai;
//
//import java.io.IOException;
//import java.nio.file.DirectoryStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.hallock.tfe.ai.GameWriterIf.Turn;
//
//public class Combine
//{
//	static Pattern pattern = Pattern.compile(".*[0-9]x[0-9]_(.*)_([0-9]*)_play_([0-9]*).txt");
//	public static void main(String[] args) throws IOException
//	{
//		String start = "C:\\cygwin64\\home\\trever\\Documents\\Source\\Multiplayer2048\\Desktop\\all\\";
//		try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(Paths.get(start));)
//		{
//			for (Path p : newDirectoryStream)
//			{
//				String string = p.toString();
//				if (!string.startsWith(start))
//				{
//					continue;
//				}
//				string = string.substring(start.length());
//				if (string.contains("alldone"))
//				{
//					continue;
//				}
//				
//				Matcher matcher = pattern.matcher(string);
//				if (!matcher.find())
//				{
//					System.out.println("Error: " + string);
//					continue;
//				}
//				String name = matcher.group(1);
//				long time = Long.valueOf(matcher.group(2));
//				int turnNumber = Integer.valueOf(matcher.group(3));
//				
//				
//				System.out.println("Handling " + p);
//				GameWriterIf writer = GameWriterIf.getWriter(time, name);
//				
//				Turn turn = new Turn(p, turnNumber);
//				writer.addTurn(turn);
//				writer.save(false);
//				
////				
////				
////				int indexOf = string.indexOf("_play_");
////				if (indexOf < 0)
////					continue;
////				String game_name = string.substring(0, indexOf);
////				String game_info = string.substring(indexOf);
////				String rest = string.substring(indexOf + "_play_".length(), string.length());
////				
////				int turnNumber = Integer.parseInt(rest);
////				
//				
//			}
//		}
//	}
//}
