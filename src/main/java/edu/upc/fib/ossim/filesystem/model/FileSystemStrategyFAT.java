package edu.upc.fib.ossim.filesystem.model;

import java.awt.Color;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;


/**
 * File System Strategy implementation for linked list with file allocation table (FAT) algorithm
 * 
 * @author Alex Macia
 */
public class FileSystemStrategyFAT  extends FileSystemStrategyAdapter {
	private int[] fatTable; 	// Fat Table. -1 indicatesd last block

	/**
	 * Constructs FileSystemStrategyFAT and initialize FAT table, entries => diskSize / blockSize
	 * 
	 * @param blockSize	block size
	 * @param devSize	device size
	 */
	public FileSystemStrategyFAT(int blockSize, int devSize) {
		super(blockSize, devSize);
		this.fatTable = new int[devSize/blockSize];
	}

	/**
	 * Gets FAT algorithm information including main settings values  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("fs_20", blockSize);
	}
	
	/**
	 * Returns folder's content table header
	 * 
	 * @return	folder's content table header
	 * 
	 */
    public Vector<Object> getSelectedFolderHeader() {
    	// Get header.  FolderPainter
    	Vector<Object> header = new Vector<Object>();
    	header.add(""); 
    	header.add(Translation.getInstance().getLabel("fs_33")); //Init Block 
    	return header;
    }
	
	/**
	 * Returns file system object detail information table header: block number and next block
	 *  
	 * @return	file system object detail information table header
	 */
	public Vector<Object> getDetailInfoHeader() {
		// Table Header single item information 
		Vector<Object> header = new Vector<Object>();
    	header.add(Translation.getInstance().getLabel("fs_13"));	// Block 
    	header.add(Translation.getInstance().getLabel("fs_14"));	// Next
    	return header;
	}
	
	/**
	 * Unused  
	 * 
	 * @return	nothing
	 */
	public Vector<Object> getInnerDetailInfoHeader() {
		return null;
	}
	
	/**
	 * Returns file system object detail information table data. Cells are ColorCell instance
	 *  
	 * @param device	secondary storage device blocks
	 * @param o			physical file system object	 
	 *  
	 * @return	file system object detail information table data
	 * 
	 * @see ColorCell
	 */
	public Vector<Vector<Object>> getDetailInfoData(Block[] device, PhysicalFSObject o) {
		// Table Data single item information. block is initBlock of file, folder or link
		Vector<Vector<Object>> data = new  Vector<Vector<Object>>();
    	
		LogicalFSObject item	= ((Block) o).getItem();	
		int next = o.getId();
		
    	do {
    		Vector<Object> row = new Vector<Object>();
    		row.add(new ColorCell( new Integer(next).toString(), item.getColor()));
    		if (fatTable[next] == -1) row.add(new ColorCell(Translation.getInstance().getLabel("fs_47"), item.getColor()));
    		else row.add(new ColorCell( new Integer(fatTable[next]).toString(), item.getColor()));
    		data.add(row);
    		
    		next = fatTable[next];
    	} while (next != -1);
    	
		return data;
	}

	/**
	 * Unused  
	 * 
	 * @return	nothing
	 */
	public Vector<Vector<Object>> getInnerDetailInfoData(Block[] device, int id) {
		return null;
	}
	
	/**
	 * Returns secondary storage device occupation table header: block number, next block and state (free / used)
	 * 
	 * @return	secondary storage device occupation table header
	 */
	public Vector<Object> getTableHeaderInfo() {
		// FAT. Table Header
		Vector<Object> header = new Vector<Object>();
    	header.add(Translation.getInstance().getLabel("fs_13"));
    	header.add(Translation.getInstance().getLabel("fs_14"));
    	header.add(Translation.getInstance().getLabel("fs_15"));
    	return header;
	}
	
	/**
	 * Returns FAT table, starting at initial available block, for each device block, next block indicates
	 * block chaining for file system objects, last object's block is set to nil, and their state is used, 
	 * free blocks has no next block so it is set to 0.  
	 * 
	 * @param device		secondary storage device blocks
	 * 
	 * @return	secondary storage device occupation table data
	 */
	public Vector<Vector<Object>> getTableInfoData(Block[] device) {
		// FAT. Table Data
		Vector<Vector<Object>> data = new  Vector<Vector<Object>>();
		Color color;
    	
		for (int i=getFirstDataBlock(); i<fatTable.length; i++) {
			if (device[i] != null) color = device[i].getItem().getColor();
			else color = Color.WHITE;
			
    		Vector<Object> row = new Vector<Object>();
    		row.add(new ColorCell(new Integer(i).toString(), color));
    		if (fatTable[i] == -1) row.add(new ColorCell(Translation.getInstance().getLabel("fs_47"), color));
    		else row.add(new ColorCell( new Integer(fatTable[i]).toString(), color));
    		if (fatTable[i] != 0) row.add(new ColorCell(Translation.getInstance().getLabel("fs_16"), color));
    		else row.add(new ColorCell(Translation.getInstance().getLabel("fs_17"), color));

    		data.add(row);
		}
		return data;
	}

	/**
	 * Returns "\". Windows path separator 
	 * 
	 * @return	"\"
	 */
	public String getPathSeparator() {
		return "\\";
	}
	
	/**
	 * Initialize file system's root (C:) at start block, and then returns it.
	 * 
	 * @param device		secondary storage device blocks
	 * 
	 * @return	file system's root
	 */
	public LogicalFSObject initRoot(Block[] device) {
        device[getFirstDataBlock()] = new Block(getFirstDataBlock()); // First block
        LogicalFSObject root = new FolderItem(device[getFirstDataBlock()], "C:", null);
        device[getFirstDataBlock()].setItem(root);
        fatTable[getFirstDataBlock()] = -1;
        return root;
	}
	
	/**
	 * Returns next available physical file system object  
	 * 
	 * @return	next available physical file system object
	 *  
	 * @throws SoSimException	no free blocks available
	 */ 
	public PhysicalFSObject getNewPhysicalObject() throws SoSimException {
		// Returns first free Init block from table FAT, and creates device Block
		int i = getFreeBlock();
		return new Block(i);
	}
	
	/**
	 * Returns physical object identified by id
	 * 
	 * @param device		secondary storage device blocks
	 * @param id		 	object identifier
	 * 
	 * @return physical object identified by id
	 * 
	 */ 
	public PhysicalFSObject getPhysicalObject(Block[] device, int id) {
		return device[id];
	}

	
	private int getFreeBlock() throws SoSimException {
		// Returns first free Init block from table FAT, and creates device Block
		for (int i = getFirstDataBlock(); i < fatTable.length; i++) {
			if (fatTable[i] == 0) {
				return i;
			}
		}
		// No blocks available for new items
		throw new SoSimException("fs_01");
	}
	
	/**
	 * Returns if there is enough disk available to allocate data blocks
	 * 
	 * @param device		secondary storage device blocks
	 * @param objectSize	object to allocate size
	 * 
	 * @return	there is enough disk available to allocate data blocks
	 */
	public boolean checkAvailableDisk(Block[] device, int objectSize) {
		int i = getFirstDataBlock();
		
		int dataBlocks = ((objectSize -1)/blockSize) + 1;
		while (i < fatTable.length && dataBlocks > 0) {
			if (fatTable[i] == 0) {
				dataBlocks--;
			}
			i++;
		}
		return dataBlocks == 0;
	}
	
	/**
	 * Returns if there is enough disk available to allocate more data blocks
	 * 
	 * @param device			secondary storage device blocks
	 * @param newSize			new object's size
	 * @param logicalFSObject	object to allocate more data blocks 
	 *  
	 * @return	there is enough disk available to allocate more data blocks
	 */
	public boolean checkMoreAvailableDisk(Block[] device, int newSize, LogicalFSObject logicalFSObject) {
		int objectSize = logicalFSObject.getSize();
		if (objectSize >= newSize) return true;
		return checkAvailableDisk(device, newSize - objectSize);
	}
	
	/**
	 * Allocates a file system object into device, first block is contained into logicalFSObject, its physical FS object associated
	 * is object's first block, then gets other necessary blocks to complete object's size requirements.   
	 * 
	 * @param device		secondary storage device blocks
	 * @param logicalFSObject logical File System Object
	 * 
	 * @throws SoSimException	not enough blocks to allocate all object
	 */
	public void allocateObject(Block[] device, LogicalFSObject logicalFSObject) throws SoSimException {
		// Size indicates number of blocks
		int necessaryBlocks = ((logicalFSObject.getSize() - 1)/blockSize) + 1;
		necessaryBlocks--;
		int previous = logicalFSObject.getFSObject().getId();
		device[previous] = (Block) logicalFSObject.getFSObject();
		device[previous].setItem(logicalFSObject);
		fatTable[previous] = -1;
		
		while (necessaryBlocks > 0) {
			int freeBlock = getFreeBlock();
			device[freeBlock] = new Block(freeBlock);
	    	device[freeBlock].setItem(logicalFSObject); 
	    	fatTable[freeBlock] = -1;
	    	// Update FAT
	    	fatTable[previous] = freeBlock;
	    	previous = freeBlock;
	    	necessaryBlocks--;
		}
	}

	/**
	 * Updates a file system object from device. Removes the object and the reallocates it
	 * 
	 * @param object	object to be removed
	 * @param device	secondary storage device blocks
	 * 
	 * @see #allocateObject(Block[], LogicalFSObject)
	 * @see #removeObject(LogicalFSObject, Block[])
	 */
	public void updatesObject(LogicalFSObject object, Block[] device) throws SoSimException {
		removeObject(object, device); // Remove from device
    	allocateObject(device, object); // Reallocates
	}
	
	/**
	 * Removes a file system object from device, releasing all blocks that stored object
	 * 
	 * @param object	object to be removed
	 * @param device	secondary storage device blocks
	 * 
	 * @return parent folder that contained object 
	 */
	public FolderItem removeObject(LogicalFSObject object, Block[] device) {
		int next = object.getFSObject().getId();
		if (object.isFolder() || 
			(object.isLink() && object.isSoft())) {  // Nothing to do with hard links "." and ".."
			// Remove folder fat entry
			device[next] = null;
			fatTable[next] = 0;
		}
		
		if (object.isFile()) {
			while (next != -1) {
				int aux = next;
				device[aux] = null;
				next = fatTable[aux];
				fatTable[aux] = 0;
			}
		}
		
		return object.getParent();
	}
}
