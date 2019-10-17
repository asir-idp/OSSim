package edu.upc.fib.ossim.disk.model;

import java.util.Vector;

/**
 * Singleton class that contains main disk scheduling parameters, 
 * number of sectors and cylinders, current head position, 
 * initial head position, head's movement direction since previous request,
 * current head's displacement and accumulate too  
 * 
 * @author Àlex
 */
public class DiskState {
	private static DiskState instance = null;
	
	private int sectors;
	private int cylinders;
	private int headPosition; 					
	private int initHeadPosition;
	private boolean increment;	//direction increment or decrement cylinders
	private int movement;  
	private int accumulate;
	private Vector<Integer> limits;
		
	private DiskState() { 
		this.increment = true;	//Always starts incrementing cylinders
	}
	
	/**
	 * Public access to Singleton
	 * 
	 * @return instance
	 */
	public static DiskState getInstance() {
		if(instance == null) {
			instance = new DiskState();
		}
		return instance;
	}

	/**
	 * Initialize disk state information 
	 * 
	 * @param sectors 
	 * @param cylinders
	 */
	public void initState(int sectors, int cylinders) {
		this.sectors = sectors; // disk sectors
		this.cylinders = cylinders; // disk cylinders  
		headPosition = 0; 					
		initHeadPosition = 0;
		increment = true;
		movement = 0;
		limits = new Vector<Integer>();
		accumulate = 0;
	}
	
	/**
	 * Gets disk sectors  
	 * 
	 * @return disk sectors
	 */
	public int getSectors() {
		return sectors;
	}

	/**
	 * Gets disk cylinders  
	 * 
	 * @return disk cylinders
	 */
	public int getCylinders() {
		return cylinders;
	}

	/**
	 * Gets head position  
	 * 
	 * @return head position
	 */
	public int getHeadPosition() {
		return headPosition;
	}

	/**
	 * Sets head position
	 * 
	 * @param headPosition head position	
	 */
	public void setHeadPosition(int headPosition) {
		this.headPosition = headPosition;
	}

	/**
	 * Gets initial head position
	 * 
	 * @return	initial head position
	 */
	public int getInitHeadPosition() {
		return initHeadPosition;
	}

	/**
	 * Sets initial head position
	 * 
	 * @param initHeadPosition initial head position	
	 */
	public void setInitHeadPosition(int initHeadPosition) {
		this.initHeadPosition = initHeadPosition;
	}

	/**
	 * true --> head moves up
	 * false --> head moves down
	 * 
	 * @return	head movement direction
	 */
	public boolean isIncrement() {
		return increment;
	}
	
	/**
	 * Sets head direction (true --> up, false --> down)
	 * 
	 * @param increment	head's direction
	 */
	public void setIncrement(boolean increment) {
		this.increment = increment;
	}

	/**
	 * Gets current head movement since previous request
	 * 
	 * @return current head movement since previous request 
	 */
	public int getMovement() {
		return movement;
	}

	/**
	 * Sets last head movement 
	 *  
	 * @param movement  last head movement 
	 */
	public void setMovement(int movement) {
		this.movement = movement;
	}

	/** 
	 * Gets limits reached by head during movement
	 * 
	 * @return limits reached by head during movement
	 * 
	 * @sse 
	 */
	public Vector<Integer> getLimits() {
		return limits;
	}

	/** 
	 * Adds inner limit (sectors*cylinders) reached by head during movement
	 * 
	 */
	public void addInnerLimit() {
		limits.add(sectors*cylinders);
	}

	/** 
	 * Adds outer limit (-1) reached by head during movement
	 * 
	 */
	public void addOuterLimit() {
		limits.add(-1);
	}

	/** 
	 * Initialize limits vector
	 * 
	 * @return limits reached by head during movement
	 */
	public void initLimits() {
		limits = new Vector<Integer>();
	}
	
	/**
	 * Gets accumulate head movement
	 * 
	 * @return accumulate head movement
	 */
	public int getAccumulate() {
		return accumulate;
	}

	/**
	 * Sets accumulate head movement
	 * 
	 * @param accumulate	accumulate head movement
	 */
	public void setAccumulate(int accumulate) {
		this.accumulate = accumulate;
	}
}
