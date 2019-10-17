package edu.upc.fib.ossim.process.model;

import java.util.PriorityQueue;

/**
 * Process Scheduling Strategy implementation for priority algorithm
 * 
 * @author Alex Macia
 */
public class ProcessStrategyPrio extends ProcessStrategyAdapterPrio {
	public ProcessStrategyPrio(boolean preemptive) {
		super(preemptive);
	}

	/**
	 * Gets priority algorithm information and its preemptive state   
	 * 
	 * @param multiprogramming	scheduling multiprogramming state
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo(boolean multiprogramming) {
		return getAlgorithmInfo(multiprogramming, "pr_22");
	}

	/**
	 * Prepares next running process, detects possible most priority job at head's queue, 
	 * if preemptive is set returns that one, otherwise returns current process in the cpu   
	 *  
	 * @param queue			ready queue
	 * @param running_process	current process in the cpu not even completely finished
	 * @return	next process in the cpu
	 */
	public Process forwardTime(PriorityQueue<Process> queue, Process running_process) {
		Process candidate = queue.peek(); // detects possible most priority job at head's queue
		if (preemptive && candidate != null && candidate.getPrio() > running_process.getPrio()) {
			addProcess(running_process, queue); // Queued again
			return queue.poll(); // Swaps running process 
		} else { // keeps going on  
			return running_process;
		}
	}
	
	/** 
	 * Sets process order to its priority, and adds process in the proper order  
	 * 
	 * @param p		process
	 * @param queue	queue
	 */
	public void addProcess(Process p, PriorityQueue<Process> queue) {
   		// Priority add's process ordered by its priority. 
		p.setOrder(p.getPrio() * -1);
   		queue.add(p);
	}
}
