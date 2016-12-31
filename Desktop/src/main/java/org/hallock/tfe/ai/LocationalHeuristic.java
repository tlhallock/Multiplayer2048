package org.hallock.tfe.ai;

import java.awt.Point;
import java.util.ArrayList;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.sys.Constants;

public class LocationalHeuristic implements Heuristic<SnakeSequence>
{
	Point[] order;
	
	public LocationalHeuristic(int nrow, int ncol)
	{
		order = new Point[nrow * ncol];
		for (int i = 0; i < nrow; i++)
			for (int j = 0; j < ncol; j++)
				order[ncol * i + j] = new Point(i, j);
		randomize();
	}
	
	public LocationalHeuristic(Point[] array)
	{
		this.order = array;
	}
	
	


	@Override
	public SnakeSequence assess(int depth, TileBoard board)
	{
		int[] ordered = new int[order.length];
		for (int i = 0; i < order.length; i++)
			ordered[i] = board.tiles[order[i].x][order[i].y];
		return new SnakeSequence(ordered);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void randomize()
	{
		for (int i = 0; i < 2 * order.length; i++)
		{
			int idx1 = Constants.random.nextInt(order.length);
			int idx2 = Constants.random.nextInt(order.length);
			
			Point tmp = order[idx1];
			order[idx1] = order[idx2];
			order[idx2] = tmp;
		}
	}
	
	/*
	 (0,0) (0,1) (0,2) (0,3)
	 (1,0) (1,1) (1,2) (1,3)
	 (2,0) (2,1) (2,2) (2,3)
	 (3,0) (3,1) (3,2) (3,3) 
	 */
	public static LocationalHeuristic createV(int size)
	{
		ArrayList<Point> points = new ArrayList<>();
		for (int i = size - 1; i >= 0; i--)
		{
			for (int j=i; j>=0;j--)
			{
				points.add(new Point(i, j));
			}
			for (int j=i-1; j>=0;j--)
			{
				points.add(new Point(j, i));
			}
			System.out.println();
		}
		return new LocationalHeuristic(points.toArray(new Point[0]));
	}
	
	public static LocationalHeuristic createDiags(int size)
	{
		ArrayList<Point> points = new ArrayList<>();
		for (int sum = 2 * (size - 1); sum >= 0; sum--)
		{
			for (int i = size - 1; i >= 0; i--)
			{
				int j = sum - i;
				if (0 <= j && j < size)
					points.add(new Point(i, j));
			}
		}
		return new LocationalHeuristic(points.toArray(new Point[0]));
	}
	public static LocationalHeuristic createRealV(int size)
	{
		ArrayList<Point> points = new ArrayList<>();
		for (int i = size - 1; i >= 0; i--)
		{
			for (int j = i; j >= 0; j--)
			{
				points.add(new Point(i, j));
				if (i != j)
					points.add(new Point(j, i));
			}
		}
		return new LocationalHeuristic(points.toArray(new Point[0]));
	}
	
	public static LocationalHeuristic createRows(int size)
	{
		ArrayList<Point> points = new ArrayList<>();
		for (int i=size-1;i>=0;i--)
			for (int j=size-1;j>=0;j--)
				points.add(new Point(i, j));
		return new LocationalHeuristic(points.toArray(new Point[0]));
	}
	
	public static LocationalHeuristic createSnake(int size)
	{
		ArrayList<Point> points = new ArrayList<>();
		for (int i = size - 1; i >= 0; i--)
		{
			for (int j = size - 1; j >= 0; j--)
				points.add(new Point(i, j));
			if (--i < 0)
				break;
			for (int j = 0; j < size; j++)
				points.add(new Point(i, j));
		}
		return new LocationalHeuristic(points.toArray(new Point[0]));
	}
	

	public static LocationalHeuristic createHalfSnake()
	{
		return new LocationalHeuristic(new Point[]
		{
			new Point(3, 3),
			new Point(3, 2),
			new Point(3, 1),
			new Point(3, 0),
			
			new Point(2, 3),
			new Point(2, 2),
			new Point(2, 1),
			new Point(2, 0),

			new Point(1, 0),
			new Point(1, 1),
			new Point(1, 2),
			new Point(1, 3),

			new Point(0, 3),
			new Point(0, 2),
			new Point(0, 1),
			new Point(0, 0),
		});
	}
	public static LocationalHeuristic createSunnys()
	{
		return new LocationalHeuristic(new Point[]
		{
			new Point(3, 3),
			new Point(3, 2),
			new Point(2, 3),
			new Point(3, 1),
			new Point(2, 2),
			new Point(3, 0),
			new Point(2, 1),
			new Point(2, 0),
			new Point(1, 3),
			new Point(1, 2),
			new Point(1, 1),
			new Point(0, 2),
			new Point(0, 3),
			new Point(1, 0),
			new Point(0, 1),
			new Point(0, 0),
		});
	}
}
