package edu.upc.fib.ossim.filesystem.model;

import java.util.Vector;

/**
 * File System indirect block definition (Unix file system), indirect blocks are special blocks 
 * of pointers that then point to blocks of the file's data (single indirection) or 
 * other indirect blocks (double and triple indirections).
 * 
 * @author Alex Macia
 * 
 */
public class IndirectBlock extends Block {
	public final static int ADDRESS_POINTERS = 20;
	private Vector<Block> pointers; // Points to block or indirect
	
	/**
	 * Constructs an indirect block 
	 * 
	 * @param num	block number (identifier)
	 */
	public IndirectBlock(int num)  {
		super(num);
		pointers = new Vector<Block>(ADDRESS_POINTERS); 
	}
	
	/**
	 * Gets all pointed blocks
	 * 
	 * @return	all pointed blocks
	 */
	public Vector<Block> getPointers() {
		return pointers;
	}
	
	/**
	 * Adds a pointed block  
	 * 
	 * @param block	pointed block 
	 */
	public void addBlock(Block block) {
		pointers.add(block);
	}
	
	/**
	 * Returns "I" as it is shown in device painter to identify indirect blocks 
	 * 
	 * @return	"I"
	 */
	public String getString() {
		return "I";
	}

	/**
	 * Return true 
	 * 
	 * @return	true
	 */
	public boolean isIndirect() {
		return true;
	}
}
