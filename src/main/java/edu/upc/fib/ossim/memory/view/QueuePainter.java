package edu.upc.fib.ossim.memory.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Represents a processes queue. Processes are queued as they are created, 
 * and are laid out horizontally, an arrow is placed between processes 
 * showing queue direction. A brief information is shown over every process. <br/>
 * A pop up menu allows process update and delete 
 * 
 * @author Alex Macia
 * 
 * @see PainterTemplate
 */
public class QueuePainter extends PainterTemplate {
		
	private static final long serialVersionUID = 1L;
	private static final int P_HEIGHT = 50;
	private static final int P_WIDTH = 85;
	private static final int P_UNITWIDTH = 9;
	private static final int P_UNITHEIGHT = 10;
	private static final int ARROW_HEIGHT = 20;
	private static final int ARROW_WIDTH = 20;
	private BufferedImage arrow;
	private String keytitle;
	
	/**
	 * Constructs a ProgramsPainter, creates the pop up menu.  
	 * 
	 * @param presenter	event manager
	 * @param keytitle 	reference to title
	 * @param menuItems	pop up menu items
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public QueuePainter(Presenter presenter, String keytitle, Vector<String[]> menuItems, int width, int height) {
		super(presenter, menuItems, width, height);
		this.keytitle = keytitle;
		arrow = Functions.getInstance().loadImage("arrow1.png");
	}

	/**
	 * Draws processes queue components, a set of colored rectangles represents processes,
	 * after every process there is an arrow icon. 
	 * When queue width exceeds canvas width, queue enlarge and revalidates to perform scroll update
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
		
		map.clear();
		
		// Draws a processes queue into area x,y,w,h 
		int last_x = w - 20; // Starts at queue's right side 
		int program_y = 30;
		
		// background
		//g2.setPaint(bg);
		g2.setColor(Color.WHITE);
		g2.fillRect(2, 2, w-2, h-2);
		g2.setColor(Color.BLACK);
		g2.drawRect(0, 0, w, h);
		
		// Draw ready queue
		g2.setColor(Color.LIGHT_GRAY);
		g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		FontRenderContext frc = g2.getFontRenderContext();
		String title = Translation.getInstance().getLabel(keytitle);
		Rectangle2D bounds = g2.getFont().getStringBounds(title, frc);
		g2.drawString(title,  (int) (w/2 - bounds.getWidth()/2), 20); 
		
		Iterator<Integer> it = presenter.iterator(getQueue());
		
		while (it.hasNext()) {
			g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			Integer pid = it.next();
			
			if (last_x - P_WIDTH - 10 - ARROW_WIDTH < 0) {
				int newWidth = w - (last_x - P_WIDTH - 10 - ARROW_WIDTH) + 20;
				setPreferredSize(new Dimension(newWidth, h));
				revalidate(); // Updates scroll 
			}
			
			// Program
			drawProgram(pid.intValue(), last_x - P_WIDTH, program_y, P_WIDTH, P_HEIGHT);
			
			// Size, pages or segments
			drawBlocks(pid.intValue(), last_x - P_WIDTH, program_y + P_HEIGHT);

			last_x -= P_WIDTH; 
			
			// Arrow
			drawArrow(arrow, last_x - 5 - ARROW_WIDTH, 50, ARROW_HEIGHT, ARROW_WIDTH);
			
			last_x -= 10 + ARROW_WIDTH;
		}
		
		if (w - last_x < MemoryPresenter.PROGRAMS_WIDTH) {
			// Restore initial width
			setPreferredSize(new Dimension(MemoryPresenter.PROGRAMS_WIDTH, MemoryPresenter.PROGRAMS_HEIGHT));
			revalidate(); // Updates scroll 
		}
	}

	private void drawProgram(int pid, int x, int y, int width, int height) {
		RoundRectangle2D rect = new RoundRectangle2D.Double(x,y,width,height, 5, 5); // Process Rectangle
		Rectangle rec = new Rectangle(x,y,width,height);
		mapProcess(rec, pid);
		
		g2.setColor(presenter.getColor(pid));
		g2.fill(rect);
		
		// Bound
		g2.setColor(Color.BLACK);
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

	private void drawBlocks(int pid, int x_ini, int y_ini) {
		g2.setColor(Color.BLACK);
		g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		String sizeinfo = ((MemoryPresenter) presenter).getComponentSizeInfo(pid); 
		g2.drawString(sizeinfo, x_ini, y_ini + 15);
		
		int blocks = ((MemoryPresenter) presenter).getTotalComponents(pid);	
		
		int x = x_ini;
		int y = y_ini + 20;
		int uxline = 0; // 8 units per line
		
		for(int b=0; b < blocks; b++) {
			int bsize = ((MemoryPresenter) presenter).getSizeOfComponents(pid, b);
			boolean bswap = ((MemoryPresenter) presenter).isComponentSwapped(pid, b);
			
			if (uxline == 0)  y += 2; // new block next line
			else {
				if (((MemoryPresenter) presenter).isComponentPage(pid, b)) {
					x += 2; // new block same line	
				} else {					
					uxline = 0;
					x = x_ini;
					y += P_UNITHEIGHT + 2 ; // new block next line
				}
			}
			
			for(int i=0; i < bsize; i++) {
				Rectangle rect = new Rectangle(x, y,P_UNITWIDTH,P_UNITHEIGHT); 
				if (bswap) {
					g2.setColor(Color.GRAY);
					g2.fill(rect);
					mapComponent(rect, 1000*pid + b); // Compound id 1000 * pid + component id
					g2.setColor(Color.BLACK);
				}
				g2.draw(rect);
				x += P_UNITWIDTH;
				uxline++; 
				if (uxline >= 8) {
					uxline = 0;
					x = x_ini;
					y += P_UNITHEIGHT;
				}
			}
		}
	}

	/**
	 * Returns queue to display from model
	 * 
	 * @return 1
	 */
	public int getQueue() {
		return 1;
	}
	
	/**
	 * Draws an arrow at position x,y and dimensions width, height
	 * 
	 * @param arrow		arrow image
	 * @param x			x position
	 * @param y			y position
	 * @param width		arrow width
	 * @param height	arrow height
	 */
	public void drawArrow(BufferedImage arrow, int x, int y, int width, int height) {
		Rectangle rect = new Rectangle(x, y, width, height); 
		g2.setPaint(new TexturePaint(arrow, rect));
		g2.fill(rect);
	}
	
	/**
	 * Maps queue processes to manage popup menu
	 * 
	 * @param rec	rectangle to map
	 * @param value	process pid
	 */
	public void mapProcess(Rectangle rec, int value) {
		map.put(rec, new Integer(value));
	}

	/**
	 * No component is mapped
	 * 
	 * @param rec	unused
	 * @param value	unused
	 */
	public void mapComponent(Rectangle rec, int value) { }
	
	/**
 	 * Returns true when any rectangle representing a queued process contains position (x,y) and false otherwise    
	 * 
	 * @param o rectangle representing a queued process
	 * @param x	x position
	 * @param y	y position
	 * 
	 * @return	queued process contains (x,y) 
	 */
	public boolean contains(Object o, int x, int y){
		Rectangle r = (Rectangle) o;  
		return r.contains(x, y);
	}
	
	/**
	 * Returns "queue" 
	 * 
	 * @return "queue"
	 */
	public String getAlias() {
		return "queue";
	}
}