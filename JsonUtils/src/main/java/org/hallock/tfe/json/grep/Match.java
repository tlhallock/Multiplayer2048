package org.hallock.tfe.json.grep;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.json.grep.path.JsonPath;

import com.fasterxml.jackson.core.JsonGenerator;

public class Match implements Jsonable
{
	private JsonPath stack;
	private TreeMap<String, String> matches = new TreeMap<>();
	
	public Match() {}
	
	public Match(JsonObject jsonObject)
	{
		stack = new JsonPath(jsonObject.getJsonObject("path"));
		
		JsonObject jsonObject2 = jsonObject.getJsonObject("captured values");
		for (Entry<String, JsonValue> e : jsonObject2.entrySet())
		{
			matches.put(e.getKey(), ((JsonString) e.getValue()).getString());
		}
	}
	
	@Override
	public String toString()
	{
		return Jsonable.toString(this);
	}
	
	public JsonPath getPath()
	{
		return stack;
	}
	
	public int getNumberOfCaptures()
	{
		return matches.size();
	}

	public void printHumanReadable(PrintStream ps)
	{
		ps.print("location: " + stack.toString());
		
		ps.print('\n');
		for (Entry<String, String> entry : matches.entrySet())
		{
			ps.print("\t\"" + entry.getKey() + "\"\t:\t\"" + entry.getValue() + "\"\n");
		}
		ps.print('\n');
	}
	
	public String get(String string)
	{
		return matches.get(string);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("type", getClass().getName());

		generator.writeFieldName("path");
		stack.write(generator);
		
		generator.writeFieldName("captured values");
		generator.writeStartObject();
		for (Entry<String, String> e : matches.entrySet())
			generator.writeStringField(e.getKey(), e.getValue());
		generator.writeEndObject();
		
		generator.writeEndObject();
	}

	public void setStack(JsonPath dup)
	{
		this.stack = dup;
	}

	public void putAll(TreeMap<ResultName, String> current)
	{
		for (Entry<ResultName, String> e : current.entrySet())
		{
			matches.put(e.getKey().name, e.getValue());
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Match))
		{
			return false;
		}
		Match o = (Match) other;
		if (!stack.equals(o.stack))
			return false;
		if (matches.size() != o.matches.size())
			return false;

		for (Entry<String, String> e : matches.entrySet())
		{
			String string = o.matches.get(e.getKey());
			if (string == null)
				return false;
			if (!e.getValue().equals(string))
				return false;
		}
		
		return true;
	}
}