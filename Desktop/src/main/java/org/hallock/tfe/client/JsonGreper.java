package org.hallock.tfe.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class JsonGreper
{
	public static void main(String[] args) throws IOException
	{
		try (InputStream input = Files.newInputStream(Paths.get("C:\\cygwin64\\home\\thallock\\Documents\\marriage\\SomeJson\\Hangouts.json"));
			JsonReader parser = javax.json.Json.createReader(input);
//			PrintStream writer = new PrintStream("C:\\cygwin64\\home\\thallock\\Documents\\marriage\\SomeJson\\anotherFile.txt");
			PrintStream writer2 = new PrintStream("C:\\cygwin64\\home\\thallock\\Documents\\marriage\\SomeJson\\results.txt");)
		{
			JsonObject read = (JsonObject) parser.read();
			System.out.println("read");
//			print(new LinkedList<>(), read, writer);
//			System.out.println("wrote");
			traverse(new LinkedList<>(), read, new JsonPattern(new WriteResults(writer2))
				.add(new MatchesPathCriteria()
					.exact("conversation_state")
					.any()
					.exact("conversation_state")
					.exact("event")
					.any())
				.add(new ContainsFieldCriteria("timestamp", null))
				.add(new ContainsChild().cut("chat_message:message_content:segment:$array[0]:text"))
				.add(new FieldExtractor("time", "timestamp"))
				.add(new ChildExtractor(new FieldExtractor("text", "text")).cut("chat_message:message_content:segment:$array[0]"))
				.add(new ChildExtractor(new FieldExtractor("conversation", "id")).cut("conversation_id"))
			);
		}
	}

	private static void print(LinkedList<String> stack, JsonValue object, PrintStream writer)
	{
		switch (object.getValueType())
		{
		case ARRAY:
			for (int i=0;i<((JsonArray)object).size();i++)
			{
				stack.addLast("$array[" + i + "]");
				print(stack, ((JsonArray)object).get(i), writer);
				stack.removeLast();
			}
			break;
		case FALSE:
			printEntry(stack, "false", writer);
			break;
		case NULL:
			printEntry(stack, "null", writer);
			break;
		case NUMBER:
			printEntry(stack, String.valueOf(((JsonNumber)object).doubleValue()), writer);
			break;
		case OBJECT:
			for (Entry<String, JsonValue> value : ((JsonObject) object).entrySet())
			{
				stack.addLast(value.getKey());
				print(stack, value.getValue(), writer);
				stack.removeLast();
			}
			break;
		case STRING:
			printEntry(stack, String.valueOf(((JsonString)object).getString()), writer);
			break;
		case TRUE:
			printEntry(stack, "true", writer);
			break;
		}
	}
	private static void traverse(LinkedList<String> stack, JsonValue object, JsonPattern pattern)
	{
		pattern.visit(stack, object);
		
		switch (object.getValueType())
		{
		case ARRAY:
			for (int i=0;i<((JsonArray)object).size();i++)
			{
				stack.addLast("$array[" + i + "]");
				traverse(stack, ((JsonArray)object).get(i), pattern);
				stack.removeLast();
			}
			break;
		case OBJECT:
			for (Entry<String, JsonValue> value : ((JsonObject) object).entrySet())
			{
				stack.addLast(value.getKey());
				traverse(stack, value.getValue(), pattern);
				stack.removeLast();
			}
			break;
		}
	}
	private static void printEntry(LinkedList<String> stack, String string, PrintStream writer)
	{
		for (String str : stack)
		{
			writer.print(str);
			writer.print(":");
		}
		writer.print(string);
		writer.print('\n');
	}
	
	
	
	public static abstract class SearchResults
	{
		public abstract void found(Match match);
	}
	
	public static final class CollectResults extends SearchResults
	{
		LinkedList<Match> matches = new LinkedList<>();
		
		public void print(PrintStream ps)
		{
			for (Match match : matches)
				match.print(ps);
		}

		@Override
		public void found(Match match)
		{
			matches.add(match);
		}
	}
	
	public static final class WriteResults extends SearchResults
	{
		PrintStream ps;
		
		public WriteResults(PrintStream ps)
		{
			this.ps = ps;
		}
		
		@Override
		public void found(Match match)
		{
			match.print(System.out);
			match.print(ps);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class Match
	{
		LinkedList<String> stack;
		TreeMap<String, String> matches = new TreeMap<>();
		
		public void print(PrintStream ps)
		{
			ps.print("location: ");
			printEntry(stack, "<>", ps);
			ps.print('\n');
			for (Entry<String, String> entry : matches.entrySet())
			{
				ps.print("\t\"" + entry.getKey() + "\"\t:\t\"" + entry.getValue() + "\"\n");
			}
			ps.print('\n');
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static abstract class Criteria
	{
		public abstract boolean matches(LinkedList<String> path, JsonValue value);
	}
	
	public static class ContainsFieldCriteria extends Criteria
	{
		private String field;
		private JsonValue value;
		
		public ContainsFieldCriteria(String field, JsonValue value)
		{
			this.field = field;
			this.value = value;
		}
		@Override
		public boolean matches(LinkedList<String> path, JsonValue value)
		{
			if (!value.getValueType().equals(ValueType.OBJECT))
				return false;
			JsonObject object = (JsonObject) value;
			if (!object.containsKey(field))
				return false;
			if (this.value == null)
				return true;
			JsonValue test = object.get(field);
			if (!test.getValueType().equals(this.value.getValueType()))
				return false;
			
			switch (test.getValueType())
			{
			case TRUE:
			case NULL:
			case FALSE:
				return true;
			case STRING:
				return ((JsonString)this.value).getString().equals(((JsonString)test).getString());
			case ARRAY:
			case NUMBER:
			case OBJECT:
			default:
				throw new RuntimeException("Not implemented");
			}
		}
	}
	public static class ContainsChild extends Criteria
	{
		// should be matchers...
		private ArrayList<String> matchers;
		private Criteria c;
		
//		public ContainsChild(ArrayList<String> matchers, Criteria c)
//		{
//			this.matchers = matchers;
//			this.c = c;
//		}

		public ContainsChild(Criteria c)
		{
			matchers = new ArrayList<>();
			this.c = c;
		}
		public ContainsChild()
		{
			this(null);
		}

		
		ContainsChild cut(String str)
		{
			for (String s : str.split(":"))
			{
				exact(s);
			}
			return this;
		}
		
		ContainsChild exact(String str)
		{
			matchers.add(str);
			return this;
		}
		
		
		@Override
		public boolean matches(LinkedList<String> path, JsonValue value)
		{
			for (int i=0;i<matchers.size();i++)
			{
				if (matchers.get(i).startsWith("$array["))
				{
					if (!value.getValueType().equals(ValueType.ARRAY))
					{
						return false;
					}
					int index = Integer.valueOf(matchers.get(i).substring("$array[".length(), matchers.get(i).length()-1));
					if (((JsonArray) value).size() <= index)
					{
						return false;
					}
					if (index < 0)
					{
						return false;
					}
					value = ((JsonArray) value).get(index);
				}
				else
				{
					if (!value.getValueType().equals(ValueType.OBJECT))
					{
						return false;
					}
					if (!((JsonObject) value).containsKey(matchers.get(i)))
					{
						return false;
					}
					value = ((JsonObject) value).get(matchers.get(i));
				}
			}
			if (c == null)
				return true;
			
			return c.matches(null, value);
		}
	}
	
	
	public static class MatchesPathCriteria extends Criteria
	{
		ArrayList<PathMatcher> path = new ArrayList<>();

		public MatchesPathCriteria()
		{
		}
		
		public MatchesPathCriteria(LinkedList<PathMatcher> pathSoFar, PathMatcher m)
		{
			path.addAll(pathSoFar);
			path.add(m);
		}
		
		public MatchesPathCriteria exact(String str)
		{
			return add(new ExactMatcher(str));
		}

		public MatchesPathCriteria any()
		{
			return add(new AnyMatcher());
		}
		
		public MatchesPathCriteria add(PathMatcher matcher)
		{
			path.add(matcher);
			return this;
		}

		@Override
		public boolean matches(LinkedList<String> path, JsonValue value)
		{
			for (int i=0;i<this.path.size();i++)
			{
				if (i >= path.size())
					return false;
				if (!this.path.get(i).matches(path.get(i)))
					return false;
			}
			return true;
		}
	}
	
	
	
	
	
	
	
	
	
	
	

	static abstract class PathMatcher
	{
		public abstract boolean matches(String string);
	}
	
	static class ExactMatcher extends PathMatcher
	{
		String value;
		
		public ExactMatcher(String value)
		{
			this.value = value;
		}

		@Override
		public boolean matches(String string)
		{
			return value.equals(string);
		}
	}
	static class AnyMatcher extends PathMatcher
	{
		@Override
		public boolean matches(String string)
		{
			return true;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static abstract class Extractor
	{
		public abstract void extract(JsonValue valueSearchResults, Match results);
	}
	public static class FieldExtractor extends Extractor
	{
		String name;
		String field;
		
		public FieldExtractor(String name, String field)
		{
			this.name = name;
			this.field = field;
		}

		@Override
		public void extract(JsonValue value, Match results)
		{
			if (!((JsonObject)value).containsKey(field))
			{
				System.out.println("object " + value + " does not have " + field);
				return;
			}
			if (field.equals("id"))
			{
				JsonString s = (JsonString)     ((JsonObject) value).get(field);
				if (!s.getString().equals("UgxUtAA8CxdkGQJy6RV4AaABAQ"))
				{
					return;
				}
			}
			
			results.matches.put(name, ((JsonObject) value).get(field).toString());
		}
	}
	public static class ChildExtractor extends Extractor
	{
		LinkedList<String> childPath;
		Extractor extractor;
		
		public ChildExtractor(Extractor field)
		{
			childPath = new LinkedList<>();
			this.extractor = field;
		}
		
		ChildExtractor cut(String str)
		{
			for (String s : str.split(":"))
			{
				exact(s);
			}
			return this;
		}
		
		ChildExtractor exact(String str)
		{
			childPath.add(str);
			return this;
		}
		
		ChildExtractor array(int index)
		{
			childPath.add("$array[" + index + "]");
			return this;
		}

		@Override
		public void extract(JsonValue value, Match results)
		{
			for (int i = 0; i < childPath.size(); i++)
			{
				if (childPath.get(i).startsWith("$array["))
				{
					if (!value.getValueType().equals(ValueType.ARRAY))
					{
						return;
					}
					int index = Integer.valueOf(childPath.get(i).substring("$array[".length(), childPath.get(i).length()-1));
					if (((JsonArray) value).size() <= index)
					{
						return;
					}
					if (index < 0)
					{
						return;
					}
					value = ((JsonArray) value).get(index);
				}
				else
				{
					if (!value.getValueType().equals(ValueType.OBJECT))
					{
						return;
					}
					if (!((JsonObject) value).containsKey(childPath.get(i)))
					{
						return;
					}
					value = ((JsonObject) value).get(childPath.get(i));
				}
			}
			
			extractor.extract(value, results);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class JsonPattern
	{
		ArrayList<Criteria> criteria = new ArrayList<>();
		ArrayList<Extractor> extractor = new ArrayList<>();
		SearchResults results;
		
		JsonPattern(SearchResults results)
		{
			this.results = results;
		}
		
		public JsonPattern add(Criteria c)
		{
			criteria.add(c);
			return this;
		}
		
		public JsonPattern add(Extractor e)
		{
			extractor.add(e);
			return this;
		}
		
		public void visit(LinkedList<String> path, JsonValue value)
		{
			for (Criteria criteria : criteria)
				if (!criteria.matches(path, value))
					return;
			Match match = new Match();
			match.stack = (LinkedList<String>) path.clone();
			for (Extractor e : extractor)
				e.extract(value, match);
			results.found(match);
		}
	}
}
