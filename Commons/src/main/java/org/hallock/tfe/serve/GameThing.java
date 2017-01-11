package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.ArrayList;

import org.hallock.tfe.ai.GameWriterIf.Turn;
import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.History;
import org.hallock.tfe.cmn.game.InGamePlayer;
import org.hallock.tfe.cmn.game.InGamePlayer.PlayingStatus;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.sys.GameUpdateInfo;
import org.hallock.tfe.sys.Statistics;

public abstract class GameThing<T extends InGamePlayer>
{
	protected ArrayList<T> players = new ArrayList<>();
	GameOptions options;
	private Statistics statistics;

	public GameThing(GameOptions options)
	{
		this.options = options;
		statistics = new Statistics();
	}
	
	protected synchronized void add(T computerPlayer) throws IOException
	{
		computerPlayer.setPoints(options.createPoints());
		computerPlayer.setPlayerNumber(players.size());
		TileBoard board = new TileBoard(options.numRows, options.numCols);
		board.initialize(options);
		computerPlayer.setBoard(board);
		computerPlayer.setHistory(new History());
		computerPlayer.createWriter();
		players.add(computerPlayer);
	}

	protected GameUpdateInfo getGameState()
	{
		GameUpdateInfo info = new GameUpdateInfo();
		
		for (InGamePlayer player : players)
			info.addPlayer(player);
		
		return info;
	}


	public void play(int playerNum, PossiblePlayerActions swipe, boolean award) throws IOException
	{
		boolean changed = false;
		T player = players.get(playerNum);
		TileBoard state = player.getBoard();
		History history = player.getHistory();
		
		if (!player.getStatus().canPlay())
		{
			return;
		}
		
		switch (swipe)
		{
		case Up:
		case Down:
		case Left:
		case Right:
		{
			TileBoard oldBoard = new TileBoard(state);
			TileChanges changes = new TileChanges();
			swipe.perform(state, changes);
			if (!changes.changed())
				break;
			player.setBoard(state);
			state.fillTurn(options, changes);
			player.incrementDrawId();
			int turn = player.incrementNumberOfTurns();
			player.setChanges(changes);
			if (!state.hasMoreMoves())
				player.setPlayingStatus(PlayingStatus.NoMoreMoves);
			player.addPoints(changes);
			history.updated(player);
			changed = true;
			player.getWriter().playTurn(new Turn(swipe, oldBoard, state), turn);
			break;
		}
		case Redo:
		case Undo:
		{
			TileBoard oldBoard = new TileBoard(state);
			if (!swipe.perform(history, player))
				break;
			player.setBoard(state);
			player.incrementDrawId();
			int turn = player.incrementNumberOfTurns();
			player.setChanges(null);
			if (!state.hasMoreMoves())
				player.setPlayingStatus(PlayingStatus.NoMoreMoves);
			player.getWriter().playTurn(new Turn(swipe, oldBoard, state), turn);
			changed = true;
			break;
		}
		case Quit:
			player.setPlayingStatus(PlayingStatus.Viewing);
			break;
		case Disconnect:
			disconnect(playerNum);
			break;
		case ShowAllTileBoards:
			showAllPlayersTo(playerNum);
			break;
			
		default:
			System.out.println("Unknown action: " + swipe);
		}

		if (changed && award)
		{
			award(player);
		}
		
		if (changed)
		{
			playerChanged(playerNum);
		}
		
		if (ganeIsOver())
		{
			quit();
		}
		
	}

	protected abstract boolean award(T player) throws IOException;
	protected abstract void showAllPlayersTo(int player);
	public abstract void disconnect(int number);
	public abstract void playAction(int number, EvilAction awardEvilAction, int removeFirst) throws IOException;

	public int getNumberOfPlayers()
	{
		return players.size();
	}

	protected abstract void quit();
	protected abstract void playerChanged(int player);

	private boolean ganeIsOver()
	{
		for (InGamePlayer player : players)
			if (player.getStatus().keepsGameOpen())
				return false;
		return true;
	}
}
