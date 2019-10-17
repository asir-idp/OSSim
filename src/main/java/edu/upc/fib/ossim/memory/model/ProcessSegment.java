package edu.upc.fib.ossim.memory.model;

import java.awt.Color;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Process component in segmentation memory management (Segment).
 * 
 * Segment identifier are mapped as strings: 0-> code, 1-> data, 2-> stack 
 * 
 * @author Àlex
 */
public class ProcessSegment extends ProcessComponent {

	// Segments: code, data, stack. Reference to labels Bundle
	private static final String[] KEYS = {"me_72", "me_73", "me_74"}; 
	

	/**
	 * Constructs a process segment
	 * 
	 * @param parent	parent process
	 * @param bid		component identifier	
	 * @param size		component size
	 * @param load		is component initially load into memory?
	 */
	public ProcessSegment(ProcessComplete parent, int bid, int size, boolean load) {
		super(parent, bid, size, load);
	}

	/**
	 * Gets segment string key translated to current session language
	 * 
	 * @param bid	numeric segment identifier 
	 * @return	segment string key translated to current session language
	 */
	public static String getKey(int bid) {
		return Translation.getInstance().getLabel(KEYS[bid]);
	}

	/**
	 * Is this component a process page.
	 * 
	 * @return false
	 */
	public boolean isPage() {
		return false;
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
		info.add(1, new ColorCell(getKey(bid), Color.WHITE));
		return info;
	}
}
