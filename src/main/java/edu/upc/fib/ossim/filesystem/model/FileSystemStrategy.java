package edu.upc.fib.ossim.filesystem.model;

import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.SoSimException;


/**
 * Strategy Interface for file system management model (Strategy Pattern).
 * Any file system management algorithm must implement this strategy.  
 * 
 * @author Alex Macia
 */
public interface FileSystemStrategy {
	/**
	 * Gets algorithm information including main settings values  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo();
	
	/**
	 * Returns if a block identified by num is for OS administration (less than ADMIN_SIZE/blockSize)
	 * 
	 * @return a block identified by num is for OS administration
	 */
	public boolean isAdminBlock(int num);
	
	/**
	 * Gets block size
	 * 
	 * @return block size
	 */
	public int getBlockSize();

	/**
	 * Gets device size
	 * 
	 * @return device size
	 */
	public int getDevSize();
	
	/**
	 * Returns folder's content table header
	 * 
	 * @return	folder's content table header
	 */
    public Vector<Object> getSelectedFolderHeader();
	
	/**
	 * Returns file system object detail information table header
	 * 
	 * @return	file system object detail information table header
	 */
	public Vector<Object> getDetailInfoHeader();
		
	
	/**
	 * Returns inner file system object detail information table header 
	 * 
	 * @return	inner file system object detail information table header
	 */
	public Vector<Object> getInnerDetailInfoHeader();
	
	/**
	 * Returns file system object detail information table data
	 * 
	 * @param device	secondary storage device blocks
	 * @param o			physical file system object
	 * 
	 * @return	file system object detail information table data
	 */
	public Vector<Vector<Object>> getDetailInfoData(Block[] device, PhysicalFSObject o);
	
	
	/**
	 * Returns inner file system object detail information table data. Cells are ColorCell instance 
	 * 
	 * @param device	secondary storage device blocks
	 * @param id		inner file system object identifier
	 * 
	 * @return	inner file system object detail information table data
	 * 
	 * @see ColorCell
	 */
	public Vector<Vector<Object>> getInnerDetailInfoData(Block[] device, int id);
		
	
	/**
	 * Returns secondary storage device occupation table header 
	 * 
	 * @return	secondary storage device occupation table header
	 */
	public Vector<Object> getTableHeaderInfo();
	
	/**
	 * Returns secondary storage device occupation table data 
	 * 
	 * @param device		secondary storage device blocks
	 * 
	 * @return	secondary storage device occupation table data
	 */
	public Vector<Vector<Object>> getTableInfoData(Block[] device);
		
	/**
	 * Returns file system path separator 
	 * 
	 * @return	file system path separator
	 */
	public String getPathSeparator();
	
	/**
	 * Initialize file system's root at start block, and then returns it.
	 * 
	 * @param device		secondary storage device blocks
	 * 
	 * @return	file system's root
	 */
	public LogicalFSObject initRoot(Block[] device);
	
	/**
	 * Returns next available physical file system object 
	 * 
	 * @return	next available physical file system object
	 * 
	 * @throws SoSimException	no free blocks available
	 */
	public PhysicalFSObject getNewPhysicalObject() throws SoSimException;
	
	/**
	 * Returns physical object identified by id
	 * 
	 * @param device		secondary storage device blocks
	 * @param id		 	object identifier
	 * 
	 * @return physical object identified by id
	 * 
	 */ 
	public PhysicalFSObject getPhysicalObject(Block[] device, int id);
	
	/**
	 * Returns if there is enough disk available to allocate data blocks
	 * 
	 * @param device		secondary storage device blocks
	 * @param objectSize	object to allocate size
	 * 
	 * @return	there is enough disk available to allocate data blocks
	 */
	public boolean checkAvailableDisk(Block[] device, int objectSize);

	
	/**
	 * Returns if there is enough disk available to allocate more data blocks
	 * 
	 * @param device			secondary storage device blocks
	 * @param newSize			new object's size
	 * @param logicalFSObject	object to allocate more data blocks 
	 *  
	 * @return	there is enough disk available to allocate more data blocks
	 */
	public boolean checkMoreAvailableDisk(Block[] device, int newSize, LogicalFSObject logicalFSObject);
	
	/**
	 * Allocates a file system object into device, starting at initBlock, then gets other necessary blocks to 
	 * complete object's size requirements.   
	 * 
	 * @param device		secondary storage device blocks
	 * @param logicalFSObject logical File System Object
	 * 
	 * @throws SoSimException	not enough blocks to allocate all object
	 */
	public void allocateObject(Block[] device, LogicalFSObject logicalFSObject) throws SoSimException;
	
	/**
	 * Updates a file system object from device
	 * 
	 * @param object	object to be removed
	 * @param device	secondary storage device blocks
	 * 
	 */
	public void updatesObject(LogicalFSObject object, Block[] device) throws SoSimException;

	/**
	 * Removes a file system object from device, releasing all blocks that stored object
	 * 
	 * @param object	object to be removed
	 * @param device	secondary storage device blocks
	 * 
	 * @return parent folder that contained object 
	 */
	public FolderItem removeObject(LogicalFSObject object, Block[] device);
}
