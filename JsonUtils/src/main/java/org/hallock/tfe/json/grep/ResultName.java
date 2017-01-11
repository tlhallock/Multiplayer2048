package org.hallock.tfe.json.grep;

import java.io.IOException;

import javax.json.JsonObject;

import org.hallock.tfe.cmn.util.Jsonable;

import com.fasterxml.jackson.core.JsonGenerator;

public class ResultName implements Comparable<ResultName>, Jsonable
{
	public final String name;
	public final boolean required;
	
	public ResultName(String name, boolean required)
	{
		this.name = name;
		this.required = required;
	}
	
	public ResultName(String name)
	{
		this(name, true);
	}
	
	public ResultName(JsonObject jsonObject)
	{
		name = jsonObject.getString("name");
		required = jsonObject.getBoolean("required");
	}
	
	@Override
	public String toString()
	{
		return "{" + name + ":" + required + "}";
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		return other instanceof ResultName && ((ResultName) other).name.equals(name);
	}

	@Override
	public int compareTo(ResultName arg0)
	{
		return name.compareTo(arg0.name);
	}
	
	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeStringField("name", name);
		generator.writeBooleanField("required", required);
		generator.writeEndObject();
	}
}
