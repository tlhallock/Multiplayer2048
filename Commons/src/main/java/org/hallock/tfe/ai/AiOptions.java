package org.hallock.tfe.ai;

import java.io.IOException;
import java.math.BigDecimal;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.msg.SimpleParser;
import org.hallock.tfe.msg.SimpleParser.KnownValueReader;
import org.hallock.tfe.msg.SimpleParser.SimpleKnownValue;
import org.hallock.tfe.serve.ai.ComputerAiStrategy;
import org.hallock.tfe.serve.ai.RandomStrategy;
import org.hallock.tfe.serve.ai.SearchStrategy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class AiOptions implements Jsonable
{
	public int numLooksAhead;
	public int numLooksAcross;
	public int nrows;
	public int ncols;
	public ComputerAiType type;
//	public long AI_WAIT;
	
	public enum ComputerAiType
	{
		Random,
		RealV,
		Diags,
		Rows,
		RandomSearch,
		HalfSnake,
		Snake,
		Sunny, FakeV,
	}

	public AiOptions(int nrows, int ncols)
	{
		this.nrows = nrows;
		this.ncols = ncols;
		numLooksAhead = 20;
		numLooksAcross = 50;
		type = ComputerAiType.Random;
	}
	
	public AiOptions(JsonParser parser) throws IOException
	{
		KnownValueReader handler = new KnownValueReader(true);
		
		SimpleKnownValue<BigDecimal> depth   = handler.listenForNumber("/depth");
		SimpleKnownValue<BigDecimal> breadth = handler.listenForNumber("/breadth");
		SimpleKnownValue<BigDecimal> nrows   = handler.listenForNumber("/nrows");
		SimpleKnownValue<BigDecimal> ncols   = handler.listenForNumber("/ncols");
//		SimpleKnownValue<BigDecimal> wait    = handler.listenForNumber("/wait");
		SimpleKnownValue<String>     type    = handler.listenForString("/type");
                                                     
		SimpleParser.parseAllOfCurrentObject(handler, parser);

		this.numLooksAhead  = depth   .getValue().intValue();
		this.numLooksAcross = breadth .getValue().intValue();
		this.nrows          = nrows   .getValue().intValue();
		this.ncols          = ncols   .getValue().intValue();
//		this.AI_WAIT        = wait    .getValue().longValue();
		this.type = ComputerAiType.valueOf(type.getValue());
	}

	public GameWriterIf createLog() throws IOException
	{
		return GameWriterIf.createAiWriter(this);
	}

	@Override
	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeNumberField("depth", numLooksAhead);
		generator.writeNumberField("breadth", numLooksAcross);
		generator.writeNumberField("nrows", nrows);
		generator.writeNumberField("ncols", ncols);
//		generator.writeNumberField("wait", AI_WAIT);
		generator.writeStringField("type", type.name());
		generator.writeEndObject();
	}
	
	
	
	public static ComputerAiStrategy createComputerAiStrategy(AiOptions options, int playerNumber, GameOptions gOptions)
	{
		LocationalHeuristic h;
		Search<?> search;
		switch (options.type)
		{
		case RandomSearch:
			h = new LocationalHeuristic(options.nrows, options.ncols);
			
		case Diags:
			if (options.nrows != options.ncols)
				throw new RuntimeException();
			h = LocationalHeuristic.createDiags(options.nrows);
			search = new Search<>(h, options.numLooksAhead, options.numLooksAcross, gOptions);
			return new SearchStrategy(search);
		case FakeV:
			if (options.nrows != options.ncols)
				throw new RuntimeException();
			h = LocationalHeuristic.createV(options.nrows);
			search = new Search<>(h, options.numLooksAhead, options.numLooksAcross, gOptions);
			return new SearchStrategy(search);
		case RealV:
			if (options.nrows != options.ncols)
				throw new RuntimeException();
			h = LocationalHeuristic.createRealV(options.nrows);
			search = new Search<>(h, options.numLooksAhead, options.numLooksAcross, gOptions);
			return new SearchStrategy(search);
		case Rows:
			if (options.nrows != options.ncols)
				throw new RuntimeException();
			h = LocationalHeuristic.createRows(options.nrows);
			search = new Search<>(h, options.numLooksAhead, options.numLooksAcross, gOptions);
			return new SearchStrategy(search);
		case Snake:
			if (options.nrows != options.ncols)
				throw new RuntimeException();
			h = LocationalHeuristic.createSnake(options.nrows);
			search = new Search<>(h, options.numLooksAhead, options.numLooksAcross, gOptions);
			return new SearchStrategy(search);
		case HalfSnake:
			if (options.nrows != options.ncols && options.nrows != 4)
				throw new RuntimeException();
			h = LocationalHeuristic.createHalfSnake();
			search = new Search<>(h, options.numLooksAhead, options.numLooksAcross, gOptions);
			return new SearchStrategy(search);
		case Sunny:
			if (options.nrows != options.ncols && options.nrows != 4)
				throw new RuntimeException();
			h = LocationalHeuristic.createSunnys();
			search = new Search<>(h, options.numLooksAhead, options.numLooksAcross, gOptions);
			return new SearchStrategy(search);
		case Random:
			return new RandomStrategy(playerNumber);
		default:
			throw new RuntimeException();
		}
	}
}
