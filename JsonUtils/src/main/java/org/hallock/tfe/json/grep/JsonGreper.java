package org.hallock.tfe.json.grep;

import java.util.Map.Entry;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.json.grep.path.JsonPath;
import org.hallock.tfe.json.grep.path.JsonPathElement.JsonArrayElement;
import org.hallock.tfe.json.grep.path.JsonPathElement.JsonFieldElement;

public class JsonGreper
{
	public static void traverse(
			JsonPath stack, 
			JsonValue object, 
			JsonPattern pattern)
	{
		pattern.visit(stack, object);
		
		switch (object.getValueType())
		{
		case ARRAY:
			for (int i = 0; i < ((JsonArray) object).size(); i++)
			{
				stack.push(new JsonArrayElement(i));
				traverse(stack, ((JsonArray)object).get(i), pattern);
				stack.pop();
			}
			break;
		case OBJECT:
			for (Entry<String, JsonValue> value : ((JsonObject) object).entrySet())
			{
				stack.push(new JsonFieldElement(value.getKey()));
				traverse(stack, value.getValue(), pattern);
				stack.pop();
			}
			break;
		default:
			break;
		}
	}
	
	public static void traverse(JsonValue object, JsonPattern pattern)
	{
		traverse(new JsonPath(), object, pattern);
	}
}
