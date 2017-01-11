package org.hallock.tfe.json.bin;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;

public class BinaryJsonGenerator extends JsonGenerator implements BinaryJson
{
	@Override
	public void writeStartArray() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeEndArray() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeStartObject() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeEndObject() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeFieldName(String name) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeFieldName(SerializableString name) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeString(String text) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeString(char[] text, int offset, int len) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeString(SerializableString text) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeNumber(int v) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeNumber(long v) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeNumber(BigInteger v) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeNumber(double v) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeNumber(float v) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeNumber(BigDecimal v) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeNumber(String encodedValue) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeBoolean(boolean state) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeNull() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void flush() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isClosed()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void close() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public JsonGenerator setCodec(ObjectCodec oc)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ObjectCodec getCodec()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Version version()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonGenerator enable(Feature f)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonGenerator disable(Feature f)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isEnabled(Feature f)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getFeatureMask()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonGenerator setFeatureMask(int values)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonGenerator useDefaultPrettyPrinter()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeUTF8String(byte[] text, int offset, int length) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeRaw(String text) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeRaw(String text, int offset, int len) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeRaw(char[] text, int offset, int len) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeRaw(char c) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeRawValue(String text) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeRawValue(String text, int offset, int len) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeRawValue(char[] text, int offset, int len) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeObject(Object pojo) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void writeTree(TreeNode rootNode) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonStreamContext getOutputContext()
	{
		throw new RuntimeException("Not implemented");
	}
}