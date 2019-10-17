package edu.upc.fib.ossim.filesystem.model;

/**
 * Abstract File System physical object identified by id (a block or i-node)
 * 
 * @author Alex Macia
 * 
 */
public abstract class PhysicalFSObject {
	private int id;

	/**
	 * Constructs a FS physical object
	 * 
	 * @param id	FS physical object identifier
	 */
	public PhysicalFSObject(int id) {
		this.id = id;
	}

	/**
	 * Gets block number
	 * 
	 * @return	block number
	 */
	public int getId() {
		return id;
	}

	/**
	 * Nothing to do. Must redefine
	 */
	public void addLink() { }

	/**
	 * Nothing to do. Must redefine
	 */
	public void removeLink() { }
	
	/**
	 * Return false 
	 * 
	 * @return	false
	 * 
	 * @see Inode#isInode()
	 */
	public boolean isInode() {
		return false;
	}
}
