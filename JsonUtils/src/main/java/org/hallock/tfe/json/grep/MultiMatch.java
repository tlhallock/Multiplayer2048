package org.hallock.tfe.json.grep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.json.grep.path.JsonPath;
import org.hallock.tfe.json.grep.results.MatchListener;

import com.fasterxml.jackson.core.JsonGenerator;

public class MultiMatch implements Jsonable
{
	TreeMap<ResultName, LinkedList<String>> matches = new TreeMap<>();
	
	public MultiMatch() {}
	
	public MultiMatch(JsonObject object)
	{
		JsonObject matchObject = object.getJsonObject("matches");
		for (Entry<String, JsonValue> e : matchObject.entrySet())
		{
			LinkedList<String> list = new LinkedList<>();
			JsonArray jsonArray = (JsonArray) e.getValue();
			for (int i = 0; i < jsonArray.size(); i++)
				list.add(jsonArray.getString(i));
			matches.put(new ResultName(e.getKey()), list);
		}
	}

	public LinkedList<String> getValues(String string)
	{
		return matches.get(new ResultName(string));
	}

	public void seeks(ResultName key)
	{
		matches.put(key, new LinkedList<>());
	}
	
	public void clear()
	{
		for (LinkedList<String> list : matches.values())
		{
			list.clear();
		}
	}
	
	public void foundMatch(ResultName name, String value)
	{
		LinkedList<String> linkedList = matches.get(name);
		if (linkedList == null)
		{
			throw new RuntimeException("Found extra field: " + name);
		}
		linkedList.add(value);
	}
	
	public void enumerate(JsonPath stack, MatchListener output)
	{
		ArrayList<Entry<ResultName, LinkedList<String>>> rest = new ArrayList<>(matches.size());
		rest.addAll(matches.entrySet());
		Collections.sort(rest, COMPARATOR);
		enumerate(stack, output, new TreeMap<ResultName, String>(), rest);
	}
	

	private void enumerate(
			JsonPath stack,
			MatchListener results, 
			TreeMap<ResultName, String> current,
			ArrayList<Entry<ResultName, LinkedList<String>>> rest)
	{
		if (rest.isEmpty())
		{
			Match match = new Match();
			match.setStack(stack.dup());
			match.putAll(current);
			
			try
			{
				results.found(match);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return;
		}
		
		Entry<ResultName, LinkedList<String>> remove = rest.remove(rest.size()-1);
		
		Collections.sort(remove.getValue());
		
//		if (!remove.getKey().required && remove.getValue().isEmpty())
//		{
//			enumerate(stack, results, current, rest);
//		}
		
		for (String value : remove.getValue())
		{
			current.put(remove.getKey(), value);
			enumerate(stack, results, current, rest);
			current.remove(remove.getKey());
		}
		
		rest.add(rest.size(), remove);
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("Matcher:\n");
		for (Entry<ResultName, LinkedList<String>> e : matches.entrySet())
		{
			builder.append("\t");
			builder.append(e.getKey());
			builder.append(" : ");
			
			for (String str : e.getValue())
			{
				builder.append(str);
				builder.append(" ");
			}
			
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	
	private static Comparator<Entry<ResultName, LinkedList<String>>> COMPARATOR = new Comparator<Entry<ResultName, LinkedList<String>>>() {
		@Override
		public int compare(Entry<ResultName, LinkedList<String>> arg0, Entry<ResultName, LinkedList<String>> arg1)
		{
			return -Integer.compare(arg0.getValue().size(), arg1.getValue().size());
		}};

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		
		generator.writeStringField("type", getClass().getName());
		
		generator.writeFieldName("matches");
		generator.writeStartObject();
		
		for (Entry<ResultName, LinkedList<String>> e : matches.entrySet())
		{
			generator.writeFieldName(e.getKey().name);
			generator.writeStartArray();
			for (String str : e.getValue())
			{
				generator.writeString(str);
			}
			generator.writeEndArray();
		}

		generator.writeEndObject();
		generator.writeEndObject();
	}
}
