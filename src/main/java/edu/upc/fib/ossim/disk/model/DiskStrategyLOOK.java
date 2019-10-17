package edu.upc.fib.ossim.disk.model;

import java.util.Iterator;
import java.util.List;

import edu.upc.fib.ossim.utils.Translation;


public class DiskStrategyLOOK extends DiskStrategyAdapterGeneric {

	/**
	 * Gets LOOK algorithm information  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("dk_28");
	}

	/**
	 * Returns first request at head's cylinder, or nearest request to head position in the same movement direction,
	 * or nearest request to head position in other direction.
	 * LOOK moves ahead until last request, and then scans back
	 * 
	 * @param queue	queued requests
	 * @return	next request to serve
	 */
	public DiskBlockRequest getNextRequest(List<DiskBlockRequest> queue) {
		// Next request, LOOK moves ahead until last request, and then scans back
		DiskBlockRequest nearestOver = null, nearestBelow = null, aux;
		int headCylinder = DiskState.getInstance().getHeadPosition()/DiskState.getInstance().getSectors();
		int auxCylinder, nearestOverCylinder = 0, nearestBelowCylinder = 0;
		
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
				if (nearestBelow == null) {
					nearestBelow = aux;
					nearestBelowCylinder = auxCylinder;
				}
				if (auxCylinder > nearestBelowCylinder) { // nearest
					nearestBelow = aux;
					nearestBelowCylinder = auxCylinder;
				}
			}
		}
		
		// Here, no empty queue and no requests at head's cylinder 
		if (DiskState.getInstance().isIncrement()) {
			if (nearestOver != null) return nearestOver;
			else return nearestBelow;
		} else {
			if (nearestBelow != null) return nearestBelow;
			else return nearestOver;
		}
	}

	/**
	 * While there are requests forward moves head cylinder according to current head's movement direction (next cylinder up or down).
	 * When request is at head's cylinder or there is no request does not move head, otherwise changes movement direction 
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
		
		DiskState.getInstance().setMovement(DiskState.getInstance().getMovement() + 1);
		if (DiskState.getInstance().isIncrement()) {
			if (nextCylinder < headCylinder) {  // Change direction
				DiskState.getInstance().setHeadPosition(headPosition - sectors);
				DiskState.getInstance().setIncrement(false);
			} else  {	// seek in
				DiskState.getInstance().setHeadPosition(headPosition + sectors);
			}
		} else {
			if (nextCylinder > headCylinder) {  // Change direction
				DiskState.getInstance().setHeadPosition(headPosition + sectors);
				DiskState.getInstance().setIncrement(true);
			} else  {	// seek out
				DiskState.getInstance().setHeadPosition(headPosition - sectors);
			}
		}
	}
}
