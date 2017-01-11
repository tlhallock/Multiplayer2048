package org.hallock.tfe.json.grep.search;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.path.JsonPath;

public abstract class Searcher implements Jsonable
{
	public abstract void register(MultiMatch match);

	/**
	 * 
	 * @return true if the search should continue the search
	 */
	public abstract SearchResult visit(JsonPath currentPath, JsonValue valueSearchResults, MultiMatch results);
	
	@Override
	public abstract boolean equals(Object other);

	
	public static Searcher read(JsonObject jsonObject)
	{
		String string = jsonObject.getString("type");
		if (string.equals(AndSearcher.class.getName()))
		{
			return new AndSearcher(jsonObject);
		}
		else if (string.equals(ChildSearcher.class.getName()))
		{
			return new ChildSearcher(jsonObject);
		}
		else if (string.equals(FieldSearcher.class.getName()))
		{
			return new FieldSearcher(jsonObject);
		}
		else if (string.equals(OrSearcher.class.getName()))
		{
			return new OrSearcher(jsonObject);
		}
		else if (string.equals(PathSearcher.class.getName()))
		{
			return new PathSearcher(jsonObject);
		}
		else if (string.equals(FinderSearcher.class.getName()))
		{
			return new FinderSearcher(jsonObject);
		}
		throw new RuntimeException("Unexpected type: " + jsonObject);
	}
}