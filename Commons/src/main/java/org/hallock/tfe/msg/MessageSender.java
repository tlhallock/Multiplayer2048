package org.hallock.tfe.msg;

import java.io.IOException;
import java.util.Collection;

import org.hallock.tfe.cmn.util.Jsonable;

import com.fasterxml.jackson.core.JsonGenerator;

public abstract class MessageSender
{
	public abstract void sendMessage(JsonGenerator generator) throws IOException;
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void send(Entry entry, JsonGenerator generator) throws IOException
	{
		generator.writeFieldName(entry.fieldName);
		if (entry.value == null)
			generator.writeNull();
		else
			entry.value.write(generator);
	}
	public static void send(Collection<Entry> fields, JsonGenerator generator) throws IOException
	{
		for (Entry e : fields)
		{
			send(e, generator);
		}
	}
	public static <T extends Jsonable> void sendArray(Collection<T> fields, JsonGenerator generator) throws IOException
	{
		generator.writeStartArray();
		for (T t : fields)
			t.write(generator);
		generator.writeEndArray();
	}
	public static <T extends Jsonable> void sendArray(T[] fields, JsonGenerator generator) throws IOException
	{
		generator.writeStartArray();
		for (T t : fields)
			t.write(generator);
		generator.writeEndArray();
	}
	
	
	
	public static class Entry
	{
		String fieldName;
		Jsonable value;

		public Entry(String fieldName, Jsonable value)
		{
			this.fieldName = fieldName;
			this.value = value;
		}
	}
}
