package edu.upc.fib.ossim.memory.model;

import java.util.Vector;

/**
 * Memory Partition definition, partitions starts at a memory address, has size > 0 and
 * may allocate a process. Instance are comparable to sort them, and cloneable to clone them.  
 * 
 * @author Alex Macia
 */
public class MemPartition implements Comparable<MemPartition>, Cloneable {
	private int start;
	private int size;
	private ProcessMemUnit allocated;
	
	/**
	 * Constructs a memory partition
	 * 
	 * @param start	partition start address
	 * @param size	partition size
	 */
	public MemPartition(int start, int size) {
		super();
		this.start = start;
		this.size = size;
	}

	/**
	 * Gets partition start address
	 * 
	 * @return	partition start address
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Sets partition start address
	 * 
	 * @param start	partition start address
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Gets partition size
	 * 
	 * @return	partition size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets partition size
	 * 
	 * @param size	partition size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Gets process allocated into partition
	 * 
	 * @return	process allocated into partition
	 */
	public ProcessMemUnit getAllocated() {
		return allocated;
	}
	
	/**
	 * Allocates a process into partition
	 * 
	 * @param allocated	process to allocate
	 */
	public void setAllocated(ProcessMemUnit allocated) {
		this.allocated = allocated;
	}

	/**
	 * Returns memory partition xml information, pairs attribute name - attribute value 
	 * 
	 * @return	memory partition  xml information
	 */
	public Vector<Vector<String>> getMemPartitionXMLInfo() {
		// MemBlock xml information  
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> attribute;		
		
		attribute = new Vector<String>();
		attribute.add("start");
		attribute.add(Integer.toString(start));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("size");
		attribute.add(Integer.toString(size));
		data.add(attribute);

		return data;
	}
	
	/**
	 * Compare this partition  with p 
	 * 
	 * @return 	comparison result 
	 */
	public int compareTo(MemPartition p) {
		return this.start - p.getStart();
	}
	
	/**
	 * Clones this partition
	 * 
	 * @return cloned partition
	 */
	protected MemPartition clone() {
		MemPartition clone = null;
		try {
			clone = (MemPartition) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    return clone;
	}
}
