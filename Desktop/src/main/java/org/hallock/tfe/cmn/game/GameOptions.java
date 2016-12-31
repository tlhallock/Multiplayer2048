package org.hallock.tfe.cmn.game;

import java.io.IOException;

import org.hallock.tfe.cmn.svr.EvilActionsAwarder;
import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.serve.PointsCounter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class GameOptions
{
	public int numberOfNewTilesPerTurn = 1;
	public int startingTiles = 3;
	public boolean skipIsAnOption;
	public DiscreteDistribution newTileDistribution = createDefaultDistribution();
	public int numRows = 6;
	public int numCols = 6;
        public int numberOfPlayers = 2;
        public long aiWait = 500;
        public boolean evilActionsRemoveHistory = true;
        
        public GameOptions() {}
        
        private DiscreteDistribution createDefaultDistribution()
	{
        	DiscreteDistribution distribution = new DiscreteDistribution();
        	distribution.add(1, 1);
        	distribution.add(2, .5);
        	return distribution;
	}

	public GameOptions(GameOptions options)
        {
            this.numberOfNewTilesPerTurn = options.numberOfNewTilesPerTurn;
            this.startingTiles = options.startingTiles;
            this.skipIsAnOption = options.skipIsAnOption;
            this.newTileDistribution = new DiscreteDistribution(options.newTileDistribution);
            this.numCols = options.numCols;
            this.numRows = options.numRows;
            this.numberOfPlayers = options.numberOfPlayers;
            this.evilActionsRemoveHistory = options.evilActionsRemoveHistory;
            this.aiWait = options.aiWait;
        }

	public PointsCounter createPoints()
	{
		return new PointsCounter();
	}
	public EvilActionsAwarder createAwarder()
	{
		return new EvilActionsAwarder();
	}
        
        public GameOptions(JsonParser parser) throws IOException
        {
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");

			String currentName = parser.getCurrentName();
			switch (parser.nextToken())
			{
			case VALUE_FALSE:
				switch (currentName)
				{
				case "skip":
					skipIsAnOption = false;
					break;
				case "evilActionsRemoveHistory":
					evilActionsRemoveHistory = false;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_TRUE:
				switch (currentName)
				{
				case "skip":
					skipIsAnOption = true;
					break;
				case "evilActionsRemoveHistory":
					evilActionsRemoveHistory = true;
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case VALUE_NUMBER_INT:
				switch (currentName)
				{
				case "newTilesPerTurn":
					numberOfNewTilesPerTurn = parser.getNumberValue().intValue();
					break;
				case "numberStartingTiles":
					startingTiles = parser.getNumberValue().intValue();
					break;
				case "nrows":
					numRows = parser.getNumberValue().intValue();
					break;
				case "ncols":
					numCols = parser.getNumberValue().intValue();
					break;
				case "nplayers":
					numberOfPlayers = parser.getNumberValue().intValue();
					break;
				case "aiwait":
					aiWait = parser.getNumberValue().longValue();
					break;
				}
				break;
			case START_OBJECT:
				switch (currentName)
				{
				case "distribution":
					newTileDistribution = new DiscreteDistribution(parser);
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
        
        public void print(JsonGenerator generator) throws IOException
        {
        	generator.writeStartObject();
        	generator.writeBooleanField("skip", skipIsAnOption);
        	generator.writeNumberField("newTilesPerTurn", numberOfNewTilesPerTurn);
        	generator.writeNumberField("numberStartingTiles", startingTiles);
        	generator.writeNumberField("nrows", numRows);
        	generator.writeBooleanField("evilActionsRemoveHistory", evilActionsRemoveHistory);
        	generator.writeNumberField("ncols", numCols);
        	generator.writeNumberField("nplayers", numberOfPlayers);
        	generator.writeNumberField("aiwait", aiWait);
        	generator.writeFieldName("distribution");
        	newTileDistribution.write(generator);
        	generator.writeEndObject();
        }
}
