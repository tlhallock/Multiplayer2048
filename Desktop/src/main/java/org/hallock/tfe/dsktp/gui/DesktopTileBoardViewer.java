package org.hallock.tfe.dsktp.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.game.TileChanges;
import org.hallock.tfe.cmn.game.TileChanges.TileChange;
import org.hallock.tfe.cmn.sys.Animator;
import org.hallock.tfe.cmn.util.Utils;

public class DesktopTileBoardViewer extends JPanel {
	
	private static final long ANIMATION_TIME = 250;
	
	TileBoard state;
	TileChanges changes;
	int dividerSize = 5;
	String name;
	// Should not make an animator for each one
	Animator animator = new Animator(20, new Runnable()
	{
		@Override
		public void run()
		{
			repaint();
		}
	});
	int currentTurn;
	private long startAnimating;
	
	public DesktopTileBoardViewer(String name)
	{
		this.name = name;
	}

	public void setTileBoard(TileBoard board, TileChanges changes, int newTurn)
	{
		if (currentTurn != newTurn)
		{
			startAnimating = System.currentTimeMillis();
			currentTurn = newTurn;
		}
		this.state = new TileBoard(board);
		this.changes = changes;
		repaint();
	}

	@Override
	public void paint(Graphics gr) {
		
		Graphics2D g = (Graphics2D) gr;
		int w = getWidth();
		int h = getHeight();
		
		g.setColor(Color.red);
		g.fillRect(0, 0, w, h);

		if (state == null)
			return;
		
		// assumes we try to fit it into a given size
		int nr = state.getNRows();
		int nc = state.getNCols();
		int tw = (w - dividerSize) / (nc);
		int th = (h - dividerSize) / (nr);
		

		long now = System.currentTimeMillis();
		if (now > startAnimating + ANIMATION_TIME || changes == null)
		{
			animator.setRunning(false);
			for (int r = 0; r < nr; r++) {
				for (int c = 0; c < nc; c++) {
					drawTile(g, state.get(r, c),
							dividerSize + c * tw,
							dividerSize + r * th,
							tw - dividerSize, th - dividerSize,
							1.0);
				}
			}
		}
		else
		{
			if (name == null)
			{
				System.out.println("start: " + startAnimating);
				System.out.println("now: "   + now);
				System.out.println(now - startAnimating);
			}
			
			animator.setRunning(true);

			g.setColor(Color.white);
			for (int r = 0; r < nr; r++) {
				for (int c = 0; c < nc; c++) {
					drawBlank(g,
							dividerSize + c * tw,
							dividerSize + r * th,
							tw - dividerSize, th - dividerSize);
				}
			}
			
			for (TileChange change : changes)
			{
				double mult = (now - startAnimating) / (double) ANIMATION_TIME;
				
				if (change.from1Col != change.toCol && change.from1Row != change.toRow)
				{
					System.out.println("uh oh");
				}

				if (change.from2Col >= 0 && change.from2Row >= 0)
				{
					double oldR = dividerSize + change.from2Row * th;
					double oldC = dividerSize + change.from2Col * tw;
				
					double newR = dividerSize + change.toRow * th;
					double newC = dividerSize + change.toCol * tw;
				
					double cR = oldR + mult * (newR - oldR);
					double cC = oldC + mult * (newC - oldC);
				
					drawTile(g, change.toNum,
						(int) cC,
						(int) cR,
						tw - dividerSize, th - dividerSize,
						1 - mult);
				}
				
				{
					double oldR = dividerSize + change.from1Row * th;
					double oldC = dividerSize + change.from1Col * tw;

					double newR = dividerSize + change.toRow * th;
					double newC = dividerSize + change.toCol * tw;

					double cR = oldR + mult * (newR - oldR);
					double cC = oldC + mult * (newC - oldC);

					drawTile(g, change.toNum,
						(int) cC,
						(int) cR,
						tw - dividerSize, th - dividerSize,
						change.isNew ? mult : 1);
				}
			}
		}


		g.setColor(Color.black);
		if (name != null)
			g.drawString(name, w/2, h/2);
	}

	private void drawTile(Graphics2D g, int number, int i, int j, int tw, int th, double alpha) {
		Color color = getColor(number, alpha);
		g.setColor(color);
		g.fillRect(i, j, tw, th);
		g.setColor(Utils.invertColor(color));
		g.drawString(Utils.display(number), i + tw/2, j+th/2);
	}
	private void drawBlank(Graphics2D g, int i, int j, int tw, int th) {
		g.fillRect(i, j, tw, th);
	}
	
	
	private static Color getColor(int number, double alpha)
	{
		switch (number)
		{
			case  0: return Color.white; 
			case  1: return new Color(scale(0, alpha), scale(0, alpha), scale(  0, alpha));
			case  2: return new Color(scale(0, alpha), scale(0, alpha), scale( 13, alpha));
			case  3: return new Color(scale(0, alpha), scale(0, alpha), scale( 27, alpha));
			case  4: return new Color(scale(0, alpha), scale(0, alpha), scale( 40, alpha));
			case  5: return new Color(scale(0, alpha), scale(0, alpha), scale( 54, alpha));
			case  6: return new Color(scale(0, alpha), scale(0, alpha), scale( 67, alpha));
			case  7: return new Color(scale(0, alpha), scale(0, alpha), scale( 81, alpha));
			case  8: return new Color(scale(0, alpha), scale(0, alpha), scale( 94, alpha));
			case  9: return new Color(scale(0, alpha), scale(0, alpha), scale(107, alpha));
			case 10: return new Color(scale(0, alpha), scale(0, alpha), scale(121, alpha));
			case 11: return new Color(scale(0, alpha), scale(0, alpha), scale(134, alpha));
			case 12: return new Color(scale(0, alpha), scale(0, alpha), scale(148, alpha));
			case 13: return new Color(scale(0, alpha), scale(0, alpha), scale(161, alpha));
			case 14: return new Color(scale(0, alpha), scale(0, alpha), scale(174, alpha));
			case 15: return new Color(scale(0, alpha), scale(0, alpha), scale(188, alpha));
			case 16: return new Color(scale(0, alpha), scale(0, alpha), scale(201, alpha));
			case 17: return new Color(scale(0, alpha), scale(0, alpha), scale(215, alpha));
			case 18: return new Color(scale(0, alpha), scale(0, alpha), scale(228, alpha));
			case 19: return new Color(scale(0, alpha), scale(0, alpha), scale(242, alpha));
			case 20: return new Color(scale(0, alpha), scale(0, alpha), scale(255, alpha));
			
			default: return Color.red;
		}
	}
	
	private static int scale(int i, double d)
	{
		return (int) (255 + d * (i - 255));
	}

	public void stop()
	{
		animator.quit();
	}

	public void start()
	{
		new Thread(animator).start();
	}
}
