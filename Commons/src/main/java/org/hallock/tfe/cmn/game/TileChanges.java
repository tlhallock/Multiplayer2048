package org.hallock.tfe.cmn.game;

import java.io.IOException;
import java.util.LinkedList;

import org.hallock.tfe.cmn.game.TileChanges.TileChange;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.sys.PointsCounter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class TileChanges extends LinkedList<TileChange> implements Jsonable
{
	public TileChanges() {}
	
	public TileChanges(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case START_ARRAY:
				switch (currentName)
				{
				case "changes":
					while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
					{
						switch (next)
						{
						case START_OBJECT:
							TileChange tileChange = new TileChange(parser);
							add(tileChange);
							break;
						default:
							throw new RuntimeException("Unexpected.");
						}
					}
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			default:
				throw new RuntimeException("Unexpected.");
			}
		}
	}
	
	
	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeFieldName("changes");
		generator.writeStartArray();
		for (TileChange change : this)
			change.write(generator);
		generator.writeEndArray();
		generator.writeEndObject();
	}

	public boolean changed()
	{
		for (TileChange change : this)
			if (change.changed())
				return true;
		return false;
	}
	
	
	
	
	
	
	
	
	
	public void countPoints(PointsCounter counter)
	{
		counter.startTurn();
		for (TileChange change : this)
		{
			if (change.from2Col >= 0 && change.from2Row >= 0)
			{
				counter.countCombine(change.toNum);
			}
		}
		counter.stopTurn();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static class TileChange implements Jsonable
	{
		public int fromNum;
		
		public int from1Row;
		public int from1Col;
		
		public int from2Row;
		public int from2Col;

		public int toNum;
		
		public int toRow;
		public int toCol;

		public  boolean isNew;
		
		public TileChange(int fromNum, int from1Row, int from1Col, int from2Row, int from2Col, int toNum, int toRow, int toCol)
		{
			this.fromNum = fromNum;
			this.from1Row = from1Row;
			this.from1Col = from1Col;
			this.from2Row = from2Row;
			this.from2Col = from2Col;
			this.toNum = toNum;
			this.toRow = toRow;
			this.toCol = toCol;
			this.isNew = false;
		}
		
		public TileChange(int fromNum, int from1Row, int from1Col, int toRow, int toCol)
		{
			this.fromNum = fromNum;
			this.from1Row = from1Row;
			this.from1Col = from1Col;
			this.from2Row = -1;
			this.from2Col = -1;
			this.toNum = fromNum;
			this.toRow = toRow;
			this.toCol = toCol;
			this.isNew = false;
		}

		public TileChange(int fromNum, int from1Row, int from1Col, boolean isNew)
		{
			this.fromNum = fromNum;
			this.from1Row = from1Row;
			this.from1Col = from1Col;
			this.from2Row = -1;
			this.from2Col = -1;
			this.toNum = fromNum;
			this.toRow = from1Row;
			this.toCol = from1Col;
			this.isNew = isNew;
		}

		public TileChange(JsonParser parser) throws IOException
		{
			toNum = -1;
			fromNum = -1;
			from1Row = -1;
			from1Col = -1;
			from2Row = -1;
			from2Col = -1;
			toRow = -1;
			toCol = -1;

			JsonToken next;
			while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
			{
				if (!next.equals(JsonToken.FIELD_NAME))
					throw new RuntimeException("Unexpected.");

				String currentName = parser.getCurrentName();
				switch (parser.nextToken())
				{
				case VALUE_NUMBER_INT:
					switch (currentName)
					{
					case "f1r": from1Row = parser.getIntValue(); break;
					case "f1c": from1Col = parser.getIntValue(); break;
					case "f2r": from2Row = parser.getIntValue(); break;
					case "f2c": from2Col = parser.getIntValue(); break;
					case "tr" : toRow    = parser.getIntValue(); break;
					case "tc" : toCol    = parser.getIntValue(); break;
					case "fn" : fromNum  = parser.getIntValue(); break;
					case "tn" : toNum    = parser.getIntValue(); break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case VALUE_FALSE:
					switch (currentName)
					{
					case "n":
						isNew = false;
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case VALUE_TRUE:
					switch (currentName)
					{
					case "n":
						isNew = true;
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
			}
		}
		
		@Override
		public void write(JsonGenerator generator) throws IOException
		{
			generator.writeStartObject();
			generator.writeNumberField("f1r", from1Row);
			generator.writeNumberField("f1c", from1Col);
			generator.writeNumberField("f2r", from2Row);
			generator.writeNumberField("f2c", from2Col);
			generator.writeNumberField("tr", toRow);
			generator.writeNumberField("tc", toCol);
			generator.writeNumberField("fn", fromNum);
			generator.writeNumberField("tn", toNum);
			generator.writeBooleanField("n", isNew);
			generator.writeEndObject();
		}
		
		public boolean changed()
		{
			return
				isNew ||
				from1Row != toRow ||
				from1Col != toCol ||
				(from2Row >= 0 && from2Col >= 0 && (from2Row != toRow || from2Col != toCol)) ||
				fromNum != toNum;
		}
//		
//		public boolean changedTo(int other)
//		{
//			return changed() && combined() && toNum == other;
//		}
	}
}
