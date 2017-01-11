package org.hallock.tfe.msg;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;

public abstract class MessageHandler
{
	public abstract String getType();
	public abstract void handle(JsonParser parser) throws IOException;
	
	
//	public static void finishObject(JsonParser parser) throws IOException
//	{
//		int depth = 0;
//		while (depth >= 0)
//		{
//			switch (parser.nextToken())
//			{
//			case END_OBJECT:
//				depth--;
//				break;
//			case START_OBJECT:
//				depth++;
//				break;
//			default:
//				// ignore...
//			}
//		}
//	}
}
