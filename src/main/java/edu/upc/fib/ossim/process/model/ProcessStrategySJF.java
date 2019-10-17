package edu.upc.fib.ossim.process.model;

import java.util.PriorityQueue;


/**
 * Process Scheduling Strategy implementation for Shortest Job First algorithm
 * 
 * @author Alex Macia
 */
public class ProcessStrategySJF extends ProcessStrategyAdapterPrio {

	public ProcessStrategySJF(boolean preemptive) {
		super(preemptive);
	}

	/**
	 * Gets shortest job first algorithm information and its preemptive state   
	 * 
	 * @param multiprogramming	scheduling multiprogramming state
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo(boolean multiprogramming) {
		return getAlgorithmInfo(multiprogramming, "pr_21");
	}

	/**
	 * Prepares next running process. Detects possible shortest job at head's queue, 
	 * if preemptive is set returns that one, otherwise returns current process in the cpu   
	 *  
	 * @param queue			ready queue
	 * @param running_process	current process in the cpu not even completely finished
	 * @return	next process in the cpu
	 */
	public Process forwardTime(PriorityQueue<Process> queue, Process running_process) {
		//running_process.addRunTime(1);
		Process candidate = queue.peek(); // detect possible shortest job at head's queue
		if (preemptive && candidate != null && candidate.getCurrentBurstDuration() < running_process.getCurrentBurstDuration()) {
			addProcess(running_process, queue); // Queued again
			return queue.poll(); // Swaps running process 
		} else { // keeps going on  
			return running_process;
		}
	}
	
	/** 
	 * Sets process order to its current bursts duration, and adds process in the proper order  
	 * 
	 * @param p		process
	 * @param queue	queue
	 */
	public void addProcess(Process p, PriorityQueue<Process> queue) {
   		// SJF add's process ordered by its current bursts duration. 
		p.setOrder(p.getCurrentBurstDuration());
   		queue.add(p);	
	}
}
