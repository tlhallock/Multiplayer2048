package org.hallock.tfe.cmn.game;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

import org.hallock.tfe.cmn.game.TileChanges.TileChange;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.cmn.util.Utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class TileBoard  implements Jsonable
{
	int[][] tiles;

	public TileBoard(int nrows, int ncols)
	{
		if (nrows < 1 || ncols < 1)
			throw new RuntimeException("Too few rows or columns.");
		tiles = new int[nrows][ncols];
	}

	public TileBoard(TileBoard state)
	{
		this(state.tiles.length, state.tiles[0].length);
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[i].length; j++)
				tiles[i][j] = state.tiles[i][j];
	}

	public TileBoard(JsonParser parser) throws IOException
	{
		int rows = -1;
		int cols = -1;

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
				case "cols":
					rows = parser.getNumberValue().intValue();
					break;
				case "rows":
					cols = parser.getNumberValue().intValue();
					break;
				default:
					throw new RuntimeException("Unexpected.");
				}
				break;
			case START_ARRAY:
				switch (currentName)
				{
				case "vals":
					tiles = new int[rows][cols];
					tiles = new int[rows][cols];
					for (int i = 0; i < rows; i++)
					{
						for (int j = 0; j < cols; j++)
						{
							if (!parser.nextToken().equals(JsonToken.VALUE_NUMBER_INT))
								throw new RuntimeException("Unexpected.");
							tiles[i][j] = parser.getNumberValue().intValue();
						}
					}
					if (!parser.nextToken().equals(JsonToken.END_ARRAY))
						throw new RuntimeException("Unexpected.");
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

	@Override
	public void write(JsonGenerator writer) throws IOException
	{
		writer.writeStartObject();

		writer.writeNumberField("rows", tiles.length);
		writer.writeNumberField("cols", tiles[0].length);

		writer.writeFieldName("vals");
		writer.writeStartArray();

		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[0].length; j++)
			{
				writer.writeNumber(tiles[i][j]);
			}
		}

		writer.writeEndArray();
		writer.writeEndObject();
	}

	public boolean isFinished()
	{
		return getPossibles().isEmpty();
	}

	public void initialize(GameOptions options)
	{
		randomlyFill(options, new TileChanges(), options.startingTiles);
	}

	public void fillTurn(GameOptions options, TileChanges changes)
	{
		randomlyFill(options, changes, options.numberOfNewTilesPerTurn);
	}
	private void randomlyFill(GameOptions options, TileChanges changes, int num)
	{
		LinkedList<Point> possibles = getPossibles();
		Collections.shuffle(possibles, Constants.random);

		for (int i = 0; i < num; i++)
		{
			if (possibles.isEmpty())
				return;
			Point nextLoc = possibles.removeFirst();
			int added = options.newTileDistribution.sample();
			tiles[nextLoc.x][nextLoc.y] = added;
			changes.add(new TileChange(added, nextLoc.x, nextLoc.y, true));
		}
	}

	private LinkedList<Point> getPossibles()
	{
		LinkedList<Point> possibles = new LinkedList<>();
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[i].length; j++)
				if (tiles[i][j] <= 0)
					possibles.add(new Point(i, j));
		return possibles;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(tiles.length * (1 + (Constants.DISPLAY_WIDTH + 1) * tiles[0].length));

		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[i].length; j++)
			{
				builder.append(Utils.ensureLength(Utils.display(tiles[i][j]), ' ', Constants.DISPLAY_WIDTH)).append(' ');
			}
			builder.append('\n');
		}

		return builder.toString();
	}

	
	
	
	
	
	
	
	
	
	
	


	private void increment(int r, int first)
	{
		tiles[r][first]++;
	}
	private void pushRow(int r, int to, int from, TileChanges changes)
	{
		if (to == from)
		{
			changes.add(new TileChange(tiles[r][from], r, from, false));
			return;
		}
		changes.add(new TileChange(
				tiles[r][from],
				r, from,
				r, to));
		tiles[r][to] = tiles[r][from];
		tiles[r][from] = 0;
	}
	private void pushRow(int r, int to, int from1, int from2, TileChanges changes)
	{
		int old = tiles[r][from1];
		increment(r, from1);
		changes.add(new TileChange(
				old,
				r, from1,
				r, from2,
				tiles[r][from1],
				r, to));
		tiles[r][from2] = 0;
		if (to == from1)
			return;
		tiles[r][to] = tiles[r][from1];
		tiles[r][from1] = 0;
	}

	public TileChanges left(TileChanges changes)
	{
		for (int r = 0; r < tiles.length; r++)
		{
			int next = 0;
			for (int first = 0; first < tiles[r].length; first++)
			{
				if (tiles[r][first] <= 0)
					continue;
				int second;
				for (second = first + 1; second < tiles[r].length && tiles[r][second] <= 0; second++)
					;
				if (second >= tiles[r].length)
				{
					pushRow(r, next++, first, changes);
					break;
				}
				if (tiles[r][second] == tiles[r][first])
				{
					pushRow(r, next++, first, second, changes);
					first = second;
				}
				else
				{
					pushRow(r, next++, first, changes);
					first = second - 1;
				}
			}
		}
		return changes;
	}

	public TileChanges right(TileChanges changes)
	{
		for (int r = tiles.length - 1; r >= 0; r--)
		{
			int next = tiles[r].length - 1;
			for (int first = tiles[r].length - 1; first >= 0; first--)
			{
				if (tiles[r][first] <= 0)
					continue;
				int second;
				for (second = first - 1; second >= 0 && tiles[r][second] <= 0; second--)
					;
				if (second < 0)
				{
					pushRow(r, next--, first, changes);
					break;
				}
				if (tiles[r][second] == tiles[r][first])
				{
					pushRow(r, next--, first, second, changes);
					first = second;
				}
				else
				{
					pushRow(r, next--, first, changes);
					first = second + 1;
				}
			}
		}
		return changes;
	}

	
	
	

	private void pushCol(int c, int to, int from, TileChanges changes)
	{
		if (to == from)
		{
			changes.add(new TileChange(tiles[from][c], from, c, false));
			return;
		}
		changes.add(new TileChange(
				tiles[from][c],
				from, c,
				to, c));
		tiles[to][c] = tiles[from][c];
		tiles[from][c] = 0;
	}
	private void pushCol(int c, int to, int from1, int from2, TileChanges changes)
	{
		int old = tiles[from1][c];
		increment(from1, c);
		changes.add(new TileChange(
				old,
				from1, c,
				from2, c,
				tiles[from1][c],
				to, c));
		tiles[from2][c] = 0;
		if (to == from1)
			return;
		tiles[to][c] = tiles[from1][c];
		tiles[from1][c] = 0;
	}
	
	public TileChanges up(TileChanges changes)
	{
		for (int c = 0; c < tiles[0].length; c++)
		{
			int next = 0;
			for (int first = 0; first < tiles.length; first++)
			{
				if (tiles[first][c] <= 0)
					continue;
				int second;
				for (second = first + 1; second < tiles.length && tiles[second][c] <= 0; second++)
					;
				if (second >= tiles.length)
				{
					pushCol(c, next++, first, changes);
					break;
				}
				if (tiles[second][c] == tiles[first][c])
				{
					pushCol(c, next++, first, second, changes);
					first = second;
				}
				else
				{
					pushCol(c, next++, first, changes);
					first = second - 1;
				}
			}
		}
		return changes;
	}

	public TileChanges down(TileChanges changes)
	{
		for (int c = tiles[0].length - 1; c >= 0; c--)
		{
			int next = tiles.length - 1;
			for (int first = tiles.length - 1; first >= 0; first--)
			{
				if (tiles[first][c] <= 0)
					continue;
				int second;
				for (second = first - 1; second >= 0 && tiles[second][c] <= 0; second--)
					;
				if (second < 0)
				{
					pushCol(c, next--, first, changes);
					break;
				}
				if (tiles[second][c] == tiles[first][c])
				{
					pushCol(c, next--, first, second, changes);
					first = second;
				}
				else
				{
					pushCol(c, next--, first, changes);
					first = second + 1;
				}
			}
		}
		return changes;
	}

	public void zero()
	{
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[i].length; j++)
				tiles[i][j] = 0;
	}

	public void load(String filename) throws IOException
	{
		try (Scanner scanner = new Scanner(Files.newInputStream(Paths.get(filename)));)
		{
			int rows = scanner.nextInt();
			int cols = scanner.nextInt();

			tiles = new int[rows][cols];
			for (int i = 0; i < tiles.length; i++)
			{
				for (int j = 0; j < tiles[i].length; j++)
				{
					tiles[i][j] = scanner.nextInt();
				}
			}
		}
	}

	public int get(int r, int c)
	{
		return tiles[r][c];
	}

	public int getNRows()
	{
		return tiles.length;
	}

	public int getNCols()
	{
		return tiles[0].length;
	}
}
