package edu.upc.fib.ossim.filesystem.model;

import java.util.Vector;

/**
 * File System inode definition (Unix file system), inode is a structure that contains all logical FS objects 
 * It is structured as follows: general information (links), 12 direct block pointer and 3 indirections (single, double and triple)
 * 
 * @author Alex Macia
 * 
 * @see IndirectBlock
 */
public class Inode extends PhysicalFSObject {
	public final static int DIRECT_BLOCKS = 12;
	private int links;
	private Vector<Block> direct;
	private IndirectBlock indirect_1;
	private IndirectBlock indirect_2;
	private IndirectBlock indirect_3;
	
	/**
	 * Constructs a i-node. 
	 * 
	 * @param id	i-node identifier
	 */
	public Inode(int id) {
		super(id);
		links = 1;
		direct = new Vector<Block>(DIRECT_BLOCKS); 
	}
	
	/**
	 * Gets i-node's links
	 * 
	 * @return	i-node's links
	 */
	public int getLinks() {
		return links;
	}
	
	/**
	 * Gets all direct blocks
	 * 
	 * @return	all direct blocks
	 */
	public Vector<Block> getDirect() {
		return direct;
	}

	/**
	 * Gets single indirect block
	 * 
	 * @return	single indirect block
	 */
	public IndirectBlock getIndirect_1() {
		return indirect_1;
	}

	/**
	 * Gets double indirect block
	 * 
	 * @return	double indirect block
	 */
	public IndirectBlock getIndirect_2() {
		return indirect_2;
	}

	/**
	 * Gets triple indirect block
	 * 
	 * @return	triple indirect block
	 */
	public IndirectBlock getIndirect_3() {
		return indirect_3;
	}

	/**
	 * Sets first indirect block
	 * 
	 * @param indirect_1	first indirect block
	 */
	public void setIndirect_1(IndirectBlock indirect_1) {
		this.indirect_1 = indirect_1;
	}

	/**
	 * Sets double indirect block
	 * 
	 * @param indirect_2	double indirect block
	 */
	public void setIndirect_2(IndirectBlock indirect_2) {
		this.indirect_2 = indirect_2;
	}

	/**
	 * Sets triple indirect block
	 * 
	 * @param indirect_3	triple indirect block
	 */
	public void setIndirect_3(IndirectBlock indirect_3) {
		this.indirect_3 = indirect_3;
	}
	
	/**
	 * Initialize i-node structure, to reuse it  
	 * 
	 */
	public void initialize() {
		direct = new Vector<Block>(DIRECT_BLOCKS);
		indirect_1 = null;
		indirect_2 = null;
		indirect_3 = null;
	}
	/**
	 * Adds a new link to this i-node
	 */
	public void addLink() {
		links++;
	}

	/**
	 * Removes a link from this i-node
	 */
	public void removeLink() {
		links--;
	}

	/**
	 * Adds a direct block 
	 * 
	 * @param block	direct block 
	 */
	public void addDirectBlock(Block block) {
		direct.add(block);
	}
	
	/**
	 * Return true 
	 * 
	 * @return	true
	 */
	public boolean isInode() {
		return true;
	}
}
