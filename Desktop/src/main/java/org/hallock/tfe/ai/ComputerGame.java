package org.hallock.tfe.ai;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.io.IOException;

import org.hallock.tfe.ai.ComputerGame.ComputerPlayer;
import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.sys.SimpleGuiGame;
import org.hallock.tfe.dsktp.gui.DesktopTileBoardViewer;
import org.hallock.tfe.serve.ai.ComputerAiStrategy;
import org.hallock.tfe.sys.GameConstants;

public class ComputerGame extends SimpleGuiGame<ComputerPlayer> implements Runnable
{
	private ComputerAiStrategy strategy;
	
	AiOptions aiOptions;

	public ComputerGame(GameOptions options,
			DesktopTileBoardViewer view) throws IOException
	{
		super(options, view);
		this.aiOptions = options.aiOptions;
		add(createPlayer());
		strategy = AiOptions.createComputerAiStrategy(aiOptions, 0, options);
	}

	@Override
	protected ComputerPlayer createPlayer()
	{
		return new ComputerPlayer(aiOptions);
	}


	@Override
	public void run()
	{
		Robot robot = null;
		try
		{
			robot = new Robot();
		}
		catch (AWTException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (getPlayer().getBoard().hasMoreMoves())
		{
			Point location = MouseInfo.getPointerInfo().getLocation();
			int delta = 10;
			robot.mouseMove(
					Math.max(0, Math.min(1000, location.x + delta / 2 - GameConstants.random.nextInt(delta))), 
					Math.max(0, Math.min(1000, location.y + delta / 2 - GameConstants.random.nextInt(delta))));
			
			
			
			strategy.setNewBoard(getPlayer().getBoard());
			PossiblePlayerActions nextMove = strategy.getNextMove();
			play(nextMove);
			view.repaint();
		}
		
		try
		{
			getPlayer().getWriter().close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static class ComputerPlayer extends SimpleGuiGame.SinglePlayer
	{
		AiOptions options;
		
		public ComputerPlayer(AiOptions options)
		{
			this.options = options;
		}

		@Override
		public void createWriter() throws IOException
		{
			writer = GameWriterIf.createAiWriter(options);
		}
	}
}
