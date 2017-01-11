package org.hallock.tfe.json.grep;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Combine
{
	static PrintStream errors;
	static PrintStream output;
	
	static final SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy 'at' hh:mm:ss a zzz");
	
	static final Comparator<ConversationEvent> TIME_COMPARATOR = new Comparator<ConversationEvent>(){
		@Override
		public int compare(ConversationEvent arg0, ConversationEvent arg1)
		{
			return Long.compare(arg0.time, arg1.time);
		}};

	public static void main(String[] args) throws IOException
	{
		errors = new PrintStream("C:\\cygwin64\\home\\trever\\Documents\\output\\combine_errors.txt");
		output = new PrintStream("C:\\cygwin64\\home\\trever\\Documents\\output\\sorted.txt");
		
		
		HashMap<String, HashSet<String>> conversationPeople = getConversationPeople();
		System.out.println("Done reading people");
		LinkedList<ConversationEvent> conversationEvents = getConversationEvents();
		System.out.println("Done reading events");
		
		conversationEvents.sort(TIME_COMPARATOR);
		
		for (ConversationEvent event : conversationEvents)
		{
			HashSet<String> hashSet = conversationPeople.get(event.conversationId);
			if (hashSet == null)
			{
				errors.println("uh oh, event not found: " + event);
				continue;
			}
			
			output.println("Time: " + event.time + " (" + format.format(new Date(event.time / 100)) + ")");
			output.print("People: ");
			for (String s : hashSet)
				output.print(s + " ");
			output.println();
			output.println("Text: \"" + event.text + '"');
			output.println("=================================================");
		}
		System.out.println("Done writing people");
		
		errors.close();
		output.close();
	}
	
	
	public static HashMap<String, HashSet<String>> getConversationPeople() throws IOException
	{
		HashMap<String, HashSet<String>> conversationPeople = new HashMap<>();

		String inputFile = "C:\\cygwin64\\home\\trever\\Documents\\output\\conversation_people.json";
		
		JsonObject object;
		
		try (JsonReader parser = javax.json.Json.createReader(Files.newInputStream(Paths.get(inputFile)));)
		{
			object = (JsonObject) parser.read();
		}
		
		JsonArray array = object.getJsonArray("output");
		for (int i = 0; i < array.size(); i++)
		{
			MultiMatch match = new MultiMatch(array.getJsonObject(i));
			LinkedList<String> conversation_ids = match.getValues("conversation id");
			if (conversation_ids.size() != 1)
				throw new RuntimeException();
			String conversation_id = clip(conversation_ids.get(0));
			LinkedList<String> names = match.getValues("name");
			if (names.size() < 1)
			{
				throw new RuntimeException();
			}
			HashSet<String> allPeople = new HashSet<>();
			allPeople.addAll(names);
			conversationPeople.put(conversation_id, allPeople);
		}
		
		return conversationPeople;
	}
	public static LinkedList<ConversationEvent> getConversationEvents() throws IOException
	{
		LinkedList<ConversationEvent> events = new LinkedList<>();

		String inputFile = "C:\\cygwin64\\home\\trever\\Documents\\output\\conversation_text.json";
		
		JsonObject object;
		
		try (JsonReader parser = javax.json.Json.createReader(Files.newInputStream(Paths.get(inputFile)));)
		{
			object = (JsonObject) parser.read();
		}
		
		JsonArray array = object.getJsonArray("output");
		for (int i = 0; i < array.size(); i++)
		{
			Match match = new Match(array.getJsonObject(i));
			try
			{
				events.add(new ConversationEvent(match));
			}
			catch (Exception e)
			{
				errors.println("Error combining on " + match);
				System.out.println(match);
				e.printStackTrace(errors);
			}
		}

		return events;
	}

	private static class ConversationEvent
	{
		String conversationId;
		String text;
		long time;

		public ConversationEvent(Match match)
		{
			conversationId = clip(match.get("conversation"));
			text = clip(match.get("text"));
			time = Long.parseLong(clip(match.get("time")));
		}

		@Override
		public String toString()
		{
			return "ConversationEvent [conversationId=" + conversationId + ", text=" + text + ", time=" + time + "]";
		}
	}
	
	public static String clip(String other)
	{
		try
		{
			int startIndex = 0;
			while (startIndex < other.length() && other.charAt(startIndex) == '"')
				startIndex++;
			if (startIndex == other.length())
				return "";

			int endIndex = other.length();
			while (other.charAt(endIndex - 1) == '"')
				endIndex--;
			return other.substring(startIndex, endIndex);
		}
		catch (Exception e)
		{
			System.out.println("Error on " + other);
			System.exit(0);
			throw new RuntimeException();
		}
	}
}
