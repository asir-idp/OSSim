package edu.upc.fib.ossim.memory.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Paints fragmentation color legend, external and internal 
 * 
 * @author Alex Macia
 */
public class LegendPainter extends JPanel {
		
	private static final long serialVersionUID = 1L;
	private static final int L_SIDE = 20;
	private Font font;
	
	/**
	 * Constructs fragmentation legend canvas
	 *  
	 * @param width
	 * @param height
	 */
	public LegendPainter(int width, int height) {
		font = new Font(Font.MONOSPACED, Font.BOLD, 10);
		this.setPreferredSize(new Dimension(width, height));
	}

	/**
	 * Paints legend graphic components
	 *
	 * @param g	graphic context	
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// Legend
		Functions.getInstance().drawTexture(g2, MemoryPainter.FRAG_E, 0, 5, L_SIDE, L_SIDE);
		g2.drawString(Translation.getInstance().getLabel("me_60"), L_SIDE + 5, 10 + L_SIDE/2);

	    FontRenderContext frc = g2.getFontRenderContext();
	    Rectangle2D boundsArea = font.getStringBounds(Translation.getInstance().getLabel("me_60"), frc);
	    int wText = (int)boundsArea.getWidth();
	    
	    Functions.getInstance().drawTexture(g2, MemoryPainter.FRAG_I, L_SIDE + wText + 20, 5, L_SIDE, L_SIDE);
		g2.drawString(Translation.getInstance().getLabel("me_61"), 2*L_SIDE + wText + 30, 10 + L_SIDE/2);
	}
}


