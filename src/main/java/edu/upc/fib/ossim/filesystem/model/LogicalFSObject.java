package edu.upc.fib.ossim.filesystem.model;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Common attributes and behaviors to serve as a basis for implementing file system objects. 
 * 
 * @author Alex Macia
 * 
 */
public abstract class LogicalFSObject {
	private static int maxid = 1;
	private int id;
	private PhysicalFSObject fsObject; // physical FS object, initial block (Fat entry) or inode
	private String name;
	private FolderItem parent;
	private List<String> path; // List contains path objects in proper order

	/**
	 * Constructs a FileSystemObject, unique identifier is automatically generated
	 * 
	 * @param fsObject	physical FS object associated with this logical FS Object
	 * @param name		object name		
	 * @param parent	object's parent folder
	 * 
	 * @see Block
	 */
	public LogicalFSObject(PhysicalFSObject fsObject, String name, FolderItem parent) {
		if (parent == null) maxid = 1; // Root initialize id
		this.path = new LinkedList<String>();
		this.id = maxid;
		this.fsObject = fsObject;
		this.name = name;
		this.parent = parent;
		if (parent != null) path.addAll(this.getParent().getPath());
		path.add(name);
		maxid++;
	}

	/**
	 * Constructs a FileSystemObject
	 * 
	 * @param id		object identifier
	 * @param fsObject	physical FS object associated with this logical FS Object
	 * @param name		object name		
	 * @param parent	object's parent folder
	 * 
	 * @see Block
	 */
	public LogicalFSObject(int id, PhysicalFSObject fsObject, String name, FolderItem parent) {
		// XML load includes id's information
		this.path = new LinkedList<String>();
		this.id = id;
		this.fsObject = fsObject;
		this.name = name;
		this.parent = parent;
		if (parent != null) path.addAll(this.getParent().getPath());
		path.add(name);
		if (id >= maxid) maxid = id + 1; // Links saved (xml file) are at files end and may not have the higher id's    
	}
	
	/**
	 * Gets object identifier
	 * 
	 * @return	object identifier
	 */
	public int getId() {
		return id;
	}

	/**
	 * By default logical FS objects size is 1
	 * 
	 * @return	1
	 */
	public int getSize() {
		return 1;
	}
	
	/**
	 * Gets physical FS object associated with this logical FS Object
	 *  
	 * @return physical FS object associated with this logical FS Object
	 */
	public PhysicalFSObject getFSObject() {
		return fsObject;
	}

	/**
	 * Gets object name
	 * 
	 * @return	object name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets object name
	 * 
	 * @param name	object name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets object's parent folder
	 * 
	 * @return	object's parent folder
	 */
	public FolderItem getParent() {
		return parent;
	}

	/**
	 * Returns a linked list containing path to object 
	 * 
	 * @return	a linked list containing path to object
	 */
	public List<String> getPath() {
		return path;
	}
	
	/**
	 * Gets object's folder
	 * 
	 * @return	object's folder
	 */
	public FolderItem getFolder() {
		return getParent();
	}

	/**
	 * Returns white, implementations may overwrite this color 
	 * 
	 * @return	white
	 */
	public Color getColor() {
		return Color.WHITE;
	}

	/**
	 * Return false 
	 * 
	 * @return	false
	 * 
	 * @see FileItem#isFile()
	 */
	public boolean isFile() {
		return false;
	}

	/**
	 * Return false 
	 * 
	 * @return	false
	 * 
	 * @see FolderItem#isFolder()
	 */
	public boolean isFolder() {
		return false;
	}

	/**
	 * Return false 
	 * 
	 * @return	false
	 * 
	 * @see LinkItem#isLink()
	 */
	public boolean isLink() {
		return false;
	}

	/**
	 * Returns true
	 * 
	 * @return true
	 */
	public boolean isSoft() {
		return true;
	}
	
	/**
	 * Returns object xml information, pairs attribute name - attribute value 
	 * 
	 * @return	object xml information
	 */
	public Vector<Vector<String>> getXMLInfo() {
		// Process xml information  
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> attribute;		
		
		attribute = new Vector<String>();
		attribute.add("id");
		attribute.add(Integer.toString(id));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("name");
		attribute.add(name);
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("parent");
		attribute.add(Integer.toString(parent.getId()));
		data.add(attribute);
		
		return data;
	}
	
	/**
	 * Gets object data: name  
	 * 
	 * @return	object data: name
	 */
	public Vector<Object> getData() {
		// Get Form data 
		Vector<Object> data = new Vector<Object>();
		data.add(name);
		return data;
	}
}
