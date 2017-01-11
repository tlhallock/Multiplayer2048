package org.hallock.tfe.json.grep.search;

import org.hallock.tfe.json.grep.ResultName;
import org.hallock.tfe.json.grep.find.CaptureFinder;
import org.hallock.tfe.json.grep.find.ExistsFinder;
import org.hallock.tfe.json.grep.find.ValueFinder;

public class SearcherUtils
{
	public static FieldSearcher isOneOf(String key, String... possibleValues)
	{
		
		return new FieldSearcher(new FinderSearcher(new ValueFinder(possibleValues)), key);
	}
	public static FieldSearcher captureField(String name, String field)
	{
		return new FieldSearcher(new FinderSearcher(new CaptureFinder(new ResultName(name))), field);
	}
	public static FieldSearcher ensureField(String field)
	{
		return new FieldSearcher(new FinderSearcher(new ExistsFinder()), field);
	}
	public static FinderSearcher ensure()
	{
		return new FinderSearcher(new ExistsFinder());
	}
	
	
	
	
//	public static FieldSearcher ensureField(String field)
//	{
//		return new FieldSearcher(new FinderSearcher(new ExistsFinder()), field);
//	}
	
	
	
//
//	public WholeFieldExtractor(ResultName field, String key)
//	{
//		super(field, new FinderSearcher<CaptureFinder>(new CaptureFinder(field)), key);
//	}
//	
//	public WholeFieldExtractor(String fieldName, String key)
//	{
//		this(new ResultName(fieldName), key);
//	}
//
//	public WholeChildExtractor(ResultName name)
//	{
//		super(new FinderSearcher<CaptureFinder>(new CaptureFinder(name)));
//	}
//
//	public MatchesPathCriteria()
//	{
//		super(new FinderSearcher<ExistsFinder>(new ExistsFinder()));
//	}
//
//
//	public ContainsFieldCriteria(ResultName field, String key)
//	{
//		super(field, new FinderSearcher<ExistsFinder>(new ExistsFinder()), key);
//	}
//
//	public ContainsFieldCriteria(String name, String field)
//	{
//		this(new ResultName(name), field);
//	}
//
//	public ContainsChildCriteria(Searcher<ExistsFinder> c)
//	{
//		super(c);
//	}


}
