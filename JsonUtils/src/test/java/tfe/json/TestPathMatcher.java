package tfe.json;

import org.hallock.tfe.json.grep.path.JsonPath;
import org.hallock.tfe.json.grep.path.JsonPathElement;
import org.hallock.tfe.json.grep.path.PathMatcher;

public class TestPathMatcher
{
	public static void main(String[] args)
	{
		{
			JsonPath path = new JsonPath()
				.add(new JsonPathElement.JsonFieldElement("foo"))
				.add(new JsonPathElement.JsonFieldElement("bar"));
			
			PathMatcher matcher = new PathMatcher()
					.exactField("foo")
					.exactField("bar");
			
			System.out.println("Path: " + path);
			System.out.println("Matcher: " + matcher);
			System.out.println(matcher.matches(path));
		}
		
		

		{
			JsonPath path = new JsonPath()
				.add(new JsonPathElement.JsonFieldElement("foo"))
				.add(new JsonPathElement.JsonFieldElement("bar"));
			
			PathMatcher matcher = new PathMatcher()
					.exactField("foo");

			System.out.println("Path: " + path);
			System.out.println("Matcher: " + matcher);
			System.out.println(matcher.matches(path));
		}
		
		

		{
			JsonPath path = new JsonPath()
				.add(new JsonPathElement.JsonFieldElement("foo"));
			
			PathMatcher matcher = new PathMatcher()
					.exactField("foo")
					.exactField("bar");

			System.out.println("Path: " + path);
			System.out.println("Matcher: " + matcher);
			System.out.println(matcher.matches(path));
		}

		{
			JsonPath path = new JsonPath()
					.add(new JsonPathElement.JsonFieldElement("foo"))
					.add(new JsonPathElement.JsonFieldElement("rab"));
			
			PathMatcher matcher = new PathMatcher()
					.exactField("foo")
					.exactField("bar");

			System.out.println("Path: " + path);
			System.out.println("Matcher: " + matcher);
			System.out.println(matcher.matches(path));
		}

		{
			JsonPath path = new JsonPath()
				.add(new JsonPathElement.JsonFieldElement("foo"))
				.add(new JsonPathElement.JsonFieldElement("bar"));
			
			PathMatcher matcher = new PathMatcher()
					.any()
					.exactField("bar");
			
			System.out.println("Path: " + path);
			System.out.println("Matcher: " + matcher);
			System.out.println(matcher.matches(path));
		}
	}
}
