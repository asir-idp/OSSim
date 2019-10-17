package edu.upc.fib.ossim.disk.model;

/**
 * Abstract class that implements interface DiskStrategy and 
 * contains generic behaviors of all Disk algorithms
 * 
 * @author Àlex
 */
public abstract class DiskStrategyAdapterGeneric implements DiskStrategy {
	/**
	 * Serves request. Sets its accumulate, movement and limits. Increase DiskState accumulate, and initialize
	 * DiskState movement and limits. 
	 * 
	 * @param request
	 */
	public void serveRequest(DiskBlockRequest request) {
		// Head proceed this request. Move to requestServed
    	DiskState.getInstance().setAccumulate(DiskState.getInstance().getAccumulate() + DiskState.getInstance().getMovement());
		request.setMovement(DiskState.getInstance().getMovement());
		request.setLimits(DiskState.getInstance().getLimits());
		request.setAccumulate(DiskState.getInstance().getAccumulate());
		DiskState.getInstance().setMovement(0);	
		DiskState.getInstance().initLimits();
	}
}
