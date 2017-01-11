package org.hallock.tfe.cmn.game;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.hallock.tfe.ai.GameWriterIf;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.sys.PointsCounter;

public abstract class InGamePlayer
{
	protected int playerNumber;
	protected int drawId;
	protected int numberOfTurns;
	protected TileBoard board;
	protected TileChanges changes;
	protected History history;
	protected PointsCounter points;
	protected GameWriterIf writer = GameWriterIf.NULL_WRITER;
	protected PlayingStatus status = PlayingStatus.Playing;
	
	public InGamePlayer() {}

	public GameWriterIf getWriter()
	{
		return writer;
	}
	
	public void setChanges(TileChanges changes2)
	{
		this.changes = changes2;
	}

	public void incrementDrawId()
	{
		drawId++;
	}
	public int incrementNumberOfTurns()
	{
		return numberOfTurns++;
	}
	public History getHistory()
	{
		return history;
	}
	public void setHistory(History history)
	{
		this.history = history;
	}

	public void setPoints(PointsCounter counter)
	{
		this.points = counter;
	}

	public final void setPlayerNumber(int size)
	{
		this.playerNumber = size;
	}
	
	protected void statusChanged() {}

	public void setPlayingStatus(PlayingStatus status)
	{
		if (this.status.equals(status))
			return;
		if (this.status.allows(status))
			this.status = status;
		statusChanged();
	}
	
	public int getPlayerNumber()
	{
		return playerNumber;
	}

	public TileBoard getBoard()
	{
		return board;
	}

	public TileChanges getChanges()
	{
		return changes;
	}

	public int getTurnId()
	{
		return drawId;
	}
	public int getNumberOfTurns()
	{
		return numberOfTurns;
	}
	
	public void addPoints(TileChanges newState)
	{
		newState.countPoints(points);
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public void setBoard(TileBoard board)
	{
		this.board = board;
	}

	public TileBoard getTileBoard()
	{
		return this.board;
	}

	public PointsCounter getPoints()
	{
		return points;
	}

	public PlayingStatus getStatus()
	{
		return status;
	}
	
	
	
//	public abstract void quit();
	public abstract String getName();
	public abstract void createWriter() throws IOException;
	
	public Collection<? extends EvilAction> getAppliedActions()
	{
		return Collections.emptyList();
	}

	public Collection<? extends EvilAction> getAvailableActions()
	{
		return Collections.emptyList();
	}
	
	
	
	
	
	
	

	
	public enum PlayingStatus
	{
		Playing,
		NoMoreMoves,
		Viewing,
		Disconnected,
		
		;
		
		public boolean allows(PlayingStatus status)
		{
			switch (this)
			{
			case Playing:
			case NoMoreMoves:
				return true;
			case Viewing:
				switch (this)
				{
				case Playing:
				case NoMoreMoves:
					return false;
				case Viewing:
				case Disconnected:
					return true;
				default:
					throw new RuntimeException();
				}
			case Disconnected:
				switch (this)
				{
				case Playing:
				case NoMoreMoves:
				case Viewing:
					return false;
				case Disconnected:
					return true;
				default:
					throw new RuntimeException();
				}
			default:
				throw new RuntimeException();
			}
		}

		public boolean keepsGameOpen()
		{
			switch (this)
			{
			case Playing:
			case NoMoreMoves:
				return true;
			case Viewing:
			case Disconnected:
				return false;
			default:
				throw new RuntimeException();
			}
		}

		public boolean canPlay()
		{
			switch (this)
			{
			case Playing:
			case NoMoreMoves:
				return true;
			case Viewing:
			case Disconnected:
				return false;
			default:
				throw new RuntimeException();
			}
		}
	}
}
