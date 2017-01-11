package org.hallock.tfe.cmn.game.evil;

import java.io.IOException;
import java.util.ArrayList;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.sys.GameConstants;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AddRandomMove extends EvilAction
{
	private static final PossiblePlayerActions[] possibles = new PossiblePlayerActions[]
	{
		PossiblePlayerActions.Left,
		PossiblePlayerActions.Down,
		PossiblePlayerActions.Right, 
		PossiblePlayerActions.Up
	};
	
	ArrayList<PossiblePlayerActions> actions = new ArrayList<>();
	
	public AddRandomMove(int number)
	{
		for (int i = 0; i < number; i++)
			actions.add(possibles[GameConstants.random.nextInt(possibles.length)]);
	}

	public AddRandomMove(JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (next = parser.nextToken())
			{
			case START_ARRAY:
				switch (currentName)
				{
				case "actions":
					while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
					{
						switch (next)
						{
						case VALUE_STRING:
							actions.add(PossiblePlayerActions.valueOf(parser.getValueAsString()));
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
		
		validate();
	}

	private void validate()
	{
		for (PossiblePlayerActions action : actions)
		{
			boolean found = false;
			for (int i = 0; i < possibles.length && !found; i++)
				if (action.equals(possibles[i]))
					found = true;
			if (!found)
				throw new RuntimeException();
		}
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeFieldName("actions");
		generator.writeStartArray();
		for (PossiblePlayerActions action : actions)
			generator.writeString(action.name());
		generator.writeEndArray();
		generator.writeEndObject();
	}

	@Override
	public EvilActionType getType()
	{
		return EvilActionType.AddRandomMove;
	}

	public int getNumberOfMoves()
	{
		return actions.size();
	}

	public PossiblePlayerActions getMove(int i)
	{
		return actions.get(i);
	}

	public long getWaitTime()
	{
		return 1000;
	}

	@Override
	boolean optionsAreEqual(EvilAction other)
	{
		if (actions.size() != ((AddRandomMove) other).actions.size())
			return false;
		
		for (int i = 0; i < actions.size(); i++)
		{
			if (actions.get(i).equals(((AddRandomMove) other).actions.get(i)))
				return false;
		}
		
		return true;
	}
}
