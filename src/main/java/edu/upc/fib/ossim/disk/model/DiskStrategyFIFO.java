package edu.upc.fib.ossim.disk.model;

import java.util.List;

import edu.upc.fib.ossim.utils.Translation;

/**
 *
 * Disk Management Strategy implementation for FIFO. 
 * FIFO serves the oldest request. 
 *  
 * @author Àlex
 */

public class DiskStrategyFIFO extends DiskStrategyAdapterIFOS {

	/**
	 * Gets FIFO algorithm information  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("dk_23");
	}

	/**
	 * Returns requests at the top of the queue, this algorithm is independent from head values
	 * 
	 * @param queue	queued requests
	 * @return	next request to serve
	 */
	public DiskBlockRequest getNextRequest(List<DiskBlockRequest> queue) {
		// Next request, FIFO attends first IN. Disk state independent
		if (queue.size() > 0) return queue.get(0);
		return null;
	}
}
