package tfe.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.hallock.tfe.cmn.util.AnotherUtils;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.cmn.util.JsonCopier;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.json.grep.JsonGreper;
import org.hallock.tfe.json.grep.JsonPattern;
import org.hallock.tfe.json.grep.Match;
import org.hallock.tfe.json.grep.ResultName;
import org.hallock.tfe.json.grep.find.CaptureFinder;
import org.hallock.tfe.json.grep.find.ExistsFinder;
import org.hallock.tfe.json.grep.path.JsonPath;
import org.hallock.tfe.json.grep.path.JsonPathElement.JsonFieldElement;
import org.hallock.tfe.json.grep.results.CollectResults;
import org.hallock.tfe.json.grep.search.AndSearcher;
import org.hallock.tfe.json.grep.search.FieldSearcher;
import org.hallock.tfe.json.grep.search.FinderSearcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

public class TestWholeThing
{
	private static String validateJson(String json) throws IOException, JsonParseException
	{
		ByteArrayOutputStream stringOutput = new ByteArrayOutputStream();
		try (
			JsonParser parser = Json.createParser(json);
			JsonGenerator generator = Json.createUnopenedGenerator(stringOutput);)
		{
			new JsonCopier(parser, generator).copy();
		}
		String jsonOutput = new String(stringOutput.toByteArray());
//		System.out.println("Validated:\n" + jsonOutput);
		return jsonOutput;
	}
	private static String getJson(String filename) throws IOException
	{
		return validateJson(AnotherUtils.readFile(filename));
	}
	
	JsonObject value;
	
	@Before
	public void init() throws IOException
	{
		String json = getJson("first.json");

		try (JsonReader parser = javax.json.Json.createReader(new StringReader(json));)
		{
			value = (JsonObject) parser.read();
		}
	}

	@Test
	public void test1() throws IOException
	{
		JsonPattern jsonPattern = new JsonPattern();
		
		AndSearcher and = new AndSearcher();
		
		and.add(new FieldSearcher(
				new FinderSearcher(new ExistsFinder()),
				"inner array"
				));

		and.add(new FieldSearcher(
				new FinderSearcher(new ExistsFinder()),
				"has boolean"
				));
		
		
		jsonPattern.add(and);
		

		CollectResults results = new CollectResults();
		jsonPattern.addListener(results);
		JsonGreper.traverse(value, jsonPattern);
		
		Assert.assertEquals("Wrong number of results", 1, results.getNumberOfResults());
		Match match = results.get(0);
		Assert.assertEquals("Wrong number of captures", 0, match.getNumberOfCaptures());
		Assert.assertEquals("Wrong path",
				new JsonPath().add(new JsonFieldElement("object child")),
				match.getPath());
	}
	


	@Test
	public void test2() throws IOException
	{
		JsonPattern jsonPattern = new JsonPattern();
		
		AndSearcher and = new AndSearcher();
		
		and.add(new FieldSearcher(
				new FinderSearcher(new ExistsFinder()),
				"inner array"
				));

		and.add(new FieldSearcher(
				new FinderSearcher(new ExistsFinder()),
				"has boolean"
				));

		and.add(new FieldSearcher(
				new FinderSearcher(new CaptureFinder(new ResultName("the result"))),
				"null child"
				));
		
		
		jsonPattern.add(and);

		
		System.out.println(Jsonable.toString(jsonPattern));
		

		CollectResults results = new CollectResults();
		jsonPattern.addListener(results);
		JsonGreper.traverse(value, jsonPattern);
		
		Assert.assertEquals("Wrong number of results", 1, results.getNumberOfResults());
		Match match = results.get(0);
		Assert.assertEquals("Wrong number of captures", 1, match.getNumberOfCaptures());
		Assert.assertEquals("Wrong path",
				new JsonPath().add(new JsonFieldElement("object child")),
				match.getPath());
		Assert.assertEquals("Wrong capture", "null", match.get("the result"));
	}
	

	@Test
	public void test3() throws IOException
	{
		JsonPattern jsonPattern = new JsonPattern();
		
		AndSearcher and = new AndSearcher();
		
		and.add(new FieldSearcher(
				new FinderSearcher(new ExistsFinder()),
				"inner array"
				));

		and.add(new FieldSearcher(
				new FinderSearcher(new ExistsFinder()),
				"has boolean"
				));

		and.add(new FieldSearcher(
				new FinderSearcher(new CaptureFinder(new ResultName("the result"))),
				"null child"
				));
		
		
		jsonPattern.add(and);

		JsonObject thePattern;
		try (JsonReader parser = javax.json.Json.createReader(new StringReader(Jsonable.toString(jsonPattern)));)
		{
			thePattern = (JsonObject) parser.read();
		}

		JsonPattern pattern = new JsonPattern(thePattern);
		
		Assert.assertEquals("Not serialized properly", jsonPattern, pattern);
	}
}


