package edu.upc.fib.ossim.filesystem.model;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;


/**
 * File System Management Model (Model - View - Presenter Pattern). Different management algorithm are implemented 
 * through Strategy Pattern. Model manage a maximum of<code>"MAX_ELEMENTS"</code> file system objects.    
 * 
 * @author Alex Macia
 */
public class ContextFileSystem {
	public static final int MAX_ELEMENTS = 40;

	private FileSystemStrategy algorithm;
	private Block[] device; 	// Disk blocks
	private int selectedBlock;	
	private PhysicalFSObject selectedPhysicalObject;	// Folder painter
	private LogicalFSObject selectedObject; // Tree painter
	private LogicalFSObject root;

	/**
	 * Constructs a ContextFileSystem: sets a concrete algorithm strategy,
	 * initialize device and creates file system root 
	 * 
	 * @param algorithm		default algorithm
	 */
    public ContextFileSystem(FileSystemStrategy algorithm) {
    	setAlgorithm(algorithm);
    }
 
    /**
     * Returns file system objects count, total number of files, folders and links (Recursive)
     * 
     * @return	file system objects count
     */
    public int getElementCount() {
		return getElementCount(root);
	}
    
    private int getElementCount(LogicalFSObject node) {
		// Recursive. Returns FileSystemItem identified by id
		int count = 0;
		if (node == null) return 0;
		if (node.isFile() || node.isLink()) return 1;
		FolderItem folder = node.getFolder();
		Iterator<LogicalFSObject> childs = folder.getChilds().iterator();
		while (childs.hasNext()) {
			count += getElementCount(childs.next());
		}
		return count;
	}
    
    /**
     * Change algorithm strategy, initialize device and creates file system root
     * 
	 * @param algorithm		default algorithm
     * 
     */
    public void setAlgorithm(FileSystemStrategy algorithm) {
    	this.algorithm = algorithm;
    	device = new Block[algorithm.getDevSize()/algorithm.getBlockSize()];
    	root = algorithm.initRoot(device);
    	selectedObject = root;
    }

    /**
     * Gets memory size
     * 
     * @return	memory size
     */
	public int getDiskSize() { 
		return algorithm.getDevSize();
	}

	/**
	 * Gets root identifier
	 * 
	 * @return	root identifier
	 */
	public int getRootId() {
		return root.getId();
	}

	/**
	 * Gets initial objects identifiers: root, "." and ".." 
	 * 
	 * @return	initial objects identifiers: root, "." and ".."
	 */
	public int[] getRootIds() {
		int[] ids = new int[3];
		FolderItem froot = root.getFolder();
		ids[0] = root.getId();
		ids[1] = froot.getChildByName(".").getId();
		ids[2] = froot.getChildByName("..").getId();
		return ids;
	}
    
	/**
	 * Returns true if there is any object apart from initial ones, false otherwise   
	 * 
	 * @return	 true if there is any object apart from initial ones, false otherwise
	 */
	public boolean areAnyFile() {
		return root.getFolder().getChilds().size() > 2;
	}
	
	/**
	 * Gets selected object data
	 * 
	 * @return	selected object data
	 * 
	 * @see LogicalFSObject#getData()
	 */
	public Vector<Object> getSelectedData() {
		return selectedObject.getData();
	}

	/**
	 * Selects a logical file system object identified by id
	 * 
	 * @param id	logical file system object identifier
	 * 
	 * @return object exist
	 */
	public boolean setSelectedLogicalObject(int id) {
		selectedObject = getByID(root, id);
		return selectedObject != null;
	}

	/**
	 * Selects a physical file system object identified by id
	 * 
	 * @param id	physical file system object identifier
	 */
	public void setselectedPhysicalObject(int id) {
		selectedPhysicalObject = algorithm.getPhysicalObject(device, id);
	}

	/**
	 * Selects a block identified by id
	 * 
	 * @param id	block number (identifier)
	 */
	public void setselectedBlock(int num) {
		selectedBlock = num;
	}

	/**
	 * Gets a string that represents the path to the selected folder 
	 * 
	 * @return string that represents the path to the selected folder 
	 * 
	 * @see #getPath(List)
	 */
	public String getFolderSelected() {
		return getPath(selectedObject.getFolder().getPath());   
	}
	
	
	public String getLinkTargetInfo() {
		if (selectedObject.isLink()) {
			LogicalFSObject target = ((LinkItem) selectedObject).getTarget();
			if (target == null || getByID(root, target.getId()) == null) {
				return Translation.getInstance().getError("fs_13");
			}
		}
		return null;
	}
	
	/**
	 * Returns true if selected object is a folder, false otherwise
	 * 
	 * @return true if selected object is a folder, false otherwise
	 */
	public boolean isSelectedAFolder() {
		return selectedObject.isFolder();
	}

	/**
	 * Returns true if selected object is a file, false otherwise
	 * 
	 * @return true if selected object is a file, false otherwise
	 */
	public boolean isSelectedAFile() {
		return selectedObject.isFile();
	}

	/**
	 * Returns true if selected object is a link, false otherwise
	 * 
	 * @return true if selected object is a link, false otherwise
	 */
	public boolean isSelectedALink() {
		return selectedObject.isLink();
	}

	/**
	 * Returns folder's content table header
	 * 
	 * @return	folder's content table header
	 * 
	 */
    public Vector<Object> getSelectedFolderHeader() {
    	// Get header.  FolderPainter
    	return algorithm.getSelectedFolderHeader();
    }
	
	/**
	 * Returns folder's content table data. Subfolders first and files and links after. (Ordered by name)
	 * 
	 * 
	 * @return	folder's content table data
	 * 
	 */
    public Vector<Vector<Object>> getSelectedFolderData() {
    	// Get selected folder files. FolderPainter 
    	Vector<Vector<Object>> data = new  Vector<Vector<Object>>();
    	
    	FolderItem folder = selectedObject.getFolder();
    	List<LogicalFSObject> childs = folder.getChilds();
    	Iterator<LogicalFSObject> it = childs.iterator();
    	while (it.hasNext()) {
    		Vector<Object> row = new Vector<Object>();
    		LogicalFSObject item = it.next();
    		row.add(new ColorCell(item.getName(), item.getColor()));
    		row.add(new ColorCell( new Integer(item.getFSObject().getId()).toString() ,item.getColor()));
    		data.add(row);
    	}
    	
		return data;
    }

	private LogicalFSObject getByID(LogicalFSObject node, int id) {
		// Recursive. Returns FileSystemItem identified by id
		if (node == null) return null;
		if (node.getId() == id) return node;
		if (node.isFolder()) {
			FolderItem folder = node.getFolder();
			Iterator<LogicalFSObject> childs = folder.getChilds().iterator();
			LogicalFSObject child = null;
			while (childs.hasNext() && child == null) {
				child = getByID(childs.next(), id);
			}
			return child;
		}
		return null;
	}
	
	/**
	 * Gets file system object's name identified by id (Recursive)
	 * 
	 * @param id	system object identifier
	 * @return	file system object's name identified by id (Recursive)
	 */
	public String getName(int id) {
		return getByID(root, id).getName();
	}

	/**
	 * Returns block background color, it depends on object stored into it. Administrative blocks are painted in black,
	 * free blocks are painted in white, and blocks storing file system objects are painted in object's color    
	 * 
	 * @param block	block identifier
	 * @return	block background color
	 * 
	 * @see LogicalFSObject#getColor()
	 */
	public Color getBlockColor(int block) {
		if (algorithm.isAdminBlock(block)) return Color.BLACK;
		if (device[block] == null) return Color.WHITE; 
		LogicalFSObject item = device[block].getItem();	
		return item.getColor();
	}

	/**
	 * Returns block label, it depends on object stored into it. Administrative blocks and 
	 * free blocks have black labels, blocks storing file system objects have object's label    
	 * 
	 * @param block	block identifier
	 * 
	 * @return	block label
	 * 
	 * @see Block#getString()
	 */
	public String getBlockString(int block) {
		if (algorithm.isAdminBlock(block)) return "";
		if (device[block] == null) return ""; 
		return device[block].getString();	
	}
	
	/**
	 * Gets file system object's color
	 * 
	 * @param id	system object identifier
	 * @return	file system object's color
	 */
	public Color getColor(int id) {
		LogicalFSObject item = getByID(root, id);
		if (item != null && item.isFile()) return item.getColor(); 
		return Color.WHITE;
	}

	/**
	 * Gets file system object's parent id 
	 * 
	 * @param id	system object identifier
	 * @return	file system object's parent id 
	 */
	public int getParentId(int id) {
		return getByID(root, id).getParent().getId();
	}

	/**
	 * Returns all file system objects xml information  (Recursive)
	 * 
	 * @return	all file system objects xml information
	 * 
	 * @see LogicalFSObject#getXMLInfo()
	 */
	public Vector<Vector<Vector<String>>> getXMLDataFileSystemItems() {
		// Return all items except initial structure: root "." ".."
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();
		LogicalFSObject child;
		Iterator<LogicalFSObject> it;
		List<LogicalFSObject> childs = root.getFolder().getChilds();
		LinkedList<LogicalFSObject> links = new LinkedList<LogicalFSObject>();  

		it = childs.iterator();
		while (it.hasNext()) {
			child = it.next();
			if (!".".equals(child.getName()) && !"..".equals(child.getName()))
				data.addAll(getXMLDataFileSystemItems(child, links));
		}

		// Finally add links, to ensure targets exist  
		// A link is only added if its target has been already exported
		while (links.size() > 0) {
			child = links.getFirst();
			
			LogicalFSObject target = ((LinkItem) child).getTarget();
			
			if (!links.contains(target))  {
				data.add(child.getXMLInfo());
				links.remove(child);
			} else {
				links.remove(child);
				links.addLast(child);
			}
		}
		
		return data;
	}

	private Vector<Vector<Vector<String>>> getXMLDataFileSystemItems(LogicalFSObject node, List<LogicalFSObject> links) {
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();
		if (node.isLink()) {
			links.add(node);
		} else {
			data.add(node.getXMLInfo());
			if (node.isFolder()) {
				// Recursive
				List<LogicalFSObject> childs = node.getFolder().getChilds();
				Iterator<LogicalFSObject> it = childs.iterator();
				while (it.hasNext()) {
					LogicalFSObject child = it.next();
					if (!".".equals(child.getName()) && !"..".equals(child.getName())) // structure links not included "." ".."
						data.addAll(getXMLDataFileSystemItems(child, links));
				}
			}
		}
		return data;
	}
	
	/**************************************************************************************************/
	/*************************************  Specific Strategies ***************************************/
	/**************************************************************************************************/
	
	/**
	 * Returns true if selected block is indirect, false otherwise
	 * 
	 * @see Block#isIndirect() 
	 */
	public boolean isSelectedIndirect() {
		if (device[selectedBlock] == null) return false;
		return device[selectedBlock].isIndirect();
	}
	
	/**
	 * Returns file system object detail information table header
	 * 
	 * @return file system object detail information table header
	 * 
	 * @see FileSystemStrategy#getDetailInfoHeader(Block)
	 */
	public Vector<Object> getDetailInfoHeader() {
		// Item information header 
		return algorithm.getDetailInfoHeader();
	}

	/**
	 * Returns inner file system object detail information table header
	 * 
	 * @return inner file system object detail information table header
	 * 
	 * @see FileSystemStrategy#getInnerDetailInfoHeader(Block)
	 */
	public Vector<Object> getInnerDetailInfoHeader() {
		// Item information header 
		return algorithm.getInnerDetailInfoHeader();
	}

	/**
	 * Returns file system object detail information table data
	 * 
	 * @return file system object detail information table data
	 * 
	 * @see FileSystemStrategy#getDetailInfoData(Block)
	 */
	public Vector<Vector<Object>> getDetailInfoData() {
		// Item information data 
		return algorithm.getDetailInfoData(device, selectedPhysicalObject);
	}

	/**
	 * Returns inner file system object detail information table data
	 * 
	 * @return inner file system object detail information table data
	 * 
	 * @see FileSystemStrategy#getInnerDetailInfoData(Block)
	 */
	public Vector<Vector<Object>> getInnerDetailInfoData() {
		// Item information data 
		return algorithm.getInnerDetailInfoData(device, selectedBlock);
	}
	
	/**
	 * Returns file system information table header 
	 * 
	 * @return	file system information table header
	 * 
	 * @see FileSystemStrategy#getTableHeaderInfo()
	 */
	public Vector<Object> getTableHeaderInfo() {
		// General information header 
		return algorithm.getTableHeaderInfo();
	}

	/**
	 * Returns file system information table data depending on current algorithm      
	 * 
	 * @return	file system information table data
	 * 
	 * @see FileSystemStrategy#getTableInfoData(int, Block[])
	 */
	public Vector<Vector<Object>> getTableInfoData() {
		// General information data 
		return algorithm.getTableInfoData(device);
	}
	
	/**
	 * Adds new file under selected folder
	 * 
	 * @param id	(optional) file identifier
	 * @param data	file data: name, size and color
	 * @return	file identifier	
	 * @throws SoSimException	Exists other objects with the same name under selected folder
	 *  or there are not enough blocks to allocate this file
	 * 
	 * @see FileSystemStrategy#getFreeBlock(int, Block[])
	 * @see FileSystemStrategy#allocateObject(int, int, int, Block[])
	 */
	public int addFile(int id, Vector<Object> data) throws SoSimException {
    	// id -1 new.  id >= 0 keep
		FolderItem folder = selectedObject.getFolder();
    	
    	if (folder.getChildByName(data.get(0).toString()) != null) throw new SoSimException("fs_02");
    	if (!algorithm.checkAvailableDisk(device, (Integer) data.get(1))) throw new SoSimException("fs_01"); 
    	
    	PhysicalFSObject fsObject = algorithm.getNewPhysicalObject();
    	
    	FileItem file;
    	if (id < 0) file = new FileItem(fsObject, data.get(0).toString(), folder, (Integer) data.get(1), (Color) data.get(2));
    	else file = new FileItem(id, fsObject, data.get(0).toString(), folder, (Integer) data.get(1), (Color) data.get(2));
    	
    	folder.addChild(file);
    	
    	algorithm.allocateObject(device, file);
    	    	
    	return file.getId();
    }
    
	/**
	 * Adds new subfolder under selected folder. Links "." and ".." are automatically added to new folder
	 * 
	 * @param id	(optional) folder identifier
	 * @param data	file data: name
	 * @return	folder identifier	
	 * @throws SoSimException	Exists other objects with the same name under selected folder
	 *  or there are not enough blocks to allocate this folder
	 * 
	 * @see FileSystemStrategy#getFreeBlock(int, Block[])
	 * @see FileSystemStrategy#allocateObject(int, int, int, Block[])
	 */
    public int[] addFolder(int id, Vector<Object> data) throws SoSimException {
    	// id -1 new.  id >= 0 keep
    	FolderItem folder = selectedObject.getFolder();
    	
    	if (folder.getChildByName(data.get(0).toString()) != null) throw new SoSimException("fs_02");
    	if (!algorithm.checkAvailableDisk(device, 1)) throw new SoSimException("fs_01");
    	
    	PhysicalFSObject fsObject = algorithm.getNewPhysicalObject();
    	
    	FolderItem newFolder;
    	if (id < 0) newFolder = new FolderItem(fsObject, data.get(0).toString(), folder);
    	else newFolder = new FolderItem(id, fsObject, data.get(0).toString(), folder);
    	
    	folder.addChild(newFolder);
    	
    	algorithm.allocateObject(device, newFolder); // Each Folder only 1 block
    	
    	int[] ids = new int[3]; // Folder + "." + ".."
    	ids[0] =  newFolder.getId();
    	ids[1] =  newFolder.getChildByName(".").getId();
    	ids[2] =  newFolder.getChildByName("..").getId();
    	return ids;
    }
    
	/**
	 * Adds new link under selected folder. 
	 * Soft links are associated to a new Physical object
	 * Hard links (UNIX only) are associated to the same Physical object (i-node)  
	 * 
	 * @param id	(optional) link identifier
	 * @param data	file data: name, link reference id, soft?
	 * @return	link identifier	
	 * @throws SoSimException	Exists other objects with the same name under selected folder, 
	 * there are not enough blocks to allocate this link or reference doesn't exist 
	 * 
	 */
     public int addLink(int id, Vector<Object> data)  throws SoSimException {
    	// id -1 new.  id >= 0 keep
    	FolderItem folder = selectedObject.getFolder();
    	
    	if (folder.getChildByName(data.get(0).toString()) != null) throw new SoSimException("fs_02");
    	
    	LinkItem newLink;
    	LogicalFSObject target;
		Integer targetId = (Integer) data.get(1);
		target = getByID(root, targetId);
		if (target == null && id < 0) throw new SoSimException("fs_05");
		
		
		Boolean soft = (Boolean) data.get(2);
		PhysicalFSObject fsObject;
		if (soft) {
			fsObject = algorithm.getNewPhysicalObject();
		} else {
			if (target == null) {
				fsObject = algorithm.getNewPhysicalObject();
				target = new FileItem(fsObject, "", null, (Integer) data.get(3), (Color) data.get(4));
		    	algorithm.allocateObject(device, target);
		    	fsObject.removeLink();
			} else fsObject = target.getFSObject(); // Same block
		}
		
    	if (id < 0) { // New
    		newLink = new LinkItem(fsObject, data.get(0).toString(), folder, target, soft);
    	} else {
    		newLink = new LinkItem(id, fsObject, data.get(0).toString(), folder, target, soft);
    	}
    	
    	algorithm.allocateObject(device, newLink); 
    	
    	folder.addChild(newLink);
   	
    	return newLink.getId();
    }
    
    /**
     * Update a file system object: file, folder or link
     * 
     * @param data	new object's data
     * @return	objects identifier
     * 
     * @throws SoSimException	can not update root or "." ".." links, exists other objects 
     * with the same name under selected folder, there are not enough blocks to allocate this object
     * or reference doesn't exist (links) 
     */
    public int updateFileSystemObject(Vector<Object> data) throws SoSimException {
    	// Update any file system item
    	FolderItem folder;
    	if (selectedObject.isFolder()) folder = selectedObject.getParent();
    	else folder = selectedObject.getFolder();
    	
    	if (selectedObject.getParent() == null) throw new SoSimException("fs_04"); // Can't update root
    	if (".".equals(selectedObject.getName()) ||
    			"..".equals(selectedObject.getName())) throw new SoSimException("fs_03");
    	if (!selectedObject.getName().equals(data.get(0).toString())) {
    		if (folder.getChildByName(data.get(0).toString()) != null) throw new SoSimException("fs_02");
    	}

    	if (selectedObject.isFile()) {
    		int newSize = ((Integer) data.get(1)).intValue();
    		
   			if (!(algorithm.checkMoreAvailableDisk(device, newSize, selectedObject))) throw new SoSimException("fs_01");
    			   			
   			((FileItem) selectedObject).setSize((Integer) data.get(1));
   			
   			algorithm.updatesObject(selectedObject, device); // Updates device
    		
    		((FileItem) selectedObject).setColor((Color) data.get(2));
    	}
    	
    	if (selectedObject.isLink()) {
    		
    		/* Desactivated target modification
    		LogicalFSObject target, oldTarget;
    		Integer targetId = (Integer) data.get(1);
    		target = getByID(root, targetId);
    		if (target.getId() == selectedObject.getId()) throw new SoSimException("fs_12"); // Itself
    		
    		oldTarget = ((LinkItem) selectedObject).getTarget();
    		if (!oldTarget.equals(target)) { // change target
        		int id = selectedObject.getId();
        		oldTarget.removeLink((LinkItem) selectedObject); 
        		selectedObject.getFolder().removeChild(selectedObject);
        		addLink(id, data);
        		selectedObject = this.getByID(root, id);
    		}
    		*/
    	}
    	
    	selectedObject.setName(data.get(0).toString());
    	return selectedObject.getId();
    }
    
    /**
     * Removes selected file system object
     * 
     * @return	vector with removed objects identifiers (object removed and its links)
     * 
     * @throws SoSimException	can not remove root or "." ".." links
     * 
     * @see FileSystemStrategy#removeObject(LogicalFSObject, Block[])
     */
    public Vector<Integer> removeFileSystemObject() throws SoSimException {
    	// Removes any file system item
    	if (selectedObject.getParent() == null) throw new SoSimException("fs_04"); // Can't remove root
    	if (".".equals(selectedObject.getName()) ||
    			"..".equals(selectedObject.getName())) throw new SoSimException("fs_03");
    	
    	Vector<Integer> ids = removeObject(selectedObject);
    	
    	return ids;
    }
    
    // Recursive
    private Vector<Integer> removeObject(LogicalFSObject object) {
    	Vector<Integer> ids = new Vector<Integer>();

    	// First if it is a folder, remove recursively all its child
    	if (object.isFolder()) {
			// Recursive
			FolderItem folder = object.getFolder();
			List<LogicalFSObject> childs = folder.getChilds();
			while (childs.size() > 0) {
				LogicalFSObject child = childs.get(0);
				ids.addAll(removeObject(child));
			}
    	}
    	
    	ids.add(object.getId());
    	FolderItem parent = algorithm.removeObject(object, device);
    	parent.removeChild(object);

    	selectedObject = parent;
    	return ids;
    }
    
    
    /**
     * Gets current algorithm information
     * 
     * @param blockSize	block size
     * 
     * @return current algorithm information
     * 
     * @see FileSystemStrategy#getAlgorithmInfo(int)
     */
    public String getAlgorithmInfo() {
    	return algorithm.getAlgorithmInfo();
    }
    
    /**
     * Builds string path using file system's path separator
     * 
     * @param path	folders linked list  
     * 
     * @return string path using file system's path separator
     * 
     * @see FileSystemStrategy#getPathSeparator()
     */
	public String getPath(List<String> path) {
		// Returns path builded from list 
		String spath = "";
		Iterator<String> it = path.iterator();
		
		while (it.hasNext()) {
			String object = it.next();
			spath += object;
			if (!algorithm.getPathSeparator().equals(object)) spath += algorithm.getPathSeparator();
		}
		
		if (spath.length() == 1) return spath;
		return spath.substring(0, spath.length()-1);
	}
}

