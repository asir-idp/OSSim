package edu.upc.fib.ossim.disk.model;

/**
 * Abstract class that extends DiskStrategyAdapterGeneric and 
 * contains common behaviors of FIFO, LIFO and STF algorithms
 * 
 * @author Àlex
 */
public abstract class DiskStrategyAdapterIFOS extends DiskStrategyAdapterGeneric {

	protected DiskBlockRequest currentRequest;

	/** 
	 * Default constructor, initialize parameters
	 */
	public DiskStrategyAdapterIFOS() {
		this.currentRequest = null;
	}

	/**
	 * Moves head cylinder seeking request, if request is at head's cylinder or there is no request does not move head, 
	 * otherwise moves head next cylinders up or down towards request. Increase head movement counter
	 * 
	 * @param next	next request to serve
	 */
	public void moveHeadPosition(DiskBlockRequest next) {
		// Moves head seeking next request or keeps head into cylinder if no request
		if (next == null) return;
		
		int headCylinder = DiskState.getInstance().getHeadPosition()/DiskState.getInstance().getSectors();
		int nextCylinder = next.getBid()/DiskState.getInstance().getSectors();

		if (nextCylinder == headCylinder) return;
		
		DiskState.getInstance().setMovement(DiskState.getInstance().getMovement()+1);
		if (nextCylinder < headCylinder) {  // seek out
			DiskState.getInstance().setHeadPosition(DiskState.getInstance().getHeadPosition()-DiskState.getInstance().getSectors());
		}
		if (nextCylinder > headCylinder) {	// seek in
			DiskState.getInstance().setHeadPosition(DiskState.getInstance().getHeadPosition()+DiskState.getInstance().getSectors());
		}
	}
	
	
	/**
	 * Same as its parent behavior plus initialize currentRequest 
	 * 
	 * @param request
	 * 
	 * see super#serveRequest(request)
	 */
	public void serveRequest(DiskBlockRequest request) {
		super.serveRequest(request);
		currentRequest = null;
	}
}
