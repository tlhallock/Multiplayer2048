package org.hallock.tfe.cmn.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.PrettyPrinter;

public class Json
{
	private static final JsonFactory factory = createFactory();

	private static JsonFactory createFactory()
	{
		JsonFactory jsonFactory = new JsonFactory();
		return jsonFactory;
	}
	
	public static JsonParser createParser(InputStream input) throws JsonParseException, IOException
	{
		return factory.createParser(input);
	}
	public static JsonParser createOpenedParser(InputStream input) throws JsonParseException, IOException
	{
		JsonParser parser = createParser(input);
		readOpen(parser);
		return parser;
	}

	public static void readOpen(JsonParser parser) throws IOException
	{
		if (!parser.nextToken().equals(JsonToken.START_OBJECT))
			throw new RuntimeException("Unexpected.");
		if (!parser.nextToken().equals(JsonToken.FIELD_NAME))
			throw new RuntimeException("Unexpected.");
		if (!parser.getCurrentName().equals("messages"))
			throw new RuntimeException("Unexpected.");
		if (!parser.nextToken().equals(JsonToken.START_ARRAY))
			throw new RuntimeException("Unexpected.");
	}
	
	public static JsonGenerator createUnopenedGenerator(OutputStream output) throws IOException
	{
//		new File("output/").mkdirs();
//		output = new TeeOutputStream(output, new FileOutputStream(new File("output/messagesList_" + System.currentTimeMillis() + ".json")));
		
		JsonGenerator createGenerator = factory.createGenerator(output);
		createGenerator.setPrettyPrinter(new MyPrettyPrinter());
		return createGenerator;
	}
	
	public static JsonGenerator debug(PrintStream output) throws IOException
	{
		return new DebugGenerator(output);
	}
	public static JsonGenerator debug(OutputStream output) throws IOException
	{
		return debug(new PrintStream(output));
	}
	
	public static JsonGenerator createOpenedGenerator(OutputStream output) throws IOException
	{
		JsonGenerator createGenerator = createUnopenedGenerator(output);

		createGenerator.writeStartObject();
		createGenerator.writeFieldName("messages");
		createGenerator.writeStartArray();
		createGenerator.flush();
		
		return createGenerator;
	}

	public static JsonParser createParser(String json) throws JsonParseException, IOException
	{
		return factory.createParser(json);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private static class MyPrettyPrinter implements PrettyPrinter
	{
	    private final String lf = System.getProperty("line.separator");
	    private int indentation = 0;
	    private boolean isNewline = true;

	    @Override
	    public void writeRootValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException
	    {
	        jg.writeRaw(' ');
	    }

	    @Override
	    public void writeStartObject(JsonGenerator jg) throws IOException, JsonGenerationException
	    {
	        if (!isNewline)
	            newline(jg);
	        jg.writeRaw('{');
	        ++indentation;
	        isNewline = false;
	    }

	    @Override
	    public void writeEndObject(JsonGenerator jg, int nrOfEntries) throws IOException, JsonGenerationException
	    {
	        --indentation;
	        newline(jg);
	        jg.writeRaw('}');
	        isNewline = indentation == 0;
	    }

	    @Override
	    public void writeObjectEntrySeparator(JsonGenerator jg) throws IOException, JsonGenerationException
	    {
	        jg.writeRaw(",");
	        newline(jg);
	    }

	    @Override
	    public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException
	    {
	        jg.writeRaw(": ");
	        isNewline = false;
	    }

	    @Override
	    public void writeStartArray(JsonGenerator jg) throws IOException, JsonGenerationException
	    {
	        newline(jg);
	        jg.writeRaw("[");
	        ++indentation;
	        isNewline = false;
	    }

	    @Override
	    public void writeEndArray(JsonGenerator jg, int nrOfValues) throws IOException, JsonGenerationException
	    {
	        --indentation;
	        newline(jg);
	        jg.writeRaw(']');
	        isNewline = false;
	    }

	    @Override
	    public void writeArrayValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException
	    {
	        jg.writeRaw(", ");
	        isNewline = false;
	    }

	    @Override
	    public void beforeArrayValues(JsonGenerator jg) throws IOException, JsonGenerationException
	    {
	        newline(jg);
	    }

	    @Override
	    public void beforeObjectEntries(JsonGenerator jg) throws IOException, JsonGenerationException
	    {
	        newline(jg);
	    }

	    /**
	     * Writes a newline and indentation.
	     * <p>
	     * @param jg the JsonGenerator to write to
	     * @throws IOException if an I/O error occurs
	     */
	    private void newline(JsonGenerator jg) throws IOException
	    {
	        jg.writeRaw(lf);
	        for (int i = 0; i < indentation; ++i)
	            jg.writeRaw("  ");
	        isNewline = true;
	    }
	}
}
