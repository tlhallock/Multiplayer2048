package org.hallock.tfe.serve;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.game.evil.EvilAction.EvilActionContainer;
import org.hallock.tfe.cmn.util.Jsonable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class GamePlayerInfo implements Jsonable
{
	public String name;
	public BigDecimal points;
	public int playerNumber;
	public boolean done;
	public TileBoard board;
	public TileChanges changes;
	public int turnId;
	public LinkedList<EvilAction> availableActions = new LinkedList<>();
	public LinkedList<EvilAction> appliedActions = new LinkedList<>();

	public GamePlayerInfo(InGamePlayer player)
	{
		this.playerNumber = player.playerNumber;
		board = new TileBoard(player.getBoard());
		changes = player.getChanges();
		turnId = player.getTurnId();
		name = player.getName();
		points = player.points.getPoints();
		done = player.finished;
		availableActions.addAll(player.availableActions);
		appliedActions.addAll(player.appliedActions);
	}

	public GamePlayerInfo() {}

	public GamePlayerInfo(JsonParser parser) throws IOException
	{
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
				case "gnumber":
					playerNumber = parser.getIntValue();
					break;
				case "points":
					points = parser.getDecimalValue();
					break;
				case "turnId":
					turnId = parser.getIntValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_STRING:
				switch (currentName)
				{
				case "name":
					name = parser.getValueAsString();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_FALSE:
				switch (currentName)
				{
				case "finished":
					done = false;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_TRUE:
				switch (currentName)
				{
				case "finished":
					done = true;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_OBJECT:
				switch (currentName)
				{
				case "board":
					board = new TileBoard(parser);
					break;
				case "how":
					changes = new TileChanges(parser);
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_NULL:
				switch (currentName)
				{
				case "how":
					changes = null;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_ARRAY:
				switch (currentName)
				{
				case "availableActions":
					while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
					{
						switch (next)
						{
						case START_OBJECT:
							availableActions.add(new EvilActionContainer(parser).getAction());
							break;
						default:
							throw new RuntimeException("Unexpected.");
						}
					}
					break;
				case "appliedActions":
					while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
					{
						switch (next)
						{
						case START_OBJECT:
							appliedActions.add(new EvilActionContainer(parser).getAction());
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
	public void write(JsonGenerator writer) throws IOException
	{
		writer.writeStartObject();
		
		writer.writeStringField("name", name);
		writer.writeNumberField("points", points);
		writer.writeNumberField("gnumber", playerNumber);
		writer.writeNumberField("turnId", turnId);
		writer.writeBooleanField("finished", done);
		
		writer.writeFieldName("availableActions");
		writer.writeStartArray();
		for (EvilAction action : availableActions)
		{
			new EvilActionContainer(action).write(writer);
		}
		writer.writeEndArray();
		

		writer.writeFieldName("appliedActions");
		writer.writeStartArray();
		for (EvilAction action : appliedActions)
		{
			new EvilActionContainer(action).write(writer);
		}
		writer.writeEndArray();
		

		
		if (changes == null)
		{
			writer.writeNullField("how");
		}
		else
		{
			writer.writeFieldName("how");
			changes.write(writer);
		}
		
		writer.writeFieldName("board");
		board.write(writer);
		
		writer.writeEndObject();
	}
}
