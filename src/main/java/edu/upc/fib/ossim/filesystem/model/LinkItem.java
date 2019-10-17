package edu.upc.fib.ossim.filesystem.model;

import java.awt.Color;
import java.util.Vector;

/**
 * Link definition, besides common file system objects ({@link LogicalFSObject}) links have 
 * a reference to other file system objects
 * 
 * @author Alex Macia
 * 
 */
public class LinkItem extends LogicalFSObject {
	private LogicalFSObject target;
	private boolean soft;
	
	/**
	 * Constructs a LinkItem, unique identifier is automatically generated
	 * 
	 * @param fsObject	physical FS object associated with this logical FS Object
	 * @param name		link name		
	 * @param parent	link's parent folder
	 * @param target	file system object referenced	
	 * @param soft		soft link
	 */
	public LinkItem(PhysicalFSObject fsObject, String name, FolderItem parent, LogicalFSObject target, boolean soft) {
		super(fsObject, name, parent);
		this.target = target; // Target 
		this.soft = soft;
	}

	/**
	 * Constructs a LinkItem
	 * 
	 * @param id		link identifier
	 * @param fsObject	physical FS object associated with this logical FS Object
	 * @param name		link name		
	 * @param parent	link parent folder
	 * @param target	file system object referenced	
	 * @param soft		soft link
	 */
	public LinkItem(int id, PhysicalFSObject fsObject, String name, FolderItem parent, LogicalFSObject target, boolean soft) {
		super(id, fsObject, name, parent);
		this.target = target; // Target 
		this.soft = soft;
	}
	
	/**
	 * Returns true
	 * 
	 * @return	true
	 */
	public boolean isLink() {
		return true;
	}

	/**
	 * Gets file system object referenced	
	 * 
	 * @return file system object referenced	
	 */
	public LogicalFSObject getTarget() {
		return target;
	}

	/**
	 * Returns true if it is a soft link
	 * 
	 * @return true if it is a soft link
	 */
	public boolean isSoft() {
		return soft;
	}

	/**
	 * Returns cyan 
	 * 
	 * @return	cyan
	 */
	public Color getColor() {
		return Color.CYAN;
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
		attribute.add("link");
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("target");
		if (target == null) attribute.add("-1");
		else attribute.add(Integer.toString(target.getId()));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("soft");
		attribute.add(Boolean.toString(soft));
		data.add(attribute);
		if (!soft && target != null) {
			// Removed target information
			attribute = new Vector<String>();
			attribute.add("size");
			attribute.add(Integer.toString(target.getSize()));
			data.add(attribute);
			attribute = new Vector<String>();
			attribute.add("color");
			attribute.add(Integer.toString(target.getColor().getRGB()));
			data.add(attribute);
		}
		
		return data;
	}
	
	/**
	 * Gets link data: name and reference id  
	 * 
	 * @return	link data: name and reference id
	 */
	public Vector<Object> getData() {
		// Get Form data 
		Vector<Object> data = super.getData();
		if (target == null) data.add(-1);
		else data.add(target.getId());
		data.add(soft);
		return data;
	}
}
