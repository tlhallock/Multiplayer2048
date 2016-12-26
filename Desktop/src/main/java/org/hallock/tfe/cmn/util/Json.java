package org.hallock.tfe.cmn.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.output.TeeOutputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class Json
{
	private static final JsonFactory factory = extracted();

	private static JsonFactory extracted()
	{
		JsonFactory jsonFactory = new JsonFactory();
		return jsonFactory;
	}
	
	public static JsonParser createParser(InputStream input) throws JsonParseException, IOException
	{
		return factory.createParser(input);
	}
	
	public static JsonGenerator createUnopenedGenerator(OutputStream output) throws IOException
	{
		output = new TeeOutputStream(output, new FileOutputStream(new File("messagesList_" + Math.random() + ".json")));
		
		JsonGenerator createGenerator = factory.createGenerator(output);
		createGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
		return createGenerator;
	}
	
	public static JsonGenerator createOpenedGenerator(OutputStream output) throws IOException
	{
		JsonGenerator createGenerator = createUnopenedGenerator(output);
		Connection.opened(createGenerator);
		createGenerator.flush();
		return createGenerator;
	}

}
