package edu.upc.fib.ossim.filesystem.model;

import java.awt.Color;
import java.util.Vector;

/**
 * File definition, besides common file system objects ({@link LogicalFSObject}) files have 
 * size and color
 * 
 * @author Alex Macia
 * 
 */
public class FileItem extends LogicalFSObject {
	private int size;
	private Color color;

	/**
	 * Constructs a FileItem, unique identifier is automatically generated
	 * 
	 * @param fsObject	physical FS object associated with this logical FS Object
	 * @param name		file name		
	 * @param parent	file's parent folder
	 * @param size		file size
	 * @param color		file color	
	 */
	public FileItem(PhysicalFSObject fsObject, String name, FolderItem parent, int size, Color color) {
		super(fsObject, name, parent);
		this.size = size;
		this.color = color;
	}

	/**
	 * Constructs a FileItem
	 * 
	 * @param id		file identifier
	 * @param fsObject	physical FS object associated with this logical FS Object
	 * @param name		file name		
	 * @param parent	file's parent folder
	 * @param size		file size
	 * @param color		file color	
	 */
	public FileItem(int id, PhysicalFSObject fsObject, String name, FolderItem parent, int size, Color color) {
		super(id, fsObject, name, parent);
		this.size = size;
		this.color = color;
	}

	/**
	 * Gets file size
	 * 
	 * @return	file size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets file size
	 * 
	 * @param size	file size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Gets file color
	 * 
	 * @return	file color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets file color
	 * 
	 * @param color	file color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Returns true
	 * 
	 * @return	true
	 */
	public boolean isFile() {
		return true;
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
		attribute.add("file");
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("size");
		attribute.add(Integer.toString(size));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("color");
		attribute.add(Integer.toString(color.getRGB()));
		data.add(attribute);
		
		return data;
	}
	
	/**
	 * Gets file data: name, size and color  
	 * 
	 * @return	file data: name, size and color
	 */
	public Vector<Object> getData() {
		// Get Form data 
		Vector<Object> data = super.getData();
		data.add(size);
		data.add(color);
		return data;
	}
}
