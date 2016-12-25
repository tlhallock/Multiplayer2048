package org.hallock.tfe.dsktp.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.hallock.tfe.cmn.game.TileBoard;
import org.hallock.tfe.cmn.util.Utils;

public class StateView extends JPanel {
	private TileBoard state;
	int dividerSize = 5;

	public void setTileBoard(TileBoard board)
	{
		this.state = board;
		repaint();
	}

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

		for (int r = 0; r < nr; r++) {
			for (int c = 0; c < nc; c++) {
				drawNumber(g, state.get(r, c),
						dividerSize + c * tw,
						dividerSize + r * th,
						tw - dividerSize, th - dividerSize);
			}
		}
	}

	private void drawNumber(Graphics2D g, int number, int i, int j, int tw, int th) {
		Color color = getColor(number);
		g.setColor(color);
		g.fillRect(i, j, tw, th);
		g.setColor(Utils.invertColor(color));
		g.drawString(Utils.display(number), i + tw/2, j+th/2);
	}
	
	
	private static Color getColor(int number)
	{
		switch (number)
		{
			case  0: return Color.white; 
			case  1: return new Color(0, 0,   0);
			case  2: return new Color(0, 0,  13);
			case  3: return new Color(0, 0,  27);
			case  4: return new Color(0, 0,  40);
			case  5: return new Color(0, 0,  54);
			case  6: return new Color(0, 0,  67);
			case  7: return new Color(0, 0,  81);
			case  8: return new Color(0, 0,  94);
			case  9: return new Color(0, 0, 107);
			case 10: return new Color(0, 0, 121);
			case 11: return new Color(0, 0, 134);
			case 12: return new Color(0, 0, 148);
			case 13: return new Color(0, 0, 161);
			case 14: return new Color(0, 0, 174);
			case 15: return new Color(0, 0, 188);
			case 16: return new Color(0, 0, 201);
			case 17: return new Color(0, 0, 215);
			case 18: return new Color(0, 0, 228);
			case 19: return new Color(0, 0, 242);
			case 20: return new Color(0, 0, 255);
			
			default: return Color.red;
		}
	}
}
