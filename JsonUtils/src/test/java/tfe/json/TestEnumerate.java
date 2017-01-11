package tfe.json;

import org.hallock.tfe.json.grep.Match;
import org.hallock.tfe.json.grep.MultiMatch;
import org.hallock.tfe.json.grep.ResultName;
import org.hallock.tfe.json.grep.path.JsonPath;
import org.hallock.tfe.json.grep.results.CollectResults;
import org.junit.Assert;
import org.junit.Test;


public class TestEnumerate
{
	/**
	 * This is a bad test, it assumes the order of the enumerate...
	 */
	@Test
	public void test1()
	{
		MultiMatch match = new MultiMatch();
		match.seeks(new ResultName("key1"));
		match.seeks(new ResultName("key2"));
		
		match.foundMatch(new ResultName("key1"), "k1_value1");
		match.foundMatch(new ResultName("key1"), "k1_value2");
		
		match.foundMatch(new ResultName("key2"), "k2_value1");
		match.foundMatch(new ResultName("key2"), "k2_value2");
		match.foundMatch(new ResultName("key2"), "k2_value3");
		
		CollectResults collectResults = new CollectResults();
		match.enumerate(new JsonPath(), collectResults);
		
		Assert.assertEquals("wrong number of results", 6, collectResults.getNumberOfResults());
		
		Match m;
		m = collectResults.get(0);
		Assert.assertEquals("wrong number of captures", 2, m.getNumberOfCaptures());
		Assert.assertEquals("wrong capture", m.get("key1"), "k1_value1");
		Assert.assertEquals("wrong capture", m.get("key2"), "k2_value1");

		m = collectResults.get(1);
		Assert.assertEquals("wrong number of captures", 2, m.getNumberOfCaptures());
		Assert.assertEquals("wrong capture", m.get("key1"), "k1_value1");
		Assert.assertEquals("wrong capture", m.get("key2"), "k2_value2");

		m = collectResults.get(2);
		Assert.assertEquals("wrong number of captures", 2, m.getNumberOfCaptures());
		Assert.assertEquals("wrong capture", m.get("key1"), "k1_value1");
		Assert.assertEquals("wrong capture", m.get("key2"), "k2_value3");

		m = collectResults.get(3);
		Assert.assertEquals("wrong number of captures", 2, m.getNumberOfCaptures());
		Assert.assertEquals("wrong capture", m.get("key1"), "k1_value2");
		Assert.assertEquals("wrong capture", m.get("key2"), "k2_value1");

		m = collectResults.get(4);
		Assert.assertEquals("wrong number of captures", 2, m.getNumberOfCaptures());
		Assert.assertEquals("wrong capture", m.get("key1"), "k1_value2");
		Assert.assertEquals("wrong capture", m.get("key2"), "k2_value2");

		m = collectResults.get(5);
		Assert.assertEquals("wrong number of captures", 2, m.getNumberOfCaptures());
		Assert.assertEquals("wrong capture", m.get("key1"), "k1_value2");
		Assert.assertEquals("wrong capture", m.get("key2"), "k2_value3");
	}
}
