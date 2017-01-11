package org.hallock.tfe.cmn.sys;

import java.io.IOException;
import java.math.BigDecimal;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.InGamePlayer;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.cmn.sys.SimpleGuiGame.SinglePlayer;
import org.hallock.tfe.dsktp.gui.DesktopTileBoardViewer;
import org.hallock.tfe.serve.GamePlayerInfo;
import org.hallock.tfe.serve.GameThing;

public abstract class SimpleGuiGame<T extends SinglePlayer> extends GameThing<T>
{
	protected BigDecimal turns = BigDecimal.ZERO;
	
	protected DesktopTileBoardViewer view;

	public SimpleGuiGame(
			GameOptions options,
			DesktopTileBoardViewer view) throws IOException
	{
		super(options);
		this.view = view;
	}

	protected abstract T createPlayer();

	protected void play(PossiblePlayerActions action)
	{
		try
		{
			super.play(0, action, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static abstract class SinglePlayer extends InGamePlayer
	{
		@Override
		public String getName()
		{
			return "Single player";
		}
	}
	
	protected T getPlayer()
	{
		return players.get(0);
	}


	@Override
	protected boolean award(T player)
	{
		return false;
	}
	@Override
	protected void showAllPlayersTo(int player) {}
	@Override
	public void disconnect(int number) {}
	@Override
	protected void quit() {}
	@Override
	protected void playerChanged(int player)
	{
		view.setTileBoard(GamePlayerInfo.getGamePlayerInfo(getPlayer()));
	}
	@Override
	public void playAction(int number, EvilAction awardEvilAction, int removeFirst) throws IOException {}
}
