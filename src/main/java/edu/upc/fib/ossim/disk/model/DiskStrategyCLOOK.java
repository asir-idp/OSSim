package edu.upc.fib.ossim.disk.model;

import java.util.Iterator;
import java.util.List;

import edu.upc.fib.ossim.utils.Translation;


public class DiskStrategyCLOOK extends DiskStrategyAdapterGeneric {

	/**
	 * Gets LOOK algorithm information  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("dk_29");
	}

	/**
	 * Returns first request at head's cylinder, or nearest request to head position in the same movement direction,
	 * or if any, the first request. COOK moves ahead until last request, and then starts again at first requests
	 * 
	 * @param queue	queued requests
	 * @return	next request to serve
	 */
	public DiskBlockRequest getNextRequest(List<DiskBlockRequest> queue) {
		// Next request, LOOK moves ahead until last request, and then scans back
		DiskBlockRequest nearestOver = null, firstRequest = null, aux;
		int headCylinder = DiskState.getInstance().getHeadPosition()/DiskState.getInstance().getSectors();
		int auxCylinder, nearestOverCylinder = 0, firstRequestCylinder = 0;
		
		if (queue.size() == 0) return null;
		
		Iterator<DiskBlockRequest> it = queue.iterator();
		while (it.hasNext()) {
			aux = it.next();
			auxCylinder = aux.getBid()/DiskState.getInstance().getSectors();
			
			if (auxCylinder == headCylinder) return aux;
			
			if (auxCylinder > headCylinder) {
				if (nearestOver == null) {
					nearestOver = aux;
					nearestOverCylinder = auxCylinder;
				}
				if (auxCylinder < nearestOverCylinder) { // nearest
					nearestOver = aux;
					nearestOverCylinder = auxCylinder;
				}
			} else {
				if (firstRequest == null) {
					firstRequest = aux;
					firstRequestCylinder = auxCylinder;
				}
				if (auxCylinder < firstRequestCylinder) { // first
					firstRequest = aux;
					firstRequestCylinder = auxCylinder;
				}
			}
		}
		
		// Here, no empty queue and no requests at head's cylinder 
		if (nearestOver != null) return nearestOver;
		else return firstRequest;
	}

	/**
	 * While there are requests forward moves head cylinder according to current head's movement direction (next cylinder up).
	 * When request is at head's cylinder or there is no request does not move head, otherwise moves head towards first request 
	 *  
	 * @param next	next request to serve
	 */
	public void moveHeadPosition(DiskBlockRequest next) {
		if (next == null) return;
		
		int headCylinder = DiskState.getInstance().getHeadPosition()/DiskState.getInstance().getSectors();
		int nextCylinder = next.getBid()/DiskState.getInstance().getSectors(); 
			
		if (nextCylinder == headCylinder) return;

		int sectors = DiskState.getInstance().getSectors();
		int headPosition = DiskState.getInstance().getHeadPosition();
		
		if (nextCylinder < headCylinder) {  // Moves towards first
			DiskState.getInstance().setHeadPosition(headPosition - sectors * (headCylinder - nextCylinder));
			DiskState.getInstance().setMovement(DiskState.getInstance().getMovement() + (headCylinder - nextCylinder));
		} else  {	// seek in
			DiskState.getInstance().setHeadPosition(headPosition + sectors);
			DiskState.getInstance().setMovement(DiskState.getInstance().getMovement() + 1);
		}
	}
}
