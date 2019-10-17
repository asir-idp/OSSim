package edu.upc.fib.ossim.memory.model;

import java.awt.Color;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;

/**
 * Process component in pagination memory management (Page)
 * 
 * @author Àlex
 */
public class ProcessPage extends ProcessComponent {

	/**
	 * Constructs a process page
	 * 
	 * @param parent	parent process
	 * @param bid		component identifier	
	 * @param size		component size
	 * @param load		is component initially load into memory?
	 */
	public ProcessPage(ProcessComplete parent, int bid, int size, boolean load) {
		super(parent, bid, size, load);
	}

	/**
	 * Is this component a process page.
	 * 
	 * @return true
	 */
	public boolean isPage() {
		return true;
	}
	
	/**
	 * Returns component and its parent process information table row, cells are ColorCell instances, 
	 * pid cell background color is process color, other cell are painted in white     
	 * 
	 * @return	process information table data row
	 * 
	 * @see ColorCell
	 * @see ProcessComplete#getInfo()
	 */
	public Vector<Object> getInfo() {
		Vector<Object> info = new Vector<Object>();
		info.addAll(parent.getInfo());
		info.add(1, new ColorCell(new Integer(bid).toString(), Color.WHITE));
		return info;
	}
}
