package edu.upc.fib.ossim.filesystem.model;

import java.awt.Color;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;


public class FileSystemStrategyUNIX extends FileSystemStrategyAdapter {
	private static final int MAX_INODES = 128;
	private Inode[] inodesTable; 	// Inodes Table.
	
	/**
	 * Constructs FileSystemStrategyUNIX and initialize i-nodes table, 128 entries
	 * 
	 * @param blockSize	block size
	 * @param devSize	device size
	 */
	public FileSystemStrategyUNIX(int blockSize, int devSize) {
		super(blockSize, devSize);
		this.inodesTable = new Inode[MAX_INODES];
	}
	
	/**
	 * Gets UNIX algorithm information including main settings values  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("fs_21", blockSize);
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
    	header.add(Translation.getInstance().getLabel("fs_64")); // i-node
    	return header;
    }
    
	/**
	 * Returns i-node's detail information table header 
	 * 
	 * @return	file system object detail information table header
	 */
	public Vector<Object> getDetailInfoHeader() {
		// Table Header single item information. I-node or indirection
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("fs_18"));  // Inode's field
		header.add(Translation.getInstance().getLabel("fs_13"));  // Block
    	return header;
	}
	
	/**
	 * Returns Indirect blocks detail information table header 
	 * 
	 * @return	file system object detail information table header
	 */
	public Vector<Object> getInnerDetailInfoHeader() {
		// Table Header single item information. I-node or indirection
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("fs_19")); // Indirection
		header.add(Translation.getInstance().getLabel("fs_13")); // block
		header.add(Translation.getInstance().getLabel("fs_63")); // Type
    	return header;
	}
	
	
	/**
	 * Returns block information table data, block is a inode or an indirect block, 
	 * and this method returns all its block information. Cells are ColorCell instance 
	 * 
	 * @param device	secondary storage device blocks
	 * @param o			physical file system object
	 * 
	 * @return	file system object detail information table data
	 * 
	 * @see ColorCell
	 */
	public Vector<Vector<Object>> getDetailInfoData(Block[] device, PhysicalFSObject o) {
		// Table Data single item information. I-node
		return getInodeInfoData((Inode) o, Color.WHITE);	
	}

	/**
	 * Returns indirect block information table data. Cells are ColorCell instance 
	 * 
	 * @param device	secondary storage device blocks
	 * @param id		Indirect block number
	 * 
	 * @return	file system object detail information table data
	 * 
	 * @see ColorCell
	 */
	public Vector<Vector<Object>> getInnerDetailInfoData(Block[] device, int id) {
		// Table Data single item information. I-node
		Block block = device[id];
		LogicalFSObject item	= block.getItem();	
		return getIndirectInfoData((IndirectBlock) block, item.getColor());	
	}
	
	private Vector<Vector<Object>> getInodeInfoData(Inode inode, Color color) {
		Vector<Vector<Object>> data = new  Vector<Vector<Object>>();
		Vector<Object> row = new Vector<Object>();

		// Info
		row.add(new ColorCell(Translation.getInstance().getLabel("fs_60"), color));
		row.add(new ColorCell(new Integer(inode.getLinks()).toString(),color));
		data.add(row);
		
		// Direct blocks
		Vector<Block> direct = inode.getDirect();
		for (int i=0; i<Inode.DIRECT_BLOCKS; i++) {
			row = new Vector<Object>();
			row.add(new ColorCell(Translation.getInstance().getLabel("fs_61") + " " + i, color));
			if (direct.size() > i) row.add(new ColorCell(new Integer(direct.get(i).getId()).toString(), color));
			else row.add(new ColorCell(Translation.getInstance().getLabel("fs_47"), color)); // nil
			data.add(row);
		}
		
		// Indirect blocks
		row = new Vector<Object>();
		row.add(new ColorCell("1-" + Translation.getInstance().getLabel("fs_62"), color));
		if (inode.getIndirect_1() != null) row.add(new ColorCell(new Integer(inode.getIndirect_1().getId()).toString(), color));
		else row.add(new ColorCell(Translation.getInstance().getLabel("fs_47"), color)); // nil
		data.add(row);
		
		row = new Vector<Object>();
		row.add(new ColorCell("2-" + Translation.getInstance().getLabel("fs_62"), color));
		if (inode.getIndirect_2() != null) row.add(new ColorCell(new Integer(inode.getIndirect_2().getId()).toString(), color));
		else row.add(new ColorCell(Translation.getInstance().getLabel("fs_47"), color)); // nil
		data.add(row);
		
		row = new Vector<Object>();
		row.add(new ColorCell("3-" + Translation.getInstance().getLabel("fs_62"), color));
		if (inode.getIndirect_3() != null) row.add(new ColorCell(new Integer(inode.getIndirect_3().getId()).toString(), color));
		else row.add(new ColorCell(Translation.getInstance().getLabel("fs_47"), color)); // nil
		data.add(row);
		
		return data;
	}

	private Vector<Vector<Object>> getIndirectInfoData(IndirectBlock indirect, Color color) {
		// Indirection, block, type (block or indirection)
		
		Vector<Vector<Object>> data = new  Vector<Vector<Object>>();
		Vector<Object> row = null;
		
		// Indirections
		Vector<Block> indirections = indirect.getPointers();
		for (int i=0; i<indirections.size(); i++) {
			row = new Vector<Object>();
			Block block = indirections.get(i);
			row.add(new ColorCell(new Integer(i).toString(), color));
			if (block != null) {
				row.add(new ColorCell(new Integer(block.getId()).toString(), color));
				if (block.isIndirect()) row.add(new ColorCell(Translation.getInstance().getLabel("fs_19"), color)); // Indirect
				else row.add(new ColorCell(Translation.getInstance().getLabel("fs_13"), color)); // Block
			} else {
				row.add(new ColorCell(Translation.getInstance().getLabel("fs_47"), color)); // nil
				row.add(new ColorCell(Translation.getInstance().getLabel("fs_47"), color)); // nil
			}
			data.add(row);
		}
		
		return data;
	}
	
	/**
	 * Returns secondary storage device occupation table header: block number and type 
	 * (data block, i-node, indirect block)
	 * 
	 * @return	secondary storage device occupation table header
	 */
	public Vector<Object> getTableHeaderInfo() {
		// Disk blocks table. Table Header
		Vector<Object> header = new Vector<Object>();
    	header.add(Translation.getInstance().getLabel("fs_13")); // block
    	header.add(Translation.getInstance().getLabel("fs_63")); // type  (data, indirection)
    	return header;
	}
	
	/**
	 * Returns block's table, for each device block, possible types are: data block, i-node, indirect block
	 * 
	 * @param device		secondary storage device blocks
	 * 
	 * @return	secondary storage device occupation table data
	 */
	public Vector<Vector<Object>> getTableInfoData(Block[] device) {
		// Disk blocks table. Table Data
		Vector<Vector<Object>> data = new  Vector<Vector<Object>>();
		Color color;
    	
		for (int i=getFirstDataBlock(); i<device.length; i++) {
			if (device[i] != null) color = device[i].getItem().getColor();
			else color = Color.WHITE;
			
    		Vector<Object> row = new Vector<Object>();
    		row.add(new ColorCell(new Integer(i).toString(), color));
    		
    		if (device[i] != null) {
    			//if (device[i].isInode()) row.add(new ColorCell(Translation.getInstance().getLabel("fs_64"), color));
    			if (device[i].isIndirect()) row.add(new ColorCell(Translation.getInstance().getLabel("fs_62"), color));
    			if (!device[i].isIndirect() && !device[i].isInode())  row.add(new ColorCell(Translation.getInstance().getLabel("fs_61"), color));
    		} else {
    			row.add(new ColorCell("", color));
    		}
    		data.add(row);
		}
		return data;
	}

	/**
	 * Returns "/". Unix path separator 
	 * 
	 * @return	"/"
	 */
	public String getPathSeparator() {
		return "/";
	}
	
	/**
	 * Initialize file system's root (/) i-node 2, data at start block, and then returns it.
	 * 
	 * @param device		secondary storage device blocks
	 * @return	file system's root
	 * 
	 * @throws SoSimException  
	 */
	public LogicalFSObject initRoot(Block[] device) {
		PhysicalFSObject inode;
		FolderItem root = null;
		
		try {
			inode = getNewPhysicalObject();
			root = new FolderItem(inode, "/", null);
			allocateObject(device, root); // Each Folder only 1 block
	       
		} catch (SoSimException e) {
			System.out.println("error initRoot a FileSystemStrategyUNIX");
			e.printStackTrace();
		}  
		return root;
	}
	
	/**
	 * Returns next available physical file system object, i-node from i-node's table
	 * 
	 * @return	next available physical file system object
	 *  
	 * @throws SoSimException	no free i-nodes available
	 */ 
	public PhysicalFSObject getNewPhysicalObject() throws SoSimException {
		// Returns first free Init block from device, and creates device inode
		for (int i = 0; i < inodesTable.length; i++) {
			if (inodesTable[i] == null) {
				inodesTable[i] = new Inode(i);
				return inodesTable[i];
			}
		}
		// No blocks available for new items
		throw new SoSimException("fs_06");
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
		return inodesTable[id];
	}

	
	/**
	 * Returns first free block from device, sequentially search into device starting at start block
	 * 
	 * @param device		secondary storage device blocks
	 * 
	 * @return	first free block from device
	 * 
	 * @throws SoSimException	no free blocks available
	 */
	private int getFreeBlock(Block[] device) throws SoSimException {
		// Returns first free Init block from device, and creates device inode
		for (int i = getFirstDataBlock(); i < device.length; i++) {
			if (device[i] == null) return i;
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
	public boolean checkAvailableDisk(Block[] device,  int objectSize) {
		int i = getFirstDataBlock();
		int totalBlocks = getBlocksNeeded(objectSize);
		
		while (i < device.length && totalBlocks > 0) {
			if (device[i] == null) {
				totalBlocks--;
			}
			i++;
		}
		
		return totalBlocks == 0;
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
		if (logicalFSObject.getSize() >= newSize) return true;

		int i = getFirstDataBlock();
		int totalBlocks = getBlocksNeeded(newSize);
		
		while (i < device.length && totalBlocks > 0) {
			if (device[i] == null || logicalFSObject.equals(device[i].getItem())) {
				totalBlocks--;
			}
			i++;
		}
		
		return totalBlocks == 0;
	}
	
	private int getBlocksNeeded(int objectSize) {
		int dataBlocks = ((objectSize - 1)/blockSize) + 1;

		int limitDirect = Inode.DIRECT_BLOCKS;
		int limit1Indirect = limitDirect + IndirectBlock.ADDRESS_POINTERS;
		int limit2Indirect = limit1Indirect + IndirectBlock.ADDRESS_POINTERS * IndirectBlock.ADDRESS_POINTERS;
		int limit3Indirect = limit2Indirect + IndirectBlock.ADDRESS_POINTERS * IndirectBlock.ADDRESS_POINTERS * IndirectBlock.ADDRESS_POINTERS;
		
		if (dataBlocks <= limitDirect) return dataBlocks;
		if (dataBlocks > limitDirect && dataBlocks <= limit1Indirect) return dataBlocks + 1;
		if (dataBlocks > limit1Indirect && dataBlocks <= limit2Indirect) return dataBlocks + 3 + (((dataBlocks-limit1Indirect-1)/IndirectBlock.ADDRESS_POINTERS));
		if (dataBlocks > limit2Indirect && dataBlocks <= limit3Indirect) return dataBlocks + 5 + IndirectBlock.ADDRESS_POINTERS + (((dataBlocks-limit2Indirect-1)/IndirectBlock.ADDRESS_POINTERS)) + (((dataBlocks-limit2Indirect-1)/(IndirectBlock.ADDRESS_POINTERS*IndirectBlock.ADDRESS_POINTERS)));
		return -1; // otherwise
	} 
	
	/**
	 * Allocates a file system object into device, starting at initBlock, then gets other necessary blocks to 
	 * complete object's size requirements.   
	 * 
	 * @param device		secondary storage device blocks
	 * @param logicalFSObject logical File System Object
	 *  
	 * @throws SoSimException	not enough blocks to allocate all object
	 */
	public void allocateObject(Block[] device, LogicalFSObject logicalFSObject) throws SoSimException {
		// Size indicates number of blocks
		Inode inode = (Inode) logicalFSObject.getFSObject();
		
		if (logicalFSObject.isLink() && !logicalFSObject.isSoft()) inode.addLink(); // Inc link count
		else { 
			int necessaryBlocks = ((logicalFSObject.getSize() - 1)/blockSize) + 1;
			necessaryBlocks = allocateDirect(inode, necessaryBlocks, device, logicalFSObject);

			if (necessaryBlocks > 0) {
				// Allocate indirect 1 blocks		
				int freeBlock;
				freeBlock = getFreeBlock(device);
				IndirectBlock indirect = new IndirectBlock(freeBlock);
				inode.setIndirect_1(indirect);
				device[freeBlock] = indirect;
				device[freeBlock].setItem(logicalFSObject); 
				necessaryBlocks = addIndirectBlocks(indirect, necessaryBlocks, device, logicalFSObject);
			}

			if (necessaryBlocks > 0) {
				int freeBlock;
				freeBlock = getFreeBlock(device);
				IndirectBlock indirect2 = new IndirectBlock(freeBlock);
				inode.setIndirect_2(indirect2);
				device[freeBlock] = indirect2;
				device[freeBlock].setItem(logicalFSObject); 
				necessaryBlocks = allocateIndirect2(indirect2, necessaryBlocks, device, logicalFSObject);
			}

			if (necessaryBlocks > 0) {
				int freeBlock;
				freeBlock = getFreeBlock(device);
				IndirectBlock indirect3 = new IndirectBlock(freeBlock);
				inode.setIndirect_3(indirect3);
				device[freeBlock] = indirect3;
				device[freeBlock].setItem(logicalFSObject); 

				necessaryBlocks = allocateIndirect3(indirect3, necessaryBlocks, device, logicalFSObject);
			}
		}
	}
	
	private int allocateDirect(Inode inode, int necessaryBlocks, Block[] device, LogicalFSObject logicalFSObject) throws SoSimException {
		// Returns necessary blocks remaining
		// Allocate direct blocks		
		int freeBlock;
		int directs = Inode.DIRECT_BLOCKS;
		while (directs > 0 && necessaryBlocks > 0) {
			freeBlock = getFreeBlock(device);
			device[freeBlock] = new Block(freeBlock);
			device[freeBlock].setItem(logicalFSObject); 
			inode.addDirectBlock(device[freeBlock]);
			directs--;
			necessaryBlocks--;
		}
		return necessaryBlocks;
	}
		
	
	private int addIndirectBlocks(IndirectBlock indirect, int necessaryBlocks, Block[] device, LogicalFSObject logicalFSObject) throws SoSimException {
		// indirect point blocks  
		
		int freeBlock;
		int indirects = IndirectBlock.ADDRESS_POINTERS; // indirection 1
		
		while (indirects > 0 && necessaryBlocks > 0) {
			freeBlock = getFreeBlock(device);
			device[freeBlock] = new Block(freeBlock);
			device[freeBlock].setItem(logicalFSObject); 
			indirect.addBlock(device[freeBlock]);
			indirects--;
			necessaryBlocks--;
		}
		
		return necessaryBlocks;
	}
	
	private int allocateIndirect2(IndirectBlock indirect2, int necessaryBlocks, Block[] device, LogicalFSObject logicalFSObject) throws SoSimException {
		// Returns necessary blocks remaining
		// Allocate indirect 2 blocks		
		int freeBlock;
		int indirects2 = IndirectBlock.ADDRESS_POINTERS; // indirection 2
		
		while (indirects2 > 0 && necessaryBlocks > 0) {
			freeBlock = getFreeBlock(device);
			IndirectBlock indirect = new IndirectBlock(freeBlock);
			device[freeBlock] = indirect;
			device[freeBlock].setItem(logicalFSObject); // Same Item as inode
			indirect2.addBlock(indirect);
			
			necessaryBlocks = addIndirectBlocks(indirect, necessaryBlocks, device, logicalFSObject);

			indirects2--;
		}
		return necessaryBlocks;
	}

	private int allocateIndirect3(IndirectBlock indirect3, int necessaryBlocks, Block[] device, LogicalFSObject logicalFSObject) throws SoSimException {
		// Returns necessary blocks remaining
		// Allocate indirect 2 blocks		
		int freeBlock;
		int indirects3 = IndirectBlock.ADDRESS_POINTERS; // indirection 3
		while (indirects3 > 0 && necessaryBlocks > 0) {
			freeBlock = getFreeBlock(device);
			IndirectBlock indirect2 = new IndirectBlock(freeBlock);
			device[freeBlock] = indirect2;
			device[freeBlock].setItem(logicalFSObject); 
			indirect3.addBlock(indirect2);
			
			necessaryBlocks = allocateIndirect2(indirect2, necessaryBlocks, device, logicalFSObject);
			
			indirects3--;
		}
		return necessaryBlocks;
	}		
	
	/**
	 * Updates a file system object from device. Removes the object and the reallocates it. Keeps object's inode
	 * 
	 * @param object	object to be removed
	 * @param device	secondary storage device blocks
	 * 
	 * @see #allocateObject(Block[], LogicalFSObject)
	 * @see #removeObject(LogicalFSObject, Block[])
	 */
	public void updatesObject(LogicalFSObject object, Block[] device) throws SoSimException {
		removeObject(object, device); // Remove from device
		Inode inode = (Inode) object.getFSObject();
		inode.initialize();
		inodesTable[inode.getId()] = inode;  // keeps inode
    	allocateObject(device, object); // Reallocates
	}
	
	/**
	 * Removes a file system object from device, releasing all blocks that stored object, 
	 * decrease i-nodes link count and releases i-node if count reaches 0.  
	 * 
	 * @param object	object to be removed
	 * @param device	secondary storage device blocks
	 * 
	 * @return parent folder that contained object 
	 */
	public FolderItem removeObject(LogicalFSObject object, Block[] device) {
		Inode inode = (Inode) object.getFSObject();
		inode.removeLink();
		
		if (inode.getLinks() <= 0) {
			if (object.isFolder()) {
				// Remove inode and folder data block
				Block direct = inode.getDirect().get(0);
				device[direct.getId()] = null;
			}
			
			if (object.isFile()) { // Removes all blocks related with object
				for (int i = getFirstDataBlock(); i < device.length; i++) {
					if (device[i] != null && device[i].getItem().equals(object)) {
						device[i] = null;
					}
				}
			}
			
			if (object.isLink() && !object.isSoft()) { // Removes all blocks related with object
				for (int i = getFirstDataBlock(); i < device.length; i++) {
					if (device[i] != null && device[i].getItem().getFSObject().equals(inode)) {
						device[i] = null;
					}
				}
			}
			
			if (object.isLink() && object.isSoft()) {
				// Soft link
				Block direct = inode.getDirect().get(0);
				device[direct.getId()] = null;
			}
	
			inodesTable[inode.getId()] = null;
		}
		
		return object.getParent();
	}
}
