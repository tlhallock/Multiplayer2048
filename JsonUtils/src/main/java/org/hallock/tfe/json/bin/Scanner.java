package org.hallock.tfe.json.bin;

import java.io.IOException;

import org.omg.CORBA.portable.InputStream;

public class Scanner
{
	int currentStart;
	int currentEnd;
	int currentReadEnd;
	int unexcapedLength;
	byte[] buffer;
	
	InputStream input;

	public Scanner(int size, InputStream input)
	{
		buffer = new byte[size];
		this.input = input;
	}
	
	public boolean shift()
	{
		if (currentStart == 0)
			return false;
		
		int length = currentReadEnd - currentStart;
		for (int i = 0; i < length; i++)
		{
			buffer[i] = buffer[currentStart + i];
		}
		currentReadEnd -= currentStart;
		currentEnd -= currentStart;
		currentStart = 0;
		return true;
	}
	
	public int fill() throws IOException
	{
		if (currentReadEnd == buffer.length)
			if (!shift())
			{
				byte[] newBuffer = new byte[2 * buffer.length];
				for (int i = 0; i < buffer.length; i++)
					newBuffer[i] = buffer[i];
				buffer = newBuffer;
			}
		int bytesRead = input.read(buffer, currentReadEnd, buffer.length  - currentReadEnd);
		currentReadEnd += bytesRead;
		return bytesRead;
	}

	public void markUpTo(int value) throws IOException
	{
		do
		{
			for (int i = currentStart; i < currentReadEnd; i++)
			{
				if ((0xff & buffer[i]) == value)
					currentReadEnd = i;
				break;
			}
		}
		while (fill() > 0);
	}

	
	public void next()
	{
		currentStart = currentEnd;
	}
	
	
	

	public int currentByte()
	{
		currentEnd = currentStart + 1;
		return buffer[currentStart];
	}
	public void markLength(int length) throws IOException
	{
		while (currentReadEnd - currentStart < length && fill() > 0)
			;
		// EOF?
		currentEnd = currentStart + length;	
	}
	public String currentShortString(int length) throws IOException 
	{
		markLength(length);
		return new String(buffer, currentStart, currentEnd);
	}
	private void unescape()
	{
		unexcapedLength = 0;
		for (int i = currentStart; i < currentEnd; i++)
		{
			int val = buffer[i] & 0xff;
			if (val == BinaryJson.ESCAPE_BYTE)
			{
				buffer[unexcapedLength++] = buffer[++i];
			}
			else
			{
				buffer[unexcapedLength++] = buffer[  i];
			}
		}
	}

	public String currentString() throws IOException
	{
		markUpTo(0);
		unescape();
		return new String(buffer, currentStart, unexcapedLength);
	}
	
}
