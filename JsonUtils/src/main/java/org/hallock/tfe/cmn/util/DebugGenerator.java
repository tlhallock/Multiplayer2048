package org.hallock.tfe.cmn.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;

public class DebugGenerator extends JsonGenerator
{
	private PrintStream printer;
	private int indentationLevel;
	
	private static final String INDENT = "\t";
	
	enum State
	{
		Empty,
		WroteField,
		WroteValue,
	}
	State state;

	public DebugGenerator(PrintStream writer)
	{
		this.printer = writer;
		this.state = State.Empty;
	}

	@Override
	public void writeStartArray() throws IOException
	{
		switch (state)
		{
		case WroteField:
		case Empty:
			break;
		case WroteValue:
			printer.print(",\n");
			AnotherUtils.indent(printer, indentationLevel, INDENT);
			break;
		default:
			throw new RuntimeException(state.name());
		}
		printer.print("[\n");
		indentationLevel++;
		state = State.Empty;
		AnotherUtils.indent(printer, indentationLevel, INDENT);
	}

	@Override
	public void writeEndArray() throws IOException
	{
		switch (state)
		{
		case Empty:
			break;
		case WroteValue:
			printer.print("\n");
			AnotherUtils.indent(printer, indentationLevel - 1, INDENT);
			break;
		default:
			throw new RuntimeException(state.name());
		}
		printer.print("]");
		indentationLevel--;
		state = State.WroteValue;
	}

	@Override
	public void writeStartObject() throws IOException
	{
		switch (state)
		{
		case WroteField:
		case Empty:
			break;
		case WroteValue:
			printer.print(",\n");
			AnotherUtils.indent(printer, indentationLevel, INDENT);
			break;
		default:
			throw new RuntimeException(state.name());
		}
		printer.print("{\n");
		indentationLevel++;
		
		state = State.Empty;
		AnotherUtils.indent(printer, indentationLevel, INDENT);
	}

	@Override
	public void writeEndObject() throws IOException
	{
		switch (state)
		{
		case Empty:
			break;
		case WroteValue:
			printer.print("\n");
			AnotherUtils.indent(printer, indentationLevel - 1, INDENT);
			break;
		default:
			throw new RuntimeException(state.name());
		}
		printer.print("}");
		indentationLevel--;
		state = State.WroteValue;
	}

	private void writeValue(String value)
	{
		switch (state)
		{
		case WroteField:
		case Empty:
			break;
		case WroteValue:
			printer.print(",\n");
			AnotherUtils.indent(printer, indentationLevel, INDENT);
			break;
		default:
			throw new RuntimeException(state.name());
		}
//		AnotherUtils.indent(printer, indentationLevel);
		printer.print(value);
		state = State.WroteValue;
	}
	
	@Override
	public void writeFieldName(String name) throws IOException
	{
		switch (state)
		{
		case Empty:
			break;
		case WroteValue:
			printer.print(",\n");
			AnotherUtils.indent(printer, indentationLevel, INDENT);
			break;
		default:
			throw new RuntimeException(state.name());
		}
		
		printer.print('"' + name + '"' + " : ");
		state = State.WroteField;
	}

	@Override
	public void writeFieldName(SerializableString name) throws IOException
	{
		writeFieldName(String.valueOf(name));
	}

	@Override
	public void writeNumber(int v) throws IOException
	{
		writeValue(String.valueOf(v));
	}

	@Override
	public void writeNumber(long v) throws IOException
	{
		writeValue(String.valueOf(v));
	}

	@Override
	public void writeNumber(BigInteger v) throws IOException
	{
		writeValue(String.valueOf(v));
	}

	@Override
	public void writeNumber(double v) throws IOException
	{
		writeValue(String.valueOf(v));
	}

	@Override
	public void writeNumber(float v) throws IOException
	{
		writeValue(String.valueOf(v));
	}

	@Override
	public void writeNumber(BigDecimal v) throws IOException
	{
		writeValue(String.valueOf(v));
	}

	@Override
	public void writeNumber(String encodedValue) throws IOException
	{
		writeValue(encodedValue);
	}

	@Override
	public void writeBoolean(boolean state) throws IOException
	{
		writeValue(String.valueOf(state));
	}

	@Override
	public void writeNull() throws IOException
	{
		writeValue("null");
	}

	@Override
	public void writeString(String text) throws IOException
	{
		writeValue('"' + escape(text) + '"');
	}

	private static String escape(String text)
	{
		return text.replace("\"", "<censored>");
	}

	@Override
	public void writeString(char[] text, int offset, int len) throws IOException
	{
		writeString(new String(text, offset, len));
	}

	@Override
	public void writeString(SerializableString text) throws IOException
	{
		writeString(String.valueOf(text));
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	@Override
	public JsonGenerator setCodec(ObjectCodec oc)
	{
		throw new RuntimeException();
	}

	@Override
	public ObjectCodec getCodec()
	{
		throw new RuntimeException();
	}

	@Override
	public Version version()
	{
		throw new RuntimeException();
	}

	@Override
	public JsonGenerator enable(Feature f)
	{
		throw new RuntimeException();
	}

	@Override
	public JsonGenerator disable(Feature f)
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isEnabled(Feature f)
	{
		throw new RuntimeException();
	}

	@Override
	public int getFeatureMask()
	{
		throw new RuntimeException();
	}

	@Override
	public JsonGenerator setFeatureMask(int values)
	{
		throw new RuntimeException();
	}

	@Override
	public JsonGenerator useDefaultPrettyPrinter()
	{
		throw new RuntimeException();
	}
	
	
	
	
	
	@Override
	public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeUTF8String(byte[] text, int offset, int length) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeRaw(String text) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeRaw(String text, int offset, int len) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeRaw(char[] text, int offset, int len) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeRaw(char c) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeRawValue(String text) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeRawValue(String text, int offset, int len) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeRawValue(char[] text, int offset, int len) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeObject(Object pojo) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public void writeTree(TreeNode rootNode) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public JsonStreamContext getOutputContext()
	{
		throw new RuntimeException();
	}

	@Override
	public void flush() throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isClosed()
	{
		throw new RuntimeException();
	}

	@Override
	public void close() throws IOException
	{
		printer.flush();
	}

}
