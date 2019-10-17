package edu.upc.fib.ossim.process.model;

import java.util.PriorityQueue;

import edu.upc.fib.ossim.utils.Translation;

/**
 * Process Scheduling Strategy implementation for First Come First Served algorithm 
 * 
 * @author Alex Macia
 */
public class ProcessStrategyFCFS extends ProcessStrategyAdapterFCFS {
	/**
	 * Gets first come first served algorithm information, never preemptive   
	 * 
	 * @param multiprogramming	scheduling multiprogramming state
	 * @param preemptive	scheduling preemptive state 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo(boolean multiprogramming) {
		if (multiprogramming) return Translation.getInstance().getLabel("pr_20","pr_26");
		else return Translation.getInstance().getLabel("pr_20", "pr_27");
	}

	/**
	 * Prepares next running process, this algorithm is not preemptive so the same process remains in the cpu   
	 *  
	 * @param queue			ready queue
	 * @param running_process	current process in the cpu not even completely finished
	 * @return	same process in the cpu
	 */
	public Process forwardTime(PriorityQueue<Process> queue, Process running_process) {
		return running_process;	
	}
}
