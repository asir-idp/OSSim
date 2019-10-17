package edu.upc.fib.ossim.memory.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Vector;

import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Paints Main Memory. Processes are load into memory or released from it, main memory painter 
 * shows memory occupation, internal and external fragmentation at a concrete simulation time .
 * A pop up menu allows remove or swap out processes and address translation  
 * 
 * @author Alex Macia
 */
public class MemoryPainter extends PainterTemplate {
	private static final long serialVersionUID = 1L;
	private static final int M_UNITHEIGTH = 18;
	private static final int ADDR_WIDTH = 30;
	private static final int BORDER = 20;
	public static final Color FRAG_E = Color.pink;
	public static final Color FRAG_I = Color.blue;
	private static final Color EMPTY = Color.white;
	
	/**
	 * Constructs a MemoryPainter, creates the pop up menu and and initialize memory.  
	 * 
	 * @param presenter	event manager
	 * @param menuItems	pop up menu items
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public MemoryPainter(Presenter presenter, Vector<String[]> menuItems, int width, int height) {
		super(presenter, menuItems, width, height);
	}

	/**
	 * Draws memory occupation, a set of colored rectangles represents processes load into memory, 
	 * fragmentation is also highlighted. 
	 * When memory size height exceeds canvas height, memory enlarge and revalidates to perform scroll update
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
		
		// Draw Addresses
		int memHeight = ((MemoryPresenter) presenter).getMemorySize();
		
		// Scroll control height
		//if (M_UNITHEIGTH *  memHeight + 2*BORDER > h) {
			int newHeigth = M_UNITHEIGTH *  memHeight + 2*BORDER;
			setPreferredSize(new Dimension(MemoryPresenter.MEMORY_WIDTH, newHeigth));
			revalidate(); // Updates scroll 
		//}

		
		g2.setColor(Color.LIGHT_GRAY);
		g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		String s = Translation.getInstance().getLabel("me_03");
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D bounds = g2.getFont().getStringBounds(s, frc);
		g.drawString(s,  (int) (w/2 - bounds.getWidth()/2), (int) (BORDER/2 + bounds.getHeight()/2)); // Main memory
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
		
		
		// Fill empty memory
		Functions.getInstance().drawTexture(g2, EMPTY, ADDR_WIDTH, BORDER, w-ADDR_WIDTH-BORDER, M_UNITHEIGTH *  memHeight);  
		
		for (int i = 0; i < memHeight; i++) {
			g.setColor(Color.BLACK);
			g.drawString("@" + i, 2,  i*M_UNITHEIGTH + 8 + BORDER);
		}

		Iterator<Integer> it = presenter.iterator(0);
		int start = 0, height = 0, prog_height = 0, memStart = 0;
		map.clear();
		
		while (it.hasNext()) {
			start = it.next().intValue();
			memStart = start*M_UNITHEIGTH + BORDER;
			height = ((MemoryPresenter) presenter).getMemSize(start)*M_UNITHEIGTH;
			
			if (((MemoryPresenter) presenter).getMemProcessColor(start) != null) {
				// Draw program if exists and internal fragmentation
				prog_height = ((MemoryPresenter) presenter).getMemProcessSize(start)*M_UNITHEIGTH;
				drawProgram(start, ADDR_WIDTH, memStart, w-ADDR_WIDTH-BORDER, prog_height);
				if (prog_height != height) Functions.getInstance().drawTexture(g2, FRAG_I, ADDR_WIDTH, memStart + prog_height, w-ADDR_WIDTH-BORDER, height - prog_height);
			} else {
				// Draw empty block
				Color bground = EMPTY;
				if (((MemoryPresenter) presenter).hasExternalFragmentation()) bground = FRAG_E;
				Functions.getInstance().drawTexture(g2, bground, ADDR_WIDTH, memStart, w-ADDR_WIDTH-BORDER, height);	
			}
			map.put(new Rectangle2D.Double(ADDR_WIDTH,memStart,w-ADDR_WIDTH-BORDER,height), new Integer(start));
		}
	}

	private void drawProgram(int start, int x, int y, int width, int height) {
		Rectangle2D rect = new Rectangle2D.Double(x,y,width,height); // Memory block
		
		// Fill
		g2.setColor(((MemoryPresenter) presenter).getMemProcessColor(start));
		g2.fill(rect);
		
		// Bound
		g2.setColor (Color.BLACK);
		g2.draw(rect);
		
		// Information
		Vector<String> info = ((MemoryPresenter) presenter).getMemProgramInfo(start);
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D bounds;
		int xText = x + 5;
	    int yText = y + 15;
		
		Iterator<String> it = info.iterator();
		while (it.hasNext()) {
			String s = it.next();
			bounds = g2.getFont().getStringBounds(s, frc);
			if (bounds.getWidth() > width) {
				s = s.substring(0, 15);
				s += "...)";
				bounds = g2.getFont().getStringBounds(s, frc);
			}
			g2.setColor(Color.GRAY);
			g2.drawString(s, xText, yText);
			yText += bounds.getHeight();
		}
	}

	/**
 	 * Returns true when any rectangle representing a process contains position (x,y) and false otherwise    
	 * 
	 * @param o rectangle representing a process
	 * @param x	x position
	 * @param y	y position
	 * 
	 * @return	queued process contains (x,y) 
	 */
	public boolean contains(Object o, int x, int y){
		Rectangle2D r = (Rectangle2D) o;  
		return r.contains(x, y);
	}	
	
	/**
	 * Returns "memory" 
	 * 
	 * @return "memory"
	 */
	public String getAlias() {
		return "memory";
	}
}


