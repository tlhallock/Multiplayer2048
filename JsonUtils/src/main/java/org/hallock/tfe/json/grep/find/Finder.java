package org.hallock.tfe.json.grep.find;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.search.SearchResult;

public interface Finder extends Jsonable
{
	public void register(MultiMatch matcher);
	
	/**
	 * @return true if the searcher should continue searching
	 */
	public SearchResult found(JsonValue value, MultiMatch results);
	
	public static Finder read(JsonObject jsonObject)
	{
		String type = jsonObject.getString("type");
		if (type.equals(CaptureFinder.class.getName()))
		{
			return new CaptureFinder(jsonObject);
		}
		else if (type.equals(ExistsFinder.class.getName()))
		{
			return new ExistsFinder(jsonObject);
		}
		
		throw new RuntimeException("type: " + type);
	}
}
