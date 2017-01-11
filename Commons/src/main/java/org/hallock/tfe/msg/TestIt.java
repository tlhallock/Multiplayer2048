//package org.hallock.tfe.msg;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.math.BigDecimal;
//
//import org.hallock.tfe.cmn.game.TileBoard;
//import org.hallock.tfe.cmn.util.Json;
//import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
//import org.hallock.tfe.msg.SimpleParser.NullableActionHandler;
//import org.hallock.tfe.msg.SimpleParser.ObjectReader;
//import org.hallock.tfe.msg.SimpleParser.SimpleActionReader;
//import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonToken;
//
//public class TestIt
//{
//	public static void testValueListener1() throws IOException
//	{
//		String json = " { \"id\": 25 }";
//		JsonParser createParser = Json.createParser(json);
//		
//		KnownValueReader handler = new KnownValueReader(false);
//		SimpleKnownValue<BigDecimal> value = new SimpleKnownValue<BigDecimal>();
//		handler.addNumberListener("/id", value);
//
//		if (!createParser.nextToken().equals(JsonToken.START_OBJECT))
//			throw new RuntimeException();
//		
//		SimpleParser.parseCurrentObject(handler, createParser);
//		
//		System.out.println(value.getValue());
//		
//		handler.assumeSet();
//	}
//	
//	public static void testValueListener() throws IOException
//	{
//		String json = " { \"foobar\": {}, \"id\": 25,  \"raboof\": {} }";
//		JsonParser createParser = Json.createParser(json);
//		
//		KnownValueReader handler = new KnownValueReader(false);
//		SimpleKnownValue<BigDecimal> value = new SimpleKnownValue<BigDecimal>();
//		handler.addNumberListener("/id", value);
//
//		if (!createParser.nextToken().equals(JsonToken.START_OBJECT))
//			throw new RuntimeException();
//		
//		SimpleParser.parseCurrentObject(handler, createParser);
//		
//		System.out.println(value.getValue());
//		
//		handler.assumeSet();
//	}
//	
//	public static void testValueListener2() throws IOException
//	{
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		try (JsonGenerator gen = Json.createUnopenedGenerator(output);)
//		{
//			gen.writeStartObject();
//			gen.writeFieldName("the thing");
//			new TileBoard(4,5).write(gen);
//			gen.writeEndObject();
//		}
//		String json = new String(output.toByteArray());
//		JsonParser createParser = Json.createParser(json);
//		
//		System.out.println("Json: " + json);
//
//		KnownValueReader handler = new KnownValueReader(false);
//		SimpleKnownValue<TileBoard> value = new SimpleKnownValue<TileBoard>();
//		
//		handler.addObjectListener("/the thing", value, new ObjectReader<TileBoard>() {
//			@Override
//			public TileBoard parse(JsonParser parser) throws IOException
//			{
//				return new TileBoard(parser);
//			}});
//
//		if (!createParser.nextToken().equals(JsonToken.START_OBJECT))
//			throw new RuntimeException();
//		
//		SimpleParser.parseCurrentObject(handler, createParser);
//		
//		System.out.println(value.getValue());
//		
//		handler.assumeSet();
//	}
//
//	
//	
//	
//	
//	
//	public static void testActionListener() throws IOException
//	{
//
//		String json = " { \"id\": 25 }";
//		JsonParser createParser = Json.createParser(json);
//		
//		SimpleActionReader handler = new SimpleActionReader(false);
//		handler.addNumberListener("/id", new NullableActionHandler<BigDecimal>()
//		{
//			@Override
//			public void handle(BigDecimal t)
//			{
//				System.out.println("Found id: " + t);
//			}
//		});
//
//		if (!createParser.nextToken().equals(JsonToken.START_OBJECT))
//			throw new RuntimeException();
//		
//		SimpleParser.parseCurrentObject(handler, createParser);
//		
//		handler.assumeSet();
//	}
//
//
//	public static void main(String[] args) throws JsonParseException, IOException
//	{
//		testValueListener2();
//	}
//}
