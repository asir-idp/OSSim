package edu.upc.fib.ossim.process.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;
import java.util.Vector;

import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Paints CPU. Processes enter into CPU and are executed while time goes, 
 * process execution time remaining is highlighted, and a brief process information is shown 
 * 
 * @author Alex Macia
 */
public class ProcessorPainter extends PainterTemplate {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a ProcessorPainter
	 * 
	 * @param presenter	event manager
	 * @param width		canvas width
	 * @param height	canvas height
	 */
	public ProcessorPainter(Presenter presenter, int width, int height) {
		super(presenter, width, height);
	}

	/**
	 * Draws processor components, mainly a rectangle that represents a process  
	 * 
	 * @param g	graphic context
	 */
	public void paint(Graphics g) {
		g2 = (Graphics2D) g;
		Dimension size = getSize();
		int w = (int)size.getWidth();
		int h = (int)size.getHeight();
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, w, h);
		
		// Process in processor 
		String proc = Translation.getInstance().getLabel("pr_03");
		String idle = Translation.getInstance().getLabel("pr_04");
		String running = Translation.getInstance().getLabel("pr_05");
		String[] s = {proc, idle, running}; 
		int maxStrWidth = Functions.getInstance().maxStringsWidth(s, g2.getFontRenderContext(), g2.getFont());
		
		// Measures
		int offset = 10;
		int x_proc = maxStrWidth + 2*offset;
		int y_proc = 2*offset;
		int w_proc = w - maxStrWidth - 4*offset;
		int h_proc = h - 4*offset;
		
		// background
		g2.setColor(Color.WHITE);
		g2.fillRect(2, 2, w-2, h-2);
		g2.setColor(Color.BLACK);
		g2.drawRect(0, 0, w-1, h-1);
		
		g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
		g2.setColor(Color.GRAY);
		g2.drawString(proc, offset, h/2);

		g2.drawRect(x_proc, y_proc, w_proc, h_proc);
		
		int pid = ((ProcessPresenter) presenter).getRunning();
		if (pid == 0) {
			g2.drawString(idle, offset, h/2 + 2*offset);
		} else {
			g2.drawString(running, offset, h/2 + 2*offset);
			
			int psize =  ((ProcessPresenter) presenter).getSize(pid); 
			int current = ((ProcessPresenter) presenter).getCurrent(pid);
			
			x_proc += 3*offset;
			y_proc += offset;
			w_proc -= 6*offset;
			h_proc /= 2;
			
			drawProcess(pid, x_proc, y_proc, w_proc, h_proc);
			
			// CPU I/O burst cycle
			int last_x = x_proc;
			int hburstunit = w_proc/5;
			int wburstunit;
			if (psize > 5) wburstunit = w_proc/psize;
			else wburstunit = w_proc/5;
			
			for(int i=0; i < psize; i++) {
				boolean cpuBurst = ((ProcessPresenter) presenter).isCPUBurst(pid, i); 
				Rectangle rect = null;
				if (cpuBurst) rect = new Rectangle(last_x + i*wburstunit, y_proc + h_proc + 10, wburstunit, hburstunit); 
				else rect = new Rectangle(last_x + i*wburstunit, y_proc + h_proc + hburstunit + 10, wburstunit, hburstunit);
				if (i == current) g2.setColor(Color.RED);
				else g2.setColor(Color.LIGHT_GRAY);
				g2.fill(rect);
				g2.setColor(Color.BLACK);
				g2.draw(rect);
			}
			
			g2.setColor(Color.BLACK);
			g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
			g2.drawString("CPU", x_proc - 3*offset + 2, y_proc + h_proc + 27);
			g2.drawString("I/O", x_proc - 3*offset + 2, y_proc + h_proc + hburstunit + 27);
		}
	}
	
	private void drawProcess(int pid, int x, int y, double width, double height) {
		RoundRectangle2D rect = new RoundRectangle2D.Double(x,y,width,height, 5, 5); // Process Rectangle
		map.put(rect, new Integer(pid));
		
		g2.setColor(presenter.getColor(pid));
		g2.fill(rect);
		
		// Bound
		g2.setColor (Color.BLACK);
		g2.draw(rect);

		// Information
		Vector<String> info = presenter.getInfo(pid);
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D bounds;
		int xText = x + 5;
	    int yText = y + 20;
		
		Iterator<String> it = info.iterator();
		while (it.hasNext()) {
			String s = it.next();
			bounds = g2.getFont().getStringBounds(s, frc);
			if (bounds.getWidth() > width) {
				s = s.substring(0, 5);
				s += "...";
				bounds = g2.getFont().getStringBounds(s, frc);
			}
			g2.setColor(Color.GRAY);
			g2.drawString(s, xText, yText);
			yText += bounds.getHeight();
		}
	}
	
	/**
 	 * Returns true when the rectangle representing process at cpu contains position (x,y) and false otherwise    
	 * 
	 * @param o	graphic object
	 * @param x	x position
	 * @param y	y position
	 * 
	 * @return	process at cpu contains (x,y) 
	 */
	public boolean contains(Object o, int x, int y){
		RoundRectangle2D r = (RoundRectangle2D) o;  
		return r.contains(x, y);
	}
}


