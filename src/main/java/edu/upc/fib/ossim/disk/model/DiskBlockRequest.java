package edu.upc.fib.ossim.disk.model;

import java.awt.Color;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.Translation;

/**
 * Disk block request definition, block are uniquely identified by a block number and are
 * numbered sequentially (Logical block addressing (LBA)). Requests have also a time 
 * when they start to be processed, and information for when they are served: head movement 
 * since previous request and accumulate head displacement            
 * 
 * @author Alex Macia
 * 
 */
public class DiskBlockRequest implements Comparable<DiskBlockRequest>, Cloneable {
	private int bid;
	private int init;	// Initial time
	private int movement; // Displacement since previous request
	private int accumulate; // Accumulate Head Displacement
	private Vector<Integer> limits;
	private Color color;
	
	/**
	 * Constructs a bloc request
	 * 
	 * @param bid	block number
	 * @param init	time
	 * @param color	request color
	 */
	public DiskBlockRequest(int bid, int init, Color color) {
		super();
		this.bid = bid;
		this.init = init;
		this.color = color;
		this.limits = new Vector<Integer>();
	}

	/**
	 * Gets request identifier (block number)
	 * 
	 * @return request identifier
	 */
	public int getBid() {
		return bid;
	}
	
	/**
	 * Gets request time
	 * 
	 * @return	request time
	 */
	public int getInit() {
		return init;
	}

	/**
	 * Gets request movement
	 * 
	 * @return	request movement
	 */
	public int getMovement() {
		return movement;
	}

	/**
	 * Sets head's movement since previous request 
	 * 
	 * @param movement head's movement since previous request
	 */
	public void setMovement(int movement) {
		this.movement = movement;
	}

	/** 
	 * Gets limits reached by head during movement, possible values 0-inner cylinder, 1-outer-cylinder
	 * 
	 * @return limits reached by head during movement
	 */
	public Vector<Integer> getLimits() {
		return limits;
	}

	/** 
	 * Sets limits reached by head during movement, possible values 0-inner cylinder, 1-outer-cylinder
	 * 
	 * @param limits reached by head during movement
	 */
	public void setLimits(Vector<Integer> limits) {
		this.limits = limits;
	}

	/**
	 * Gets request accumulate
	 * 
	 * @return	request accumulate
	 */
	public int getAccumulate() {
		return accumulate;
	}

	/**
	 * Sets accumulate head's movement 
	 * 
	 * @param accumulate head's movement
	 */
	public void setAccumulate(int accumulate) {
		this.accumulate = accumulate;
	}

	/**
	 * Gets request color
	 * 
	 * @return	request color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Returns scheduling information table header: sector, cylinder, time, movement and accumulate  
	 * 
	 * @return	scheduling information table header
	 */
	public static Vector<Object> getHeaderInfo() {
		// Process information table header 
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("dk_30")); // Sector
		header.add(Translation.getInstance().getLabel("dk_34")); // Cylinder
		header.add(Translation.getInstance().getLabel("dk_31")); // init
		header.add(Translation.getInstance().getLabel("dk_32")); // movement
		header.add(Translation.getInstance().getLabel("dk_33")); // accumulate
		return header;
	}
	
	/**
	 * Returns scheduling information table data:  sector, cylinder, time, movement and accumulate.
	 * Sectors background color is request's color 
	 * 
	 * @return	scheduling information table data
	 * 
	 * @see ColorCell
	 */
	public Vector<Object> getBlockInfo(int sectors) {
		// Process information table header 
		Vector<Object> info = new Vector<Object>();
		info.add(new ColorCell(new Integer(bid).toString(), color));
		info.add(new ColorCell(new Integer(bid/sectors).toString(), Color.WHITE));
		info.add(new ColorCell(new Integer(init).toString(), Color.WHITE));
		info.add(new ColorCell(new Integer(movement).toString(), Color.WHITE));
		info.add(new ColorCell(new Integer(accumulate).toString(), Color.WHITE));
		return info;
	}
	
	/**
	 * Returns request xml information, pairs attribute name - attribute value 
	 * 
	 * @return	request xml information
	 */
	public Vector<Vector<String>> getRequestXMLInfo() {
		// Process xml information  
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> attribute;		
		
		attribute = new Vector<String>();
		attribute.add("bid");
		attribute.add(Integer.toString(bid));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("init");
		attribute.add(Integer.toString(init));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("color");
		attribute.add(Integer.toString(color.getRGB()));
		data.add(attribute);
		return data;
	}
	
	/**
	 * Compare this request with p.
	 * Accumulate asc and movement desc determines requests order
	 * 
	 * @return 	comparison result 
	 */
	public int compareTo(DiskBlockRequest p) {
		// Accumulate asc and movement desc determines serving order
		if (accumulate == p.getAccumulate()) return p.getMovement() - movement;
		return accumulate - p.getAccumulate();
	}
		
	/**
	 * Clones this request
	 * 
	 * @return cloned request
	 */
	protected DiskBlockRequest clone() {
		DiskBlockRequest clone = null;
		try {
			clone = (DiskBlockRequest) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    return clone;
	}
}
