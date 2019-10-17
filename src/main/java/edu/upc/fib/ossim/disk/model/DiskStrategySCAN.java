package edu.upc.fib.ossim.disk.model;

import edu.upc.fib.ossim.utils.Translation;

/**
 * Disk Management Strategy implementation for SCAN. 
 * CSCAN moves ahead until the last disk cylinder, then moves head to first cylinder and
 * starts again, head's movement is independent of requests
 */ 

public class DiskStrategySCAN extends DiskStrategyAdapterSCAN {

	/**
	 * Gets SCAN algorithm information  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("dk_26");
	}
	
	/**
	 * Moves head cylinder according to current head's movement direction (next cylinder up or down).
	 * When head arrives to a limit changes direction 
	 * 
	 * @param next	next request to serve
	 */
	public void moveHeadPosition(DiskBlockRequest next) {
		// Move head around cylinder, then move next cylinder. At disk end's scans back  
		// Next request independence 
		int sectors = DiskState.getInstance().getSectors();
		int headPosition = DiskState.getInstance().getHeadPosition();
		
		DiskState.getInstance().setMovement(DiskState.getInstance().getMovement() + 1);
		if (DiskState.getInstance().isIncrement()) {
			DiskState.getInstance().setHeadPosition(headPosition + sectors);
			if (DiskState.getInstance().getHeadPosition() >= DiskState.getInstance().getSectors()*DiskState.getInstance().getCylinders()) { // Back
				DiskState.getInstance().addInnerLimit(); // reached inner cylinder
				DiskState.getInstance().setIncrement(false);
				DiskState.getInstance().setHeadPosition(headPosition - sectors);
			}
		}
		else {
			DiskState.getInstance().setHeadPosition(headPosition - sectors);
			if (DiskState.getInstance().getHeadPosition() < 0) { // Back
				DiskState.getInstance().addOuterLimit(); // reached outer cylinder
				DiskState.getInstance().setIncrement(true);
				DiskState.getInstance().setHeadPosition(headPosition + sectors);
			}
		}
	}
}
