package edu.upc.fib.ossim.filesystem.model;

/**
 * File System block definition, block are uniquely identified by a block number and are
 * numbered sequentially, a block may also stores a piece of a file system object
 * 
 * @author Alex Macia
 * 
 */
public class Block extends PhysicalFSObject {
	private LogicalFSObject item; 

	/**
	 * Constructs a block
	 * 
	 * @param num	block number (identifier)
	 */
	public Block(int num) {
		super(num);
	}

	/**
	 * Gets stored file system object   
	 * 
	 * @return stored file system object
	 */
	public LogicalFSObject getItem() {
		return item;
	}

	/**
	 * Sets stored file system object 
	 * 
	 * @param item stored file system object
	 */
	public void setItem(LogicalFSObject item) {
		this.item = item;
	}
	
	/**
	 * Returns no string.
	 * 
	 * @return	""
	 * 
	 * @see Inode#getString()
	 */
	public String getString() {
		return "";
	}

	/**
	 * Return false 
	 * 
	 * @return	false
	 * 
	 * @see IndirectBlock#isIndirect()
	 */
	public boolean isIndirect() {
		return false;
	}
}

