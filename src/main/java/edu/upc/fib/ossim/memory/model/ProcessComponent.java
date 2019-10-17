package edu.upc.fib.ossim.memory.model;

import java.util.Vector;

/**
 * Process component, instance implementations are cloneable to clone them. 
 * In non contiguous memory management algorithms processes are divided into smaller pieces (components)
 * such as pages or segments, these components has its own identifier and size, moreover
 * some of them may be not initially load into memory.
 *  
 * @author Alex Macia
 * 
 * @see ProcessMemUnit
 * @see ProcessComponent
 * 
 */
public abstract class ProcessComponent implements ProcessMemUnit, Cloneable {
	// Part of a program (Segment or Page)
	protected ProcessComplete parent;
	protected int bid;
	protected int size;
	protected boolean load;

	/**
	 * Constructs a process component
	 * 
	 * @param parent	parent process
	 * @param bid		component identifier	
	 * @param size		component size
	 * @param load		is component initially load into memory?
	 */
	public ProcessComponent(ProcessComplete parent, int bid, int size, boolean load) {
		super();
		this.parent = parent;
		this.bid = bid;
		this.size = size;
		this.load = load;
	}

	/**
	 * Gets component's parent
	 * 
	 * @return	component's parent
	 */
	public ProcessComplete getParent() {
		return parent;
	}
	
	public void setParent(ProcessComplete parent) {
		this.parent = parent;
	}

	/**
	 * Gets component's identifier
	 * 
	 * @return component's identifier
	 */
	public int getBid() {
		return bid;
	}

	/**
	 * Gets parent identifier
	 * 
	 * @return parent identifier
	 */
	public int getPid() {
		return parent.getPid();
	}

	/**
	 * Gets component's size
	 * 
	 * @return component's size
	 */
	public int getSize() {
		return this.size;	// Size of block
	}

	/**
	 * is component initially load into memory?
	 * 
	 * @return	answer
	 */
	public boolean isLoad() {
		return this.load;
	}

	/**
	 * Sets if component is initially load into memory or not 	 
	 * 
	 * @param load	component is initially load into memory or not	
	 */
	public void setLoad(boolean load) {
		this.load = load;
	}

	/**
	 * Is this component a process page.
	 * 
	 * @return this component is a process page
	 */
	public abstract boolean isPage();
	
	/**
	 * Return  null. No component information is needed
	 * 
	 * @return null
	 */
	public abstract Vector<Object> getInfo(); 

	/**
	 * Returns component's xml information, pairs attribute name - attribute value. 
	 * 
	 * @return	component's xml information
	 */
	public Vector<Vector<String>> getXMLInfo() {
		// MemBlock xml information  
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> attribute;		
	
		attribute = new Vector<String>();
		attribute.add("bid");
		attribute.add(Integer.toString(bid));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("size");
		attribute.add(Integer.toString(size));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("load");
		attribute.add(Boolean.toString(load));
		data.add(attribute);
		
		return data;
	}
	
	/**
	 * Clones this component
	 * 
	 * @return cloned component
	 */
	public ProcessComponent clone() {
		ProcessComponent clone = null;
		try {
			clone = (ProcessComponent) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    return clone;
	}
}
