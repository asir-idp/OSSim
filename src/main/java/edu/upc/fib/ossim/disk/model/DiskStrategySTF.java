package edu.upc.fib.ossim.disk.model;

import java.util.Iterator;
import java.util.List;

import edu.upc.fib.ossim.utils.Translation;


public class DiskStrategySTF extends DiskStrategyAdapterIFOS {

	/**
	 * Gets STF algorithm information  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("dk_25");
	}

	/**
	 * If no currentRequest returns nearest request to head position in any direction.  
	 * Once a request has been selected (currentRequest), returns it since it is served 
	 * 
	 * @param queue	queued requests
	 * @return	next request to serve
	 * 
	 * see super#serveRequest(request)
	 */
	public DiskBlockRequest getNextRequest(List<DiskBlockRequest> queue) {
		if (currentRequest == null) { 
			// Next request, STF attends first nearest requests
			DiskBlockRequest aux;
			int min = 999;
			int headCylinder = DiskState.getInstance().getHeadPosition()/DiskState.getInstance().getSectors();
			Iterator<DiskBlockRequest> it = queue.iterator();
			
			while (it.hasNext()) {
				aux = it.next();
				int auxCylinder = aux.getBid()/DiskState.getInstance().getSectors();
				if (Math.abs(auxCylinder - headCylinder) < min) {
					min = Math.abs(auxCylinder - headCylinder);
					currentRequest = aux; 
				}
			}
		}
		
		return currentRequest;
	}
}
