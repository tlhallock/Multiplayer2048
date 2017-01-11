package org.hallock.tfe.json.grep.search;

public class SearchResult
{
	public boolean stopSearch;
	public boolean matched;
	
	public SearchResult()
	{
		this(false);
	}
	
	public SearchResult(boolean matched)
	{
		this(matched, false);
	}
	
	public SearchResult(boolean matched, boolean stopSearch)
	{
		this.matched = matched;
		this.stopSearch = stopSearch;
	}

	public SearchResult or(SearchResult other)
	{
		stopSearch |= other.stopSearch;
		matched |= other.matched;
		return this;
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof SearchResult))
			return false;
		SearchResult o = (SearchResult) other;
		return stopSearch == o.stopSearch && matched == o.matched;
	}

	@Override
	public String toString()
	{
		return "SearchResult [stopSearch=" + stopSearch + ", matched=" + matched + "]";
	}
}
