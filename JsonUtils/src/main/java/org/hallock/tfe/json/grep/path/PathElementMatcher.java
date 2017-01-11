package org.hallock.tfe.json.grep.path;

import javax.json.JsonObject;

import org.hallock.tfe.cmn.util.Jsonable;

public abstract class PathElementMatcher implements Jsonable
{
	public abstract boolean matches(JsonPathElement jsonPathElement);

	@Override
	public abstract String toString();
	
	@Override
	public abstract boolean equals(Object other);

	public static PathElementMatcher read(JsonObject jsonObject)
	{
		String type = jsonObject.getString("type");
		if (type.equals(AnyElementMatcher.class.getName()))
		{
			return new AnyElementMatcher(jsonObject);
		}
		else if (type.equals(ExactArrayElementMatcher.class.getName()))
		{
			return new ExactArrayElementMatcher(jsonObject);
		}
		else if (type.equals(ExactFieldElementMatcher.class.getName()))
		{
			return new ExactFieldElementMatcher(jsonObject);
		}
		
		throw new RuntimeException("Unknown type: " + type);
	}
	
}