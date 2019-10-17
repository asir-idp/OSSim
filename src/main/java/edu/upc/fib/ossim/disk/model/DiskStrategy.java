package edu.upc.fib.ossim.disk.model;

import java.util.List;

/**
 * Strategy Interface for disk scheduling model (Strategy Pattern).
 * Any scheduling algorithm must implement this strategy.  
 * 
 * @author Alex Macia
 */
public interface DiskStrategy {
	
	/**
	 * Gets algorithm information 
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo();
	
	/**
	 * Returns appropriate request according to algorithm and current head position and its movement direction 
	 * 
	 * @param queue	queued requests
	 * @return	next request to serve
	 */
	public DiskBlockRequest getNextRequest(List<DiskBlockRequest> queue);
	
	/**
	 * Moves head seeking request according to algorithm and current head position and direction
	 * 
	 * @param next	next request to serve
	 */
	public void moveHeadPosition(DiskBlockRequest next);
	
	/**
	 * Serves request. Sets its accumulate, movement and limits. Increase DiskState accumulate, and initialize
	 * DiskState movement and limits. 
	 * 
	 * @param request
	 */
	public void serveRequest(DiskBlockRequest request);
}
