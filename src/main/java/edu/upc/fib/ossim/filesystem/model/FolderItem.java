package edu.upc.fib.ossim.filesystem.model;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Folder definition, besides common file system objects ({@link LogicalFSObject}) folders have 
 * a reference to all file system objects that contain, every folder has always at least two
 * objects, links "." and ".."  
 * 
 * @author Alex Macia
 * 
 */
public class FolderItem extends LogicalFSObject {
	private List<LogicalFSObject> childs;

	/**
	 * Constructs a FolderItem and adds two links "." and "..", 
	 * unique identifier is automatically generated
	 * 
	 * @param fsObject	physical FS object associated with this logical FS Object
	 * @param name		object name		
	 * @param parent	object's parent folder
	 */
	public FolderItem(PhysicalFSObject fsObject, String name, FolderItem parent) {
		super(fsObject, name, parent);
		childs = new LinkedList<LogicalFSObject>(); 
		LinkItem linkSelf, linkParent;
		
		linkSelf = new LinkItem(fsObject, ".", this, this, false); // hard link
		fsObject.addLink(); // Inc link count
		if (parent != null) {  // Root
			linkParent = new LinkItem(parent.getFSObject(), "..", this, parent, false); // hard link
			parent.getFSObject().addLink(); 
		}
		else {
			linkParent = new LinkItem(fsObject, "..", this, this, false); // hard link
			fsObject.addLink(); 
		}
		
		childs.add(linkSelf); // Self link
		childs.add(linkParent); // Parent link
	}

	/**
	 * Constructs a FolderItem and adds two links "." and ".." 
	 * 
	 * @param fsObject	physical FS object associated with this logical FS Object
	 * @param name		object name		
	 * @param parent	object's parent folder
	 */
	public FolderItem(int id, PhysicalFSObject fsObject, String name, FolderItem parent) {
		super(id, fsObject, name, parent);
		childs = new LinkedList<LogicalFSObject>(); 
		LinkItem linkSelf, linkParent;
		
		linkSelf = new LinkItem(id + 1, fsObject, ".", this, this, false); // hard link
		fsObject.addLink(); // Inc link count
		
		if (parent != null) {  // Root
			linkParent = new LinkItem(id + 2, parent.getFSObject(), "..", this, parent, false); // hard link
			parent.getFSObject().addLink(); 
		}
		else {
			linkParent = new LinkItem(id + 2, fsObject, "..", this, this, false); // hard link
			fsObject.addLink(); 
		}
		
		childs.add(linkSelf); // Self link
		childs.add(linkParent); // Parent link
	}

	/**
	 * Returns true
	 * 
	 * @return	true
	 */
	public boolean isFolder() {
		return true;
	}
	
	/**
	 * Returns itself
	 *
	 * @return this
	 */
	public FolderItem getFolder() {
		return this;
	}
	
	/**
	 * Adds a file system object into folder
	 * 
	 * @param child	file system object
	 */
	public void addChild(LogicalFSObject child) {
		childs.add(child);
	}

	/**
	 * Removes a file system object from folder
	 * 
	 * @param child	file system object
	 */
	public void removeChild(LogicalFSObject child) {
		childs.remove(child);
	}

	/**
	 * Removes a set of file system objects from folder
	 * 
	 * @param childs	set of file system object
	 */
	public void removeChild( List<LogicalFSObject>  childs) {
		childs.removeAll(childs);
	}

	/**
	 * Gets all file system object contained into folder
	 * 
	 * @return	all file system object contained into folder
	 */
	public List<LogicalFSObject> getChilds() {
		return childs;
	}

	/**
	 * Gets a file system object contained into folder identified by name
	 * 
	 * @param name file system object name
	 * 
	 * @return	file system object contained into folder identified by name
	 */
	public LogicalFSObject getChildByName(String name) {
		Iterator<LogicalFSObject> it = childs.iterator();
		while (it.hasNext()) {
			LogicalFSObject child = it.next();
			if (child.getName().equals(name)) return child;
		}
		return null;
	}

	/**
	 * Returns green 
	 * 
	 * @return	green
	 */
	public Color getColor() {
		return Color.GREEN;
	}
	
	/**
	 * Returns file xml information, pairs attribute name - attribute value 
	 * 
	 * @return	file xml information
	 */
	public Vector<Vector<String>> getXMLInfo() {
		// Process xml information  
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> attribute;		
		
		data.addAll(super.getXMLInfo());
		
		attribute = new Vector<String>();
		attribute.add("type");
		attribute.add("folder");
		data.add(attribute);
		
		return data;
	}
}
