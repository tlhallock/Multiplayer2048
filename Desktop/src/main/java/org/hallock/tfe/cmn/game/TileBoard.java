package org.hallock.tfe.cmn.game;

import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.util.Utils;

public class TileBoard
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

	public TileBoard(Scanner scanner)
	{
		int rows = scanner.nextInt();
		int cols = scanner.nextInt();
		tiles = new int[rows][cols];
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				tiles[i][j] = scanner.nextInt();
			}
		}
	}

	public void print(PrintWriter writer)
	{
		writer.print(tiles.length + " ");
		writer.print(tiles[0].length + " ");
		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[0].length; j++)
			{
				writer.print(tiles[i][j] + " ");
			}
		}
	}

	public boolean isFinished()
	{
		return getPossibles().isEmpty();
	}

	public boolean randomlyFill(int numToFill)
	{
		LinkedList<Point> possibles = getPossibles();
		if (possibles.size() < numToFill)
		{
			return false;
		}

		Collections.shuffle(possibles, Constants.random);

		while (numToFill-- > 0)
		{
			Point removeFirst = possibles.removeFirst();
			tiles[removeFirst.x][removeFirst.y] = 2;
		}
		return true;
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

	private boolean pushRow(int r, int to, int from)
	{
		if (to == from)
			return false;
		tiles[r][to] = tiles[r][from];
		tiles[r][from] = 0;
		return true;
	}

	public boolean left()
	{
		boolean modified = false;
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
					modified |= pushRow(r, next++, first);
					break;
				}
				if (tiles[r][second] == tiles[r][first])
				{
					increment(r, first);
					tiles[r][second] = 0;
					pushRow(r, next++, first);
					first = second;
					modified = true;
				}
				else
				{
					modified |= pushRow(r, next++, first);
					first = second - 1;
				}
			}
		}
		return modified;
	}

	private void increment(int r, int first)
	{
		tiles[r][first]++;
	}

	public boolean right()
	{
		boolean modified = false;
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
					modified |= pushRow(r, next--, first);
					break;
				}
				if (tiles[r][second] == tiles[r][first])
				{
					increment(r, first);
					tiles[r][second] = 0;
					pushRow(r, next--, first);
					first = second;
					modified = true;
				}
				else
				{
					modified |= pushRow(r, next--, first);
					first = second + 1;
				}
			}
		}
		return modified;
	}

	private boolean pushCol(int c, int to, int from)
	{
		if (to == from)
			return false;
		tiles[to][c] = tiles[from][c];
		tiles[from][c] = 0;
		return true;
	}

	public boolean up()
	{
		if (tiles.length == 0)
			return false;
		boolean modified = false;
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
					modified |= pushCol(c, next++, first);
					break;
				}
				if (tiles[second][c] == tiles[first][c])
				{
					increment(first, c);
					tiles[second][c] = 0;
					pushCol(c, next++, first);
					first = second;
					modified = true;
				}
				else
				{
					modified |= pushCol(c, next++, first);
					first = second - 1;
				}
			}
		}
		return modified;
	}

	public boolean down()
	{
		if (tiles.length == 0)
			return false;
		boolean modified = false;
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
					modified |= pushCol(c, next--, first);
					break;
				}
				if (tiles[second][c] == tiles[first][c])
				{
					increment(first, c);
					tiles[second][c] = 0;
					pushCol(c, next--, first);
					first = second;
					modified = true;
				}
				else
				{
					modified |= pushCol(c, next--, first);
					first = second + 1;
				}
			}
		}
		return modified;
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
