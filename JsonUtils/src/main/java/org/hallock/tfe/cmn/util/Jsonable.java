package org.hallock.tfe.cmn.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

public interface Jsonable
{
	public void write(JsonGenerator generator) throws IOException;
	
	public static String toString(Jsonable jsonable)
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		try (JsonGenerator generator = Json.debug(System.out);)
		try (JsonGenerator generator = Json.createUnopenedGenerator(output);)
		{
			jsonable.write(generator);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return "error";
		}
		return new String(output.toByteArray());
	}
}
