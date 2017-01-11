package org.hallock.tfe.ai;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.sys.GameConstants;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public interface GameWriterIf extends Jsonable
{
	public void addTurn(Turn t);
	void playTurn(Turn t, int turnNumber) throws IOException;
	public void save(boolean force) throws IOException;
	public void close() throws IOException;
	
	public static final GameWriterIf NULL_WRITER = new GameWriterIf()
	{
		@Override
		public void write(JsonGenerator generator) throws IOException
		{
			
		}

		@Override
		public void addTurn(Turn t)
		{
			
		}

		@Override
		public void save(boolean force) throws IOException
		{
			
		}

		@Override
		public void playTurn(Turn t, int turnNumber)
		{
			
		}

		@Override
		public void close()
		{
			
		}
	};
	
	
	
	
	public class MemorylessGameWriter implements GameWriterIf
	{
		JsonGenerator generator;
		
		public MemorylessGameWriter(JsonGenerator generator)
		{
			this.generator = generator;
		}

		public void start(String algorithmName, int nrows, int ncols) throws IOException
		{
			generator.writeStartObject();
			generator.writeStringField("algorithm", algorithmName);
			generator.writeNumberField("time", System.currentTimeMillis());
			generator.writeNumberField("nrows", nrows);
			generator.writeNumberField("ncols", ncols);

			generator.writeFieldName("boards");
			generator.writeStartArray();
		}
		
		public void finish() throws IOException
		{
			generator.writeEndArray();
			generator.writeEndObject();
		}
		
		@Override
		public void playTurn(Turn t, int turnNumber) throws IOException
		{
			t.index = turnNumber;
			t.write(generator);
		}

		@Override
		public void save(boolean force) throws IOException {}
		@Override
		public void write(JsonGenerator generator) throws IOException {}
		@Override
		public void addTurn(Turn t) {}

		@Override
		public void close() throws IOException
		{
			finish();
		}
	}
	
	


	
	public class FileGameWriter implements GameWriterIf
	{
		String filename;
		String algorithmName;
		long time;
		ArrayList<Turn> boards = new ArrayList<>();
		int nrows = -1;
		int ncols = -1;

		boolean changed;

		public FileGameWriter(
				String name, 
				long time, 
				String filename)
		{
			this.algorithmName = name;
			this.time = time;
			this.filename = filename;
		}
		public FileGameWriter(
				String name, 
				long time, 
				String filename,
				int nrows,
				int ncols)
		{
			this.algorithmName = name;
			this.time = time;
			this.filename = filename;
			this.nrows = nrows;
			this.ncols = ncols;
		}

		public FileGameWriter(JsonParser parser, String filename) throws FileNotFoundException, IOException
		{
			this.filename = filename;
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
					case "algorithm":
						this.algorithmName = parser.getValueAsString();
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case VALUE_NUMBER_INT:
					switch (currentName)
					{
					case "time":
						this.time = parser.getLongValue();
						break;
					case "nrows":
						this.nrows = parser.getIntValue();
						break;
					case "ncols":
						this.ncols = parser.getIntValue();
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case START_ARRAY:
					switch (currentName)
					{
					case "boards":
						while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
						{
							switch (next)
							{
							case START_OBJECT:
								addTurn(new Turn(parser));
								break;
							case VALUE_NULL:
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
		public void playTurn(Turn t, int turnNumber) throws IOException
		{
			t.index = turnNumber;
			addTurn(t);
			save(false);
		}

		@Override
		public void addTurn(Turn t)
		{
			boards.ensureCapacity(t.index + 1);
			while (boards.size() < t.index + 1)
				boards.add(null);
			if (boards.get(t.index) != null && boards.get(t.index).equals(t))
				return;
			boards.set(t.index, t);
			changed = true;
		}

		@Override
		public void save(boolean force) throws IOException
		{
			if (!force && !changed)
				return;
			try (JsonGenerator generator = Json.createUnopenedGenerator(Files.newOutputStream(Paths.get(filename)));)
			{
				write(generator);
			}
		}

		@Override
		public void write(JsonGenerator generator) throws IOException
		{
			generator.writeStartObject();
			generator.writeStringField("algorithm", algorithmName);
			generator.writeNumberField("time", time);
			generator.writeNumberField("nrows", nrows);
			generator.writeNumberField("ncols", ncols);

			generator.writeFieldName("boards");
			generator.writeStartArray();
			for (int i = 0; i < boards.size(); i++)
				if (boards.get(i) == null)
					generator.writeNull();
				else
					boards.get(i).write(generator);
			generator.writeEndArray();

			generator.writeEndObject();
		}
		@Override
		public void close() throws IOException
		{
			save(true);
		}
	}
	
	
	
	public static class Turn implements Jsonable
	{
		int index;
		PossiblePlayerActions action;
		TileBoard from;
		TileBoard to;
		int searchDepth = -1;

		public Turn(PossiblePlayerActions action, TileBoard oldBoard, TileBoard newBoard) throws IOException
		{
			this.action = action;
			this.from = oldBoard;
			this.to = newBoard;
		}

//		public Turn(Path p, int index) throws IOException
//		{
//			int rows = 4; int cols = 4;
//			this.index = index;
//			try (BufferedReader newBufferedReader = Files.newBufferedReader(p);)
//			{
//				String line;
//				while ((line = newBufferedReader.readLine()) != null)
//				{
//					if (line.startsWith("Play"))
//					{
//						action = PossiblePlayerActions.valueOf(line.substring("Play ".length(), line.length()));
//					}
//					else if (line.contains("From:"))
//					{
//						if (!newBufferedReader.readLine().contains("tileboard:"))
//							throw new RuntimeException();
//						
//						TileBoard board = new TileBoard(rows, cols);
//						for (int i = 0; i < rows; i++)
//						{
//							line = newBufferedReader.readLine();
//							try (Scanner scanner = new Scanner(line);)
//							{
//								for (int j = 0; j < cols; j++)
//								{
//									board.tiles[i][j] = scanner.nextInt();
//								}
//							}
//						}
//						from = board;
//					}
//					else if (line.contains("Actually:"))
//					{
//						if (!newBufferedReader.readLine().contains("tileboard:"))
//							throw new RuntimeException();
//						
//						TileBoard board = new TileBoard(rows, cols);
//						for (int i = 0; i < rows; i++)
//						{
//							line = newBufferedReader.readLine();
//							try (Scanner scanner = new Scanner(line);)
//							{
//								for (int j = 0; j < cols; j++)
//								{
//									board.tiles[i][j] = scanner.nextInt();
//								}
//							}
//						}
//						to = board;
//					}
//				}
//			}
//			
//			if (action == null || from == null || to == null)
//			{
//				throw new RuntimeException(String.valueOf(p));
//			}
//		}
		
		public Turn(JsonParser parser) throws IOException
		{
			JsonToken next;
			while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
			{
				if (!next.equals(JsonToken.FIELD_NAME))
					throw new RuntimeException("Unexpected.");

				String currentName = parser.getCurrentName();
				switch (next = parser.nextToken())
				{
				case VALUE_NUMBER_INT:
					switch (currentName)
					{
					case "depth":
						this.searchDepth = parser.getIntValue();
						break;
					case "index":
						this.index = parser.getIntValue();
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case VALUE_STRING:
					switch (currentName)
					{
					case "action":
						this.action = PossiblePlayerActions.valueOf(parser.getValueAsString());
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case START_OBJECT:
					switch (currentName)
					{
					case "from":
						from = new TileBoard(parser);
						break;
					case "to":
						to = new TileBoard(parser);
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				default:
					throw new RuntimeException("Unexpected: " + next);
				}
			}
		}
		
		public boolean finishesGame()
		{
			return !from.hasMoreMoves();
		}
		
		@Override
		public boolean equals(Object other)
		{
			if (!(other instanceof Turn))
				return false;
			Turn o = (Turn) other;
			
			if (index != o.index)
				return false;
			if (!action.equals(o.action))
				return false;
			if (!from.equals(o.from))
				return false;
			if (!to.equals(o.to))
				return false;
			if (searchDepth != o.searchDepth)
				return false;
			return true;
		}

		@Override
		public void write(JsonGenerator generator) throws IOException
		{
			generator.writeStartObject();
			generator.writeNumberField("depth", searchDepth);
			generator.writeNumberField("index", index);
			generator.writeStringField("action", action.name());
			
			generator.writeFieldName("from");
			from.write(generator);
			generator.writeFieldName("to");
			to.write(generator);
			
			
			generator.writeEndObject();
		}
	}

	public static GameWriterIf readWriter(String filename) throws JsonParseException, IOException
	{
		File file = new File(filename);
		if (!file.exists())
		{
			return null;
		}
		try (JsonParser parser = Json.createParser(Files.newInputStream(Paths.get(file.toString())));)
		{
			if (!parser.nextToken().equals(JsonToken.START_OBJECT))
				throw new RuntimeException();
			return new FileGameWriter(parser, file.toString());
		}
	}

	public static GameWriterIf createHumanPlayerWriter(String name) throws IOException
	{
		long time = System.currentTimeMillis();
		File file = new File(GameConstants.GAME_LOG_LOCATION + time + ".json");
		if (!file.getParentFile().exists())
		{
			file.getParentFile().mkdirs();
		}
		GameWriterIf writer = new FileGameWriter("human player <" + name + ">", time, file.toString());
		writer.save(true);
		return writer;
	}
	public static GameWriterIf createAiWriter(AiOptions options) throws IOException
	{
		long time = System.currentTimeMillis();
		File file = new File(GameConstants.GAME_LOG_LOCATION + time + ".json");
		if (!file.getParentFile().exists())
		{
			file.getParentFile().mkdirs();
		}
		GameWriterIf writer = new FileGameWriter(options.type.name(), time, file.toString(), options.nrows, options.ncols);
		writer.save(true);
		return writer;
	}
}
