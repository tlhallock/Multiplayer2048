package org.hallock.tfe.cmn.game;

import java.awt.Point;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

import org.hallock.tfe.cmn.game.TileChanges.TileChange;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.cmn.util.Utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class TileBoard  implements Jsonable
{
	public int[][] tiles;
	public static final int EMPTY = 0;
	public static final int BLOCKED = -1;

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

//	public boolean isFinished()
//	{
//		return getPossibles().isEmpty();
//	}

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
		randomlyFill(num, options.newTileDistribution, changes);
	}
	public void randomlyFill(int num, DiscreteDistribution dist, TileChanges changes)
	{
		LinkedList<Point> possibles = getPossibles();
		Collections.shuffle(possibles, Constants.random);

		for (int i = 0; i < num; i++)
		{
			if (possibles.isEmpty())
				return;
			Point nextLoc = possibles.removeFirst();
			int added = dist.sample();
			tiles[nextLoc.x][nextLoc.y] = added;
			changes.add(new TileChange(added, nextLoc.x, nextLoc.y, true));
		}
	}

	public boolean addTile(int newTile, TileChanges changes)
	{
		LinkedList<Point> possibles = getPossibles();
		Collections.shuffle(possibles, Constants.random);
		if (possibles.isEmpty())
			return false;
		Point nextLoc = possibles.removeFirst();
		tiles[nextLoc.x][nextLoc.y] = newTile;
		changes.add(new TileChange(newTile, nextLoc.x, nextLoc.y, true));
		return true;
	}

	public LinkedList<Point> getPossibles()
	{
		LinkedList<Point> possibles = new LinkedList<>();
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[i].length; j++)
				if (tiles[i][j]  == EMPTY)
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

	public void addNoChanges(TileChanges changes)
	{
		for (int i=0;i<tiles.length;i++)
		{
			for (int j=0;j<tiles[i].length;j++)
			{
				if (tiles[i][j] == EMPTY)
					continue;
				changes.add(new TileChange(
						tiles[i][j],
						i, j,
						false));
			}
		}
	}
	
	
	
	
	
	
	
	
	
	

	private static boolean canCombine(int first, int second)
	{
		return first == second && first != BLOCKED && first != EMPTY;
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
		tiles[r][from] = EMPTY;
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
		tiles[r][from2] = EMPTY;
		if (to == from1)
			return;
		tiles[r][to] = tiles[r][from1];
		tiles[r][from1] = EMPTY;
	}

	public TileChanges left(TileChanges changes)
	{
		for (int r = 0; r < tiles.length; r++)
		{
			int next = 0;
			for (int first = 0; first < tiles[r].length; first++)
			{
				if (tiles[r][first] == EMPTY)
					continue;
				int second;
				for (second = first + 1; second < tiles[r].length && tiles[r][second] == EMPTY; second++)
					;
				if (second >= tiles[r].length)
				{
					pushRow(r, next++, first, changes);
					break;
				}
				if (canCombine(tiles[r][second], tiles[r][first]))
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
				if (tiles[r][first] == EMPTY)
					continue;
				int second;
				for (second = first - 1; second >= 0 && tiles[r][second] == EMPTY; second--)
					;
				if (second < 0)
				{
					pushRow(r, next--, first, changes);
					break;
				}
				if (canCombine(tiles[r][second], tiles[r][first]))
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
		tiles[from][c] = EMPTY;
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
		tiles[from2][c] = EMPTY;
		if (to == from1)
			return;
		tiles[to][c] = tiles[from1][c];
		tiles[from1][c] = EMPTY;
	}
	
	public TileChanges up(TileChanges changes)
	{
		for (int c = 0; c < tiles[0].length; c++)
		{
			int next = 0;
			for (int first = 0; first < tiles.length; first++)
			{
				if (tiles[first][c] == EMPTY)
					continue;
				int second;
				for (second = first + 1; second < tiles.length && tiles[second][c] == EMPTY; second++)
					;
				if (second >= tiles.length)
				{
					pushCol(c, next++, first, changes);
					break;
				}
				if (canCombine(tiles[second][c], tiles[first][c]))
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
				if (tiles[first][c] == EMPTY)
					continue;
				int second;
				for (second = first - 1; second >= 0 && tiles[second][c] == EMPTY; second--)
					;
				if (second < 0)
				{
					pushCol(c, next--, first, changes);
					break;
				}
				if (canCombine(tiles[second][c], tiles[first][c]))
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
				tiles[i][j] = EMPTY;
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

	public boolean hasMoreMoves()
	{
		TileBoard board = new TileBoard(this);
		if (board.up(new TileChanges()).changed())
			return true;
		if (board.down(new TileChanges()).changed())
			return true;
		if (board.left(new TileChanges()).changed())
			return true;
		if (board.right(new TileChanges()).changed())
			return true;
		return false;
	}

	public int getHighestTile()
	{
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[i].length; j++)
			{
				max = Math.max(max, tiles[i][j]);
			}
		}
		return max;
	}

	public int getNumCells()
	{
		return tiles.length * tiles[0].length;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof TileBoard))
			return false;
		TileBoard o = (TileBoard) other;
		if (tiles.length != o.tiles.length || tiles[0].length != o.tiles[0].length)
			return false;
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[i].length; j++)
				if (tiles[i][j] != o.tiles[i][j])
					return false;
		return true;
	}
	@Override
	public int hashCode()
	{
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[i].length; j++)
				builder.append(tiles[i][j]).append(j == tiles[i].length - 1 ? ';' : ',');
		}

		return builder.toString().hashCode();
	}

	public void print(PrintStream ps, int depth)
	{
		Utils.indent(ps, depth);
		ps.print("tileboard:");
		ps.print('\n');
		for (int i = 0; i < tiles.length; i++)
		{
			Utils.indent(ps, depth);
			for (int j = 0; j < tiles[i].length; j++)
			{
				ps.append(Utils.ensureLength(Utils.display(tiles[i][j]), ' ', Constants.DISPLAY_WIDTH)).append(' ');
			}
			ps.append('\n');
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean isTopHeavy()
	{
		int lower = 0;
		int upper = 0;
		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[0].length; j++)
			{
				if (i > tiles.length / 2)
				{
					upper += tiles[i][j];
				}
				else
				{
					lower += tiles[i][j];
				}
			}
		}
		return upper > lower;
	}
	public boolean isRightHeavy()
	{
		int left = 0;
		int right = 0;
		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[i].length; j++)
			{
				if (j > tiles[i].length / 2)
				{
					right += tiles[i][j];
				}
				else
				{
					left += tiles[i][j];
				}
			}
		}
		return right > left;
	}
	public void reflectVertically()
	{
		for (int c = 0; c < tiles[0].length; c++)
		{
			for (int rL = 0, rU = tiles.length-1; rL < rU; rL++, rU--)
			{
				int tmp = tiles[rL][c];
				tiles[rL][c] = tiles[rU][c];
				tiles[rU][c] = tmp;
			}
		}
	}
	public void reflectHorizontally()
	{
		for (int r = 0; r < tiles.length; r++)
		{
			for (int cL = 0, cU = tiles[r].length-1; cL < cU; cL++, cU--)
			{
				int tmp = tiles[r][cL];
				tiles[r][cL] = tiles[r][cU];
				tiles[r][cU] = tmp;
			}
		}
	}
}
