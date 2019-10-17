package edu.upc.fib.ossim.disk.model;

import java.util.List;

import edu.upc.fib.ossim.utils.Translation;

/**
*
* Disk Management Strategy implementation for LIFO. 
* LIFO serves the newest request. 
*  
* @author Àlex
*/

public class DiskStrategyLIFO extends DiskStrategyAdapterIFOS {

	/**
	 * Gets LIFO algorithm information  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("dk_24");
	}

	/**
	 * If no currentRequest returns last queued request, this algorithm is independent from head values.
	 * Once a request has been selected (currentRequest), returns it since it is served 
	 * 
	 * @param queue	queued requests
	 * @return	next request to serve
	 * 
	 * see super#serveRequest(request)
	 */
	public DiskBlockRequest getNextRequest(List<DiskBlockRequest> queue) {
		// Next request, LIFO attends last IN. Disk state independent
		if (currentRequest == null && queue.size() > 0) {
			currentRequest = queue.get(queue.size() - 1);
		} 
		return currentRequest;
	}
}
