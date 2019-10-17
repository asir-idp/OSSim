package edu.upc.fib.ossim.filesystem.model;

/**
 * Abstract class that implements interface FileSystemStrategy and 
 * contains common behaviors and parameters
 * 
 * @author Àlex
 */
public abstract class FileSystemStrategyAdapter implements FileSystemStrategy {
	protected static final int ADMIN_SIZE = 128; // FAT or Superblock size. Multiple 4
	protected int blockSize;
	protected int devSize;

	/**
	 * FileStrategyAdapter constructor
	 * 
	 * @param blockSize		block size
	 * @param devSize		device size
	 */
	public FileSystemStrategyAdapter(int blockSize, int devSize) {
		super();
		this.blockSize = blockSize;
		this.devSize = devSize;
	}

	/**
	 * Returns first available data block number
	 * 
	 * @return first available data block number
	 */
	public int getFirstDataBlock() {
		return  ADMIN_SIZE/blockSize;
	}

	/**
	 * Returns if a block identified by num is for OS administration (less than ADMIN_SIZE/blockSize)
	 * 
	 * @return a block identified by num is for OS administration
	 */
	public boolean isAdminBlock(int num) {
		return num < getFirstDataBlock();
	}

	/**
	 * Gets block size
	 * 
	 * @return block size
	 */
	public int getBlockSize() {
		return blockSize;
	}

	/**
	 * Gets device size
	 * 
	 * @return device size
	 */
	public int getDevSize() {
		return devSize;
	}
}
