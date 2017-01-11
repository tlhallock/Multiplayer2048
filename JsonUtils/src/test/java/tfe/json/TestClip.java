package tfe.json;

import org.hallock.tfe.json.grep.Combine;
import org.junit.Assert;
import org.junit.Test;


public class TestClip
{
	@Test
	public void testEmpty()
	{
		String original = "";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", "", clipped);
	}

	@Test
	public void testAllQuotes()
	{
		String original = "\"\"\"\"\"\"\"";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", "", clipped);
	}

	@Test
	public void testSingle()
	{
		String original = "\"some string\"";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", "some string", clipped);
	}

	@Test
	public void testDouble()
	{
		String original = "\"\"some string\"\"";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", "some string", clipped);
	}

	@Test
	public void testDifferentFirst()
	{
		String original = "\"\"\"\"\"\"\"\"some string\"\"";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", "some string", clipped);
	}

	@Test
	public void testDifferentSecond()
	{
		String original = "\"\"some string\"\"\"\"\"\"\"\"\"\"\"";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", "some string", clipped);
	}

	@Test
	public void testNumber()
	{
		String original = "\"\"23948720837402\"\"\"\"\"\"\"\"\"\"\"";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", "23948720837402", clipped);
	}

	@Test
	public void testNone()
	{
		String original = "a string value";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", original, clipped);
	}


	@Test
	public void testWhite()
	{
		String original = "\"\"\t\t\n    \t\"\"\"\"\"\"\"";
		String clipped = Combine.clip(original);
		Assert.assertEquals("wrong clip results", "\t\t\n    \t", clipped);
	}
}
