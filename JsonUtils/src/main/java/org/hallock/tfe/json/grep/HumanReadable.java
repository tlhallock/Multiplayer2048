package org.hallock.tfe.json.grep;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;


public class HumanReadable
{
	static void print(LinkedList<String> stack, JsonValue object, PrintStream writer)
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

	static void printEntry(LinkedList<String> stack, String string, PrintStream writer)
	{
		for (String str : stack)
		{
			writer.print(str);
			writer.print(":");
		}
		writer.print(string);
		writer.print('\n');
	}

//	static String printEntry(LinkedList<String> stack)
//	{
//		StringBuilder builder = new StringBuilder();
//		for (String str : stack)
//		{
//			builder.append(str);
//			builder.append(":");
//		}
//		builder.append(string);
//		builder.append('\n');
//	}
}
