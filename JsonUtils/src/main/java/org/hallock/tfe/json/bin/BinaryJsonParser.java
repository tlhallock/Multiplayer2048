package org.hallock.tfe.json.bin;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;

public class BinaryJsonParser extends JsonParser implements BinaryJson
{
	static JsonToken toToken(int b)
	{
		if (BinaryJson.isShortString(b))
		{
			int numberOfArgs = BinaryJson.getShortStringArg(b);
			return JsonToken.VALUE_STRING;
		}
		else if (BinaryJson.isShortArray(b))
		{
			int numberOfArgs = BinaryJson.getShortArrayArg(b);
			return JsonToken.START_ARRAY;
		} 
		else if (BinaryJson.isShortObject(b))
		{
			int numberOfArgs = BinaryJson.getShortObjectArg(b);
			return JsonToken.START_OBJECT;
		}
		else if (BinaryJson.isShortNumber(b))
		{
			int numberOfArgs = BinaryJson.getShortNumberArg(b);
			return JsonToken.VALUE_NUMBER_INT;
		}
		
		switch (b)
		{
		case START_OBJECT : return JsonToken.START_OBJECT;
		case END_OBJECT   : return JsonToken.END_OBJECT;
		case START_ARRAY  : return JsonToken.START_ARRAY;
		case END_ARRAY    : return JsonToken.END_ARRAY;
		case START_STRING : return JsonToken.VALUE_STRING;
		case START_NUMBER : return JsonToken.VALUE_NUMBER_INT;
		case VALUE_NULL   : return JsonToken.VALUE_NULL;
		case VALUE_TRUE   : return JsonToken.VALUE_TRUE;
		case VALUE_FALSE  : return JsonToken.VALUE_FALSE;
		case DOUBLE_VALUE : return JsonToken.VALUE_NUMBER_FLOAT;
		case FLOAT_VALUE  : return JsonToken.VALUE_NUMBER_FLOAT;
		case DOUBLE_TO_4  : return JsonToken.VALUE_NUMBER_FLOAT;
		case DOUBLE_TO_3  : return JsonToken.VALUE_NUMBER_FLOAT;
		case DOUBLE_TO_2  : return JsonToken.VALUE_NUMBER_FLOAT;
		case DOUBLE_TO_1  : return JsonToken.VALUE_NUMBER_FLOAT;
		case RESERVED     : 
			default:
		}
		
		throw new RuntimeException();
	}
	
	ArrayList<Integer> depthStack = new ArrayList<>();
	int nextByte;
	ByteBuffer bb;
//	
//	public void read()
//	{
//		  ByteBuffer bb = ByteBuffer.allocateDirect(64*1024);
//		  bb.clear();
//		  long len = 0;
//		  int offset = 0;
//		  while ((len = channel.read(bb))!= -1){
//		    bb.flip();
//		    //System.out.println("Offset: "+offset+"\tlen: "+len+"\tremaining:"+bb.hasRemaining());
//		    bb.asIntBuffer().get(ipArr,offset,(int)len/4);
//		    offset += (int)len/4;
//		    bb.clear();
//		  }
//	}


	@Override
	public void close() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonToken nextToken() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonToken nextValue() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonParser skipChildren() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isClosed()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonToken getCurrentToken()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean hasCurrentToken()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean hasTokenId(int id)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean hasToken(JsonToken t)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getCurrentName() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Number getNumberValue() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public NumberType getNumberType() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getIntValue() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public long getLongValue() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public BigInteger getBigIntegerValue() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public float getFloatValue() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public double getDoubleValue() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public BigDecimal getDecimalValue() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	@Override
	public int getCurrentTokenId()
	{
		throw new RuntimeException("Not implemented");
	}
	@Override
	public ObjectCodec getCodec()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setCodec(ObjectCodec c)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Version version()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonStreamContext getParsingContext()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonLocation getTokenLocation()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonLocation getCurrentLocation()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void clearCurrentToken()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JsonToken getLastClearedToken()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void overrideCurrentName(String name)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getText() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public char[] getTextCharacters() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getTextLength() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getTextOffset() throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean hasTextCharacters()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public byte[] getBinaryValue(Base64Variant bv) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getValueAsString(String def) throws IOException
	{
		throw new RuntimeException("Not implemented");
	}

}