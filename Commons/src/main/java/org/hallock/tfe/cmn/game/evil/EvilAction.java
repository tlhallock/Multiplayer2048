package org.hallock.tfe.cmn.game.evil;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.msg.SimpleParser.ObjectReader;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public abstract class EvilAction implements Jsonable
{
	public long appliedTime;
	
	public abstract EvilActionType getType();

	public String getDescription()
	{
		return "Description with params coming.";
	}
	
	public boolean equals(EvilAction other)
	{
		if (!getType().equals(other.getType()))
			return false;
		return optionsAreEqual(other);
	}
	
	abstract boolean optionsAreEqual(EvilAction other);
	
	@Override
	public String toString()
	{
		return Jsonable.toString(new EvilActionContainer(this));
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	
	
	
	

	public enum EvilActionType
	{
		BlockCell,
		AddInMoreTiles,
		AddHighTile,
		RemoveSwipeAction,
		Distract,
		DelayMoves,
		AddRandomMove,
		HideNumberValues,
		RemovePoints,
		DepriveEvilActions,
		DeflectEvilAction,
		NoUndoRedo,
		;
		
		public EvilAction parse(JsonParser parser) throws IOException
		{
			switch (this)
			{
			case AddHighTile:
				return new AddHighTile(parser);
			case AddInMoreTiles:
				return new AddInMoreTiles(parser);
			case AddRandomMove:
				return new AddRandomMove(parser);
			case BlockCell:
				return new BlockCell(parser);
			case DeflectEvilAction:
				return new DeflectEvilAction(parser);
			case DelayMoves:
				return new DelayMoves(parser);
			case DepriveEvilActions:
				return new DepriveEvilActions(parser);
			case Distract:
				return new Distract(parser);
			case HideNumberValues:
				return new HideNumberValues(parser);
			case RemovePoints:
				return new RemovePoints(parser);
			case RemoveSwipeAction:
				return new RemoveSwipeAction(parser);
			case NoUndoRedo:
				return new NoUndoRedo(parser);
			default:
				throw new RuntimeException("Not here");
			}
		}
	}
	
	
	public static class EvilActionContainer implements Jsonable
	{
		public static ObjectReader<EvilActionContainer> READER = new ObjectReader<EvilActionContainer>() {
			@Override
			public EvilActionContainer parse(JsonParser parser) throws IOException
			{
				return new EvilActionContainer(parser);
			}};
		
		EvilAction action;
		
		public EvilActionContainer(EvilAction action)
		{
			this.action = action;
		}
		
		public EvilAction getAction()
		{
			return action;
		}
		
		public EvilActionContainer(JsonParser parser) throws IOException
		{
			EvilActionType type = null;
			JsonToken next;
			while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
			{
				if (!next.equals(JsonToken.FIELD_NAME))
					throw new RuntimeException("Unexpected.");

				String currentName = parser.getCurrentName();
				switch (parser.nextToken())
				{
				case VALUE_STRING:
					switch (currentName)
					{
					case "action_type":
						type = EvilActionType.valueOf(parser.getValueAsString());
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case START_OBJECT:
					switch (currentName)
					{
					case "action":
						action = type.parse(parser);
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
			generator.writeStringField("action_type", action.getType().name());
			generator.writeFieldName("action");
			action.write(generator);
			generator.writeEndObject();
		}
	}
}
