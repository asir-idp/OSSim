package edu.upc.fib.ossim.disk.model;

import edu.upc.fib.ossim.utils.Translation;

/**
 * 
 * Disk Management Strategy implementation for C-SCAN. 
 * CSCAN moves ahead until the last disk cylinder, and then scans back,
 * head's movement is independent of requests
 * 
 * @author Àlex
 */
public class DiskStrategyCSCAN extends DiskStrategyAdapterSCAN {

	/**
	 * Gets C-SCAN algorithm information  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("dk_27");
	}
	
	/**
	 * Moves head cylinder, always incrementally. When head arrives to platter's end, move to start again 
	 * 
	 * @param next	next request to serve
	 */
	public void moveHeadPosition(DiskBlockRequest next) {
		// Move head around cylinder, then move next cylinder. At disk end's, move head to start position and scans again  
		// Next request independence 
		int sectors = DiskState.getInstance().getSectors();
		int headPosition = DiskState.getInstance().getHeadPosition();
		
		DiskState.getInstance().setHeadPosition(headPosition + sectors);
		if (DiskState.getInstance().getHeadPosition() >= DiskState.getInstance().getSectors()*DiskState.getInstance().getCylinders()) { // Move to start
			DiskState.getInstance().setHeadPosition(DiskState.getInstance().getInitHeadPosition()%DiskState.getInstance().getSectors());
			DiskState.getInstance().addInnerLimit(); // reached inner cylinder
			DiskState.getInstance().addOuterLimit(); // reached outer cylinder
			DiskState.getInstance().setMovement(DiskState.getInstance().getMovement() + DiskState.getInstance().getCylinders() - 1);
		} else {
			DiskState.getInstance().setMovement(DiskState.getInstance().getMovement() + 1);
		}
	}
}
