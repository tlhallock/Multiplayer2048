package org.hallock.tfe.ai;

import java.awt.Rectangle;
import java.io.IOException;

import org.hallock.tfe.ai.AiOptions.ComputerAiType;
import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.sys.Animator;
import org.hallock.tfe.cmn.util.DiscreteDistribution;
import org.hallock.tfe.cmn.util.SinglePanelFrame;
import org.hallock.tfe.dsktp.gui.DesktopTileBoardViewer;

public class RunHeuristic
{
	public static void main(String[] args) throws InterruptedException, IOException
	{
		int size = 4;
		while (true)
		{
			if (size == 4)
				testHeuristic(ComputerAiType.HalfSnake, size);
			testHeuristic(ComputerAiType.RealV, size);
			testHeuristic(ComputerAiType.FakeV, size);
			testHeuristic(ComputerAiType.Diags, size);
			testHeuristic(ComputerAiType.Rows, size);
			testHeuristic(ComputerAiType.Snake, size);
			testHeuristic(ComputerAiType.HalfSnake, size);
			testHeuristic(ComputerAiType.RandomSearch, size);
			if (size == 4)
				testHeuristic(ComputerAiType.Sunny, size);
		}
	}
	
	
	private static void testHeuristic(ComputerAiType type, int size) throws IOException
	{
		GameOptions options = new GameOptions();
		options.newTileDistribution = new DiscreteDistribution(new int[] {1, 2}, new double[] {.75, .25});
		options.numCols = size;
		options.numRows = size;
		options.aiOptions = new AiOptions(options.numRows, options.numCols);
		options.aiOptions.type = type;

		Animator animator = new Animator(options.aiWait);
		new Thread(animator).start();

		DesktopTileBoardViewer viewer = new DesktopTileBoardViewer(animator, "Computer");
		SinglePanelFrame showPanel = SinglePanelFrame.showPanel(viewer, new Rectangle(50, 50, 750, 750), type.name());
		ComputerGame game = new ComputerGame(options, viewer);
		game.run();
		showPanel.dispose();

		animator.quit();
	}
}
