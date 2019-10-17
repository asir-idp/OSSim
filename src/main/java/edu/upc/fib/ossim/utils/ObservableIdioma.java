package edu.upc.fib.ossim.utils;

import java.util.Observable;

/**
 * Notify language selection updates to observers that may translate labels 
 * 
 * @author Alex Macia
 */
public class ObservableIdioma extends Observable {
	
	/**
	 * Notifies change to all observers
	 * 
	 * @param b	additional information
	 */
	public void notifyObservers(Object b) {
		// Otherwise it won't propagate changes:
		setChanged();
		super.notifyObservers(b);
	}
}

