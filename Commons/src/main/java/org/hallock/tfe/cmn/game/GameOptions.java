package org.hallock.tfe.cmn.game;

import java.io.IOException;

import org.hallock.tfe.ai.AiOptions;
import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.msg.SimpleParser.ObjectReader;
import org.hallock.tfe.serve.EvilActionsAwarder;
import org.hallock.tfe.sys.PointsCounter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class GameOptions implements Jsonable
{
	public static final ObjectReader<GameOptions> READER = new ObjectReader<GameOptions>() {
		@Override
		public GameOptions parse(JsonParser parser) throws IOException
		{
			return new GameOptions(parser);
		}
	};
	
	
	public int numberOfNewTilesPerTurn;
	public int startingTiles;
	public boolean skipIsAnOption;
	public DiscreteDistribution newTileDistribution;
	public int numRows;
	public int numCols;
        public int numberOfPlayers;
        public boolean evilActionsRemoveHistory;
        
        public long aiWait;
        public AiOptions aiOptions;
        
        public GameOptions()
        {
        	numberOfNewTilesPerTurn = 1;
        	startingTiles = 3;
        	skipIsAnOption = false;
        	newTileDistribution = createDefaultDistribution();
        	numRows = 6;
        	numCols = 6;
                numberOfPlayers = 2;
                evilActionsRemoveHistory = true;
                aiWait = 1000;
                aiOptions = new AiOptions(numRows, numCols);
        }

	public GameOptions(GameOptions options)
	{
		this();
		this.numberOfNewTilesPerTurn = options.numberOfNewTilesPerTurn;
		this.startingTiles = options.startingTiles;
		this.skipIsAnOption = options.skipIsAnOption;
		this.newTileDistribution = new DiscreteDistribution(options.newTileDistribution);
		this.numCols = options.numCols;
		this.numRows = options.numRows;
		this.numberOfPlayers = options.numberOfPlayers;
		this.evilActionsRemoveHistory = options.evilActionsRemoveHistory;
		this.aiWait = options.aiWait;
		this.aiOptions = options.aiOptions;
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
				case "ai_options":
					aiOptions = new AiOptions(parser);
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

	public PointsCounter createPoints()
	{
		return new PointsCounter();
	}
	public EvilActionsAwarder createAwarder()
	{
		return new EvilActionsAwarder();
	}
        
        private DiscreteDistribution createDefaultDistribution()
	{
        	DiscreteDistribution distribution = new DiscreteDistribution();
        	distribution.add(1, 1);
        	distribution.add(2, .5);
        	return distribution;
	}
        
        @Override
	public void write(JsonGenerator generator) throws IOException
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
        	generator.writeFieldName("ai_options");
        	aiOptions.write(generator);
        	generator.writeEndObject();
        }
}
