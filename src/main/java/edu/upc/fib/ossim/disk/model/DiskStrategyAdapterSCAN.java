package edu.upc.fib.ossim.disk.model;

import java.util.Iterator;
import java.util.List;

/**
 * Abstract class that extends DiskStrategyAdapterGeneric and 
 * contains common behaviors of SCAN algorithms
 * 
 * @author Àlex
 */
public abstract class DiskStrategyAdapterSCAN extends DiskStrategyAdapterGeneric  {
	/**
	 * Returns nearest request to head position in the same movement direction.  
	 * 
	 * @param queue	queued requests
	 * @return	next request to serve
	 */
	public DiskBlockRequest getNextRequest(List<DiskBlockRequest> queue) {
		// Next request, C-SCAN moves head independently of requests. Serve request found at headPosition 
		DiskBlockRequest next = null;
		Iterator<DiskBlockRequest> it = queue.iterator();
		
		while (it.hasNext()) {
			next = it.next();
			if (next.getBid() == DiskState.getInstance().getHeadPosition()) return next; 
		}

		return next;
	}
}
