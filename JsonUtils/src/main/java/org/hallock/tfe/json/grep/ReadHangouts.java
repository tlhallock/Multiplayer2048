package org.hallock.tfe.json.grep;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.json.grep.results.HumanReadableMultimatchListener;
import org.hallock.tfe.json.grep.results.HumanReadableResults;
import org.hallock.tfe.json.grep.results.JsonMultimatchListener;
import org.hallock.tfe.json.grep.results.JsonResults;
import org.hallock.tfe.json.grep.results.SplitResults;
import org.hallock.tfe.json.grep.search.AndSearcher;
import org.hallock.tfe.json.grep.search.ChildSearcher;
import org.hallock.tfe.json.grep.search.FieldSearcher;
import org.hallock.tfe.json.grep.search.PathSearcher;
import org.hallock.tfe.json.grep.search.SearcherUtils;

import com.fasterxml.jackson.core.JsonGenerator;

public class ReadHangouts
{
	public static void main(String[] args) throws IOException
	{
//		String inputFile = "C:\\cygwin64\\home\\thallock\\Documents\\marriage\\SomeJson\\Hangouts.json";
//		String outputFile = "C:\\cygwin64\\home\\thallock\\Documents\\marriage\\SomeJson\\results.txt";
		
		String inputFile = "C:\\cygwin64\\home\\trever\\Documents\\Hangouts.json";
		String outputFilea = "C:\\cygwin64\\home\\trever\\Documents\\output\\all.txt";
		String outputFile1 = "C:\\cygwin64\\home\\trever\\Documents\\output\\conversation_text.json";
		String outputFile2 = "C:\\cygwin64\\home\\trever\\Documents\\output\\conversation_people.json";
		
		JsonObject object;
		
		try (InputStream input = Files.newInputStream(Paths.get(inputFile));
			JsonReader parser = javax.json.Json.createReader(input);)
		{
			object = (JsonObject) parser.read();
		}
		
		System.out.println("read");
		
//		try (PrintStream writer = new PrintStream(outputFilea);)
//		{
//			HumanReadable.print(new LinkedList<>(), object, writer);
//		}
//		System.out.println("wrote");
				
		printConversationText(object, outputFile1);
		
		System.out.println("first search done");
		
		printPeopleInConversation(object, outputFile2);
	
		System.out.println("second search done");
	}

	
	
	
	
	
	
	
	
	private static void printConversationText(JsonObject object, String outputFile) throws IOException, FileNotFoundException
	{
		JsonPattern jsonPattern = new JsonPattern();
		
		AndSearcher and = new AndSearcher();
		
		
		/*
		 * Make sure the path matches this:
		 */
		PathSearcher pathSearcher = new PathSearcher(
				SearcherUtils.ensure());
		pathSearcher.path()
			.exactField("conversation_state")
			.anyArray()
			.exactField("conversation_state")
			.exactField("event")
			.anyArray();
		
		and.add(pathSearcher);
		
		
		
		
		
		
		
		/*
		 * Make sure there is a conversation id
		 * 
		 * put it in conversation
		 */
		
		ChildSearcher conversationChild = new ChildSearcher(
				SearcherUtils.captureField("conversation", "id"));
		conversationChild.path()
			.exactField("conversation_id");
		and.add(conversationChild);
		
		
		
		
		
		
		
//		/*
//		 * Make sure the conversation is one of the ones between sunny and I
//		 * 
//		 */
//		ChildSearcher ensureRightConversation = new ChildSearcher(
//				SearcherUtils.isOneOf("id", new String[] {
//						"\"UgxUtAA8CxdkGQJy6RV4AaABAQ\"",
//					        "\"Ugwtue-LDVrmOE0hLlx4AaABAQ\"",
//					        "\"UgzNdqPzBiB30s0GAbd4AaABAQ\"",
//					        "\"UgyK8ITTybfXbsU-PIp4AaABAQ\"",
//					        "\"UgwKF2dJzgHb_BaJkNx4AaABAQ\"",
//				}));
//		ensureRightConversation.path()
//			.exactField("conversation_id");
//		and.add(ensureRightConversation);
		
		
		
		
		
		
		
		
		/*
		 * Make sure there is a text field
		 * 
		 * put it in text
		 */
		ChildSearcher childSearcher = new ChildSearcher(
				SearcherUtils.captureField("text", "text"));
		childSearcher.path()
			.exactField("chat_message")
			.exactField("message_content")
			.exactField("segment")
			.anyArray();
		and.add(childSearcher);
		
		
		
		
		
		
		
		
		/*
		 * Make sure there is a text field
		 * 
		 * put it in text
		 */
		FieldSearcher timeSearcher = SearcherUtils.captureField("time", "timestamp");
		and.add(timeSearcher);
		
		
		
		
		
		
		
		
		
		
		jsonPattern.add(and);
		
		try (JsonGenerator generator = Json.createUnopenedGenerator(Files.newOutputStream(Paths.get(outputFile)));
			PrintStream ps = new PrintStream(outputFile + ".human_readable.txt");)
		{
			JsonResults 		jsonResults  = new JsonResults(generator);
			HumanReadableResults 	humanResults = new HumanReadableResults(ps);
			SplitResults 		split        = new SplitResults(jsonResults, humanResults);
			
			jsonPattern.addListener(split);
			
			jsonResults.writeStart();
			JsonGreper.traverse(object, jsonPattern);
			jsonResults.writeEnd();
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void printPeopleInConversation(JsonObject object, String outputFile) throws IOException, FileNotFoundException
	{
		JsonPattern jsonPattern = new JsonPattern();
		AndSearcher and = new AndSearcher();
		
		
		
		

		
		
		/*
		 * Set the path
		 */
		PathSearcher pathSearcher = new PathSearcher(
			SearcherUtils.ensure());
		pathSearcher.path()
			.exactField("conversation_state")
			.anyArray()
			.exactField("conversation_state")
			.exactField("conversation");
		and.add(pathSearcher);
		
		
		
		
		

		
		
		
		
		
		
		/*
		 * Get the conversation id
		 */
		ChildSearcher conversationChild = new ChildSearcher(
			SearcherUtils.captureField("conversation id", "id"));
		conversationChild.path()
			.exactField("id");
		and.add(conversationChild);
		
		
		
		
		

		
		
		
		
		
		
		/*
		 * fall back name
		 */
		ChildSearcher fallback = new ChildSearcher(
			SearcherUtils.captureField("name", "fallback_name"));
		fallback.path()
			.exactField("participant_data")
			.anyArray();
		and.add(fallback);
		
		
		
		
		
		jsonPattern.add(and);
		
		
		try (
			JsonGenerator generator = Json.createUnopenedGenerator(Files.newOutputStream(Paths.get(outputFile)));
			PrintStream ps = new PrintStream(outputFile + ".human_readable.txt");)
		{
			HumanReadableMultimatchListener humanResults = new HumanReadableMultimatchListener(ps);
			JsonMultimatchListener jsonListener = new JsonMultimatchListener(generator);
			
			jsonPattern.addListener(humanResults);
			jsonPattern.addListener(jsonListener);

			jsonListener.writeStart();
			JsonGreper.traverse(object, jsonPattern);
			jsonListener.writeEnd();
		}
		
		System.out.println("searched");
	}
}
