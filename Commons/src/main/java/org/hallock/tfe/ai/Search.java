package org.hallock.tfe.ai;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hallock.tfe.ai.ComparisonSet.Worst;
import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.sys.CombinationGenerator.TileAssignment;
import org.hallock.tfe.sys.Combinatorics;

public class Search<T extends Comparable<T>>
{
	Heuristic<T> h;
	
	int transactionId;
	
	int desiredBreadth;
	private int desiredDepth;
	
	private GameOptions options;
	
	public Search(Heuristic<T> h, int depth, int breadth, GameOptions options)
	{
		this.h = h;
		this.desiredBreadth = breadth;
		this.desiredDepth = depth;
		this.options = options;
	}
	
	public synchronized SearchRunnable setBoard(TileBoard board)
	{
		transactionId++;
		SearchRunnable state = new SearchRunnable(transactionId, desiredDepth);
		state.root = new DecisionSearchTreeNode(
				board,
				null,
				0,
				state,
				null);
		return state;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public class SearchRunnable implements Runnable
	{
		public Search<T>.DecisionSearchTreeNode root;
		private PriorityBlockingQueue<QueuePriority> queue = new PriorityBlockingQueue<>();
		private HashMap<TileBoard, SearchTreeNode> alreadyComputedNodes = new HashMap<>();
		private int tId;
		private int desiredDepth;

		private final Object countSync = new Object();
		private int threadCount;
		private int numWaitingThreads;
		
		private PossiblePlayerActions bestActionSoFar;

		public SearchRunnable(int transactionId, int desiredDepth2)
		{
			this.tId = transactionId;
			this.desiredDepth = desiredDepth2;
		}

		public PossiblePlayerActions getPlay()
		{
			return bestActionSoFar;
		}
		
		public void foundBetterAction(PossiblePlayerActions action)
		{
			this.bestActionSoFar = action;
		}
		
		public void print(PrintStream ps)
		{
			if (root != null)
			{
				ps.println("Current at \n" + root.reduced);
				ps.println("Action: " + bestActionSoFar);
				root.print(ps);
			}
		}
		
		public void stop()
		{
			tId = -1;
			
			while (true)
			{
				synchronized (countSync)
				{
					if (threadCount == 0)
						return;
				}
				
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void run()
		{
			synchronized (countSync)
			{
				threadCount++;
			}
			
			Thread.currentThread().setName("Searching thread");
			
			try
			{
				while (tId == transactionId)
				{
					Search<T>.QueuePriority take;

					synchronized (countSync)
					{
						numWaitingThreads++;
						if (queue.isEmpty() && numWaitingThreads == threadCount)
						{
							break;
						}
					}
					synchronized (queue)
					{
						take = queue.poll(1, TimeUnit.SECONDS);
					}
					synchronized (countSync)
					{
						numWaitingThreads--;
					}

					if (tId != transactionId)
						break;

					if (take == null 
						|| take.event.depth >= desiredDepth
						|| take.event.computed.size() >= desiredBreadth
						|| take.event.isFinished())
					{
						continue;
					}
					take.event.sample(desiredBreadth);
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
			finally
			{
				synchronized (countSync)
				{
					threadCount--;
				}
				Thread.currentThread().setName("Not Searching thread");
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public abstract class SearchTreeNode
	{
		TileBoard reduced;
		boolean vert;
		boolean hor;
		
		T value;
		int depth;

		int maxDepth;
		SearchTreeNode maxDepthNode;
		SearchTreeNode parent;
		
		SearchTreeNode alreadyComputed;
		SearchRunnable state;
		
		LinkedList<SearchTreeNode> linkedToUs = new LinkedList<>();
		
		protected SearchTreeNode(TileBoard board, int depth, SearchTreeNode parent, SearchRunnable state)
		{
			this.depth = depth;
			setBoard(board);
			value = h.assess(depth, board);
			this.parent = parent;
			this.state = state;
			setMaxDepth();
			setComputed();
		}
		
		private void setComputed()
		{
			Search<T>.SearchTreeNode searchTreeNode = state.alreadyComputedNodes.get(reduced);
			if (searchTreeNode == null)
			{
				alreadyComputed = searchTreeNode;
				state.alreadyComputedNodes.put(reduced, this);
			}
			else
			{
				alreadyComputed = null;
			}
		}

		public void linksToUs(SearchTreeNode other)
		{
			linkedToUs.add(other);
		}

		private void setBoard(TileBoard board)
		{
//			if (board.isTopHeavy())
//			{
//				board.reflectVertically();
//				vert = true;
//			}
//			if (board.isRightHeavy())
//			{
//				board.reflectHorizontally();
//				hor = true;
//			}
			this.reduced = board;
		}
		
		TileBoard original()
		{
			TileBoard board = new TileBoard(reduced);
			if (vert)
				board.reflectVertically();
			if (hor)
				board.reflectHorizontally();
			return board;
		}

		public void setDepthMaxDepth(SearchTreeNode child, int maxDepth)
		{
			if (maxDepth <= this.maxDepth)
			{
				return;
			}
			this.maxDepth = maxDepth;
			maxDepthNode = child;
			if (parent != null)
				parent.setDepthMaxDepth(this, maxDepth);
		}
		
		public void setMaxDepth()
		{
			if (parent != null)
				parent.setDepthMaxDepth(this, depth);
		}

		public abstract void print(PrintStream ps);
		public abstract T getComparator();
		public abstract void valueChanged();
		public abstract PossiblePlayerActions getAction();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public class EventSearchTreeNode extends SearchTreeNode
	{
		LinkedList<TileAssignment> possibles = new LinkedList<>();
		
		DecisionSearchTreeNode parent;
		ComparisonSet<T, DecisionSearchTreeNode, TileAssignment> computed = new Worst<>();
		
		public EventSearchTreeNode(
				TileBoard newState,
				DecisionSearchTreeNode parent, 
				int depth, 
				SearchRunnable state)
		{
			super(newState, depth, parent, state);

			possibles = Combinatorics.enumerateTileAssignments(
					options.numberOfNewTilesPerTurn,
					options.newTileDistribution,
					newState.getPossibles(), desiredBreadth);
			
//			System.out.println("From:\n" + original());
//			for (TileAssignment assignment : possibles)
//			{
//				System.out.println(assignment);
//			}
			this.parent = parent;
		}

		@Override
		public void valueChanged()
		{
			if (parent == null)
			{
				return;
			}
			synchronized (parent)
			{
				if (getComparator() == null)
				{
					System.out.println("here");
				}
				if (getComparator().compareTo(parent.getComparator()) > 0)
				{
					parent.recomputeRepresentation();
				}
				
				parent.valueChanged();
			}
			
			for (SearchTreeNode node : linkedToUs)
			{
				synchronized (node)
				{
					node.valueChanged();
				}
			}
		}
		
		public boolean isFinished()
		{
			return alreadyComputed != null || possibles.isEmpty() || computed.size() >= desiredBreadth || depth >= desiredDepth;
		}
		
		public void sample(int size)
		{
			if (alreadyComputed != null)
				return;

			ComparisonSet<T, DecisionSearchTreeNode, TileAssignment>.Pair oldPair;
			synchronized (computed)
			{
				oldPair = computed.getCenter();
			}
			
			synchronized (this)
			{
				while (size --> 0 && !possibles.isEmpty())
				{
					TileAssignment removeFirst = possibles.removeFirst();
					TileBoard newBoard = new TileBoard(reduced);
					for (int i = 0; i < removeFirst.length(); i++)
						newBoard.tiles[removeFirst.getPoint(i).x][removeFirst.getPoint(i).y] = removeFirst.getValue(i);
					
					DecisionSearchTreeNode dec = new DecisionSearchTreeNode(newBoard, this, depth + 1, state, removeFirst);

					synchronized (computed)
					{
						computed.add(dec.getComparator(), dec, removeFirst);
					}
				}

				ComparisonSet<T, Search<T>.DecisionSearchTreeNode, TileAssignment>.Pair center = computed.getCenter();
				if (oldPair != null && center != null && oldPair.equals(center))
					return;
				if (center == null)
					return;

				valueChanged();
			}
		}
		
		@Override
		public T getComparator()
		{
			if (alreadyComputed != null)
			{
				return alreadyComputed.getComparator();
			}
			ComparisonSet<T, Search<T>.DecisionSearchTreeNode, TileAssignment>.Pair center = computed.getCenter();
			if (center == null)
				return value;
			return center.t;
		}

		public void childChanged(Search<T>.DecisionSearchTreeNode decisionSearchTreeNode)
		{
			if (computed.changed(decisionSearchTreeNode, decisionSearchTreeNode.getComparator(), decisionSearchTreeNode.parentChange))
				valueChanged();
		}

		@Override
		public void print(PrintStream ps)
		{
			Utils.indent(ps, depth);
			ps.print("event value: ");
			ps.print(getComparator());
			ps.print('\n');
			original().print(ps, depth);

			synchronized (computed)
			{
				int idx = 0;
				Iterator<DecisionSearchTreeNode> it = computed.iterator();
				while (it.hasNext())
				{
					Search<T>.DecisionSearchTreeNode next = it.next();
					if (!next.getComparator().equals(getComparator()))
					{
						continue;
					}

					Utils.indent(ps, depth);
					if (next.getComparator().equals(getComparator()))
					{
						ps.print("******** ");
					}
					ps.print("index ");
					ps.print(idx++);
					ps.print(":\n");

					next.print(ps);
					
					break;
				}
			}
		}

		@Override
		public PossiblePlayerActions getAction()
		{
			throw new RuntimeException("no action");
		}
	}
	
	public class DecisionSearchTreeNode extends SearchTreeNode
	{
		TileAssignment parentChange;
		EventSearchTreeNode left;
		EventSearchTreeNode right;
		EventSearchTreeNode up;
		EventSearchTreeNode down;
		
		T optimal;
		EventSearchTreeNode parent;
		PossiblePlayerActions action;
		
		public DecisionSearchTreeNode(TileBoard newState,  EventSearchTreeNode parent, int depth, SearchRunnable state, TileAssignment parentsEvent)
		{
			super(newState, depth, parent, state);
			
			this.parentChange = parentsEvent;

			this.parent = parent;
			
			TileBoard board;
			if ((board = new TileBoard(newState)).left(new TileChanges()).changed())
				queue(left = new EventSearchTreeNode(board, this, depth + 1, state));
			if ((board = new TileBoard(newState)).right(new TileChanges()).changed())
				queue(right = new EventSearchTreeNode(board, this, depth + 1, state));
			if ((board = new TileBoard(newState)).up(new TileChanges()).changed())
				queue(up = new EventSearchTreeNode(board, this, depth + 1, state));
			if ((board = new TileBoard(newState)).down(new TileChanges()).changed())
				queue(down = new EventSearchTreeNode(board, this, depth + 1, state));
			
			recomputeRepresentation();
		}

		public synchronized void recomputeRepresentation()
		{
			T t = null;
			
			if (alreadyComputed == null)
			{
				if ((t == null && left != null) || (left != null && t != null && t.compareTo(left.getComparator()) < 0))
				{
					t = left.getComparator();
					if (hor)
						action = PossiblePlayerActions.Right;
					else
						action = PossiblePlayerActions.Left;
				}
				if ((t == null && right != null) || (right != null && t != null && t.compareTo(right.getComparator()) < 0))
				{
					t = right.getComparator();
					if (hor)
						action = PossiblePlayerActions.Left;
					else
						action = PossiblePlayerActions.Right;
				}
				if ((t == null && up != null) || (up != null && t != null && t.compareTo(up.getComparator()) < 0))
				{
					t = up.getComparator();
					if (vert)
						action = PossiblePlayerActions.Down;
					else
						action = PossiblePlayerActions.Up;
				}
				if ((t == null && down != null) || (down != null && t != null && t.compareTo(down.getComparator()) < 0))
				{
					t = down.getComparator();
					if (vert)
						action = PossiblePlayerActions.Up;
					else
						action = PossiblePlayerActions.Down;
				}
			}
			else
			{
				t = alreadyComputed.getComparator();
				action = alreadyComputed.getAction();
			}
			
			setOptimal(t);
		}

		private void setOptimal(T t)
		{
			T oldOptimal = optimal;
			if (t == null)
			{
				optimal = value;
			}
			else
			{
				optimal = t;
			}
			
			if (oldOptimal == null || (getComparator() != null && !getComparator().equals(oldOptimal)))
			{
				valueChanged();
			}
		}

		public PossiblePlayerActions getPlay()
		{
			return action;
		}
		
		private void queue(Search<T>.EventSearchTreeNode evt)
		{
			QueuePriority queuePriority = new QueuePriority(evt.depth, evt);
			if (queuePriority.event.isFinished())
				return;
			state.queue.add(queuePriority);
			
		}

		@Override
		public void valueChanged()
		{
			if (parent == null)
			{
				state.foundBetterAction(action);
				return;
			}
			
			synchronized (parent)
			{
				parent.childChanged(this);
			}

			for (SearchTreeNode node : linkedToUs)
			{
				synchronized (node)
				{
					node.valueChanged();
				}
			}
		}

		@Override
		public void print(PrintStream ps)
		{
			Utils.indent(ps, depth);
			ps.print("decision value: ");
			ps.print(getComparator());
			ps.print('\n');
			original().print(ps, depth);

			if (left != null && left.getComparator().equals(getComparator()))
			{
				Utils.indent(ps, depth);
				if (left.getComparator().equals(getComparator()))
				{
					ps.print("******** ");
				}
				if (hor)
					ps.print("right:\n");
				else
					ps.print("left:\n");
				left.print(ps);
			}
			if (right != null && right.getComparator().equals(getComparator()))
			{
				Utils.indent(ps, depth);
				if (right.getComparator().equals(getComparator()))
				{
					ps.print("******** ");
				}
				if (hor)
					ps.print("left:\n");
				else
					ps.print("right:\n");
				right.print(ps);
			}
			if (up != null && up.getComparator().equals(getComparator()))
			{
				Utils.indent(ps, depth);
				if (up.getComparator().equals(getComparator()))
				{
					ps.print("******** ");
				}
				if (vert)
					ps.print("down:\n");
				else
					ps.print("up:\n");
				up.print(ps);
			}
			if (down != null && down.getComparator().equals(getComparator()))
			{
				Utils.indent(ps, depth);
				if (down.getComparator().equals(getComparator()))
				{
					ps.print("******** ");
				}
				if (vert)
					ps.print("up:\n");
				else
					ps.print("down:\n");
				down.print(ps);
			}
		}

		@Override
		public T getComparator()
		{
			return optimal;
		}

		@Override
		public PossiblePlayerActions getAction()
		{
			return action;
		}
	}

	public class QueuePriority implements Comparable<QueuePriority>
	{
		int depth;
		EventSearchTreeNode event;
		
		public QueuePriority(int depth, Search<T>.EventSearchTreeNode event)
		{
			super();
			this.depth = depth;
			this.event = event;
		}

		@Override
		public int compareTo(QueuePriority o)
		{
			int cmp = Integer.compare(depth, o.depth);
			if (cmp != 0)
				return cmp;
			return -event.getComparator().compareTo(o.event.getComparator());
		}
	}
}
