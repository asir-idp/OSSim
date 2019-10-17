package edu.upc.fib.ossim.disk.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.Iterator;

import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Disk scheduling results graph, x-axis shows cylinders requested and y-axis time, 
 * 
 * @author Alex Macia
 * 
 * @see PainterTemplate
 */
public class GraphPainter extends PainterTemplate {
		
	private static final long serialVersionUID = 1L;
	private static final int BLOCK = 6;
	private static final int MARGIN = 5;
	private static final int HEADER = 40;
	private static final int HEADER_INC = 2;
	
	/**
	 * Constructs a GraphPainter.  
	 * 
	 * @param presenter	event manager
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public GraphPainter(Presenter presenter, int width, int height) {
		super(presenter, width, height);
	}
	
	/**
	 * 
	 * Draws served requests graph depending on time processes, points representing requests 
	 * are painted in its appropriate color, arrows join requests as they are been served
	 * 
	 * @param g	graphic context
	 * 
	 * @see ProcessPresenter#iterator(int i)
	 */
	public void paint(Graphics g) {
		g2 = (Graphics2D) g;
		
		Dimension size = getSize();
		int w = (int)size.getWidth();
		int h = (int)size.getHeight();
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, w, h);
		g2.setPaint(Color.BLACK);
		g2.drawRect(0, 0, w-1, h-1);
		
		// Header
		//int blocks = controller.getNblocks();
		int cylinders = ((DiskPresenter) presenter).getCylinders();
		int sectors = ((DiskPresenter) presenter).getSectors();
		g2.setColor(Color.BLACK);
		g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		g2.drawString(Translation.getInstance().getLabel("dk_60"),MARGIN, 15);
		
		int coord = 0;
		while (coord < cylinders) {
			g2.drawString(new Integer(coord).toString(),(coord * w / cylinders), 30);
			coord += HEADER_INC;
		}
		//g2.drawString(new Integer(blocks).toString(), w-10, 10);
		
		// Graph area
		g2.setPaint(Color.LIGHT_GRAY);
		g2.fillRect(MARGIN, HEADER, w-2*MARGIN, h-HEADER-MARGIN);
		g2.setPaint(Color.BLACK);
		g2.drawRect(MARGIN, HEADER, w-2*MARGIN, h-HEADER-MARGIN);
		 
		int cylinder = ((DiskPresenter) presenter).getInitHeadPosition() / sectors;
	    double x = MARGIN + cylinder* w / cylinders;
	    double y = HEADER + 10;
	    double y_shift = (h - HEADER - 10) / (((DiskPresenter) presenter).getRequestsServed()+1); 
	    
	    g2.fillOval((int) x - BLOCK/2, (int) y - BLOCK/2, BLOCK, BLOCK);
		
		GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		polyline.moveTo (x, y);
		
		Iterator<Integer> it = presenter.iterator(1);
		while (it.hasNext()) {
			int block = it.next().intValue();
			if (block < 0 || block >= cylinders*sectors) {
				g2.setColor(Color.BLACK);
				if (block >= cylinders*sectors) block--; 
			} else g2.setColor(presenter.getColor(block)); 
			cylinder = block / sectors;
			
			x = MARGIN + cylinder * w / cylinders;
			y+= y_shift;
			polyline.lineTo(x , y);
			g2.fillOval((int) x - BLOCK/2, (int) y - BLOCK/2, BLOCK, BLOCK);
		}
		
		g2.setColor(Color.BLACK);
		g2.draw(polyline);
	}


	/**
 	 * Returns false. No need of event control over this device       
	 * 
	 * @param o 	unused
	 * @param x	x 	unused
	 * @param y	y 	unused
	 * 
	 * @return	false 
	 */
	public boolean contains(Object o, int x, int y) {
		return false;
	}
}