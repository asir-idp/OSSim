package edu.upc.fib.ossim;

import java.awt.Component;

import javax.swing.JPanel;

/**
 * Interface for application's top level container, a frame or an applet for instance. 
 * 
 * @author Alex
 */
public interface OSSim {
	/**
	 * Loads a view into container.  
	 * 
	 * @param view	view to load
	 */
	public void loadView(JPanel view);
	
	/**
	 * Shows a message 	 
	 * 
	 * @param msg	message content
	 */
	public void showMessage(String msg);
	
	/**
	 * Gets component in which dialogs are displayed
	 * 
	 * @return component	
	 */
	public Component getComponent();
	
	/**
	 * Is it possible to open or save simultions?
	 * 
	 * @return it is possible to open or save simultions
	 */
	public boolean allowOpenSave();
}
