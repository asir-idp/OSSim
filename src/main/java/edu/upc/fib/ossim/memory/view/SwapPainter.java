package edu.upc.fib.ossim.memory.view;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Vector;

import edu.upc.fib.ossim.template.Presenter;


/**
 * Represents a backing store that keeps swapped out processes. 
 * Processes are laid out horizontally. <br/>
 * A pop up menu allows process delete and swap in again. 
 * SwapPainter drawing differs from QueuePainter in arrows between processes (queue idea) 
 * that are not painted here. 
 * 
 * @author Alex Macia
 * 
 * @see QueuePainter
 */
public class SwapPainter extends QueuePainter {
		
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a SwapPainter, creates the pop up menu.  
	 * 
	 * @param presenter	event manager
	 * @param keytitle 	reference to title
	 * @param menuItems	pop up menu items
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public SwapPainter(Presenter presenter, String keytitle, Vector<String[]> menuItems, int width, int height) {
		super(presenter, keytitle, menuItems, width, height);
	}

	/**
	 * Returns queue to display from model
	 * 
	 * @return 2
	 */
	public int getQueue() {
		return 2;
	}
	
	/**
	 * Draws nothing
	 * 
	 * @param arrow		unused
	 * @param x			unused
	 * @param y			unused
	 * @param width		unused
	 * @param height	unused
	 */
	public void drawArrow(BufferedImage arrow, int x, int y, int width, int height) { }
	
	/**
	 * No process is mapped
	 * 
	 * @param rec	unused
	 * @param value	unused
	 */
	public void mapProcess(Rectangle rec, int value) { }

	/**
	 * Maps swapped components to manage popup menu
	 * 
	 * @param rec	rectangle to map
	 * @param value	component compound id 1000*pid + component seq. number 
	 */
	public void mapComponent(Rectangle rec, int value) {
		map.put(rec, new Integer(value));
	}
	
	/**
	 * Returns "swap" 
	 * 
	 * @return "swap"
	 */
	public String getAlias() {
		return "swap";
	}
}


