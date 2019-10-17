package edu.upc.fib.ossim.process.model;

import java.util.PriorityQueue;

import edu.upc.fib.ossim.utils.Translation;

/**
 * Process Scheduling Strategy implementation for Round Robin algorithm
 * 
 * @author Alex Macia
 */
public class ProcessStrategyRR extends ProcessStrategyAdapterFCFS {
	private int quantum;
	
	public ProcessStrategyRR(int quantum) {
		super();
		this.quantum = quantum;
	}

	/**
	 * Gets round robin algorithm information and its quantum size. It is always preemptive   
	 * 
	 * @param multiprogramming	scheduling multiprogramming state
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo(boolean multiprogramming) {
		if (multiprogramming) return Translation.getInstance().getLabel("pr_23", quantum, "pr_26");
		else return Translation.getInstance().getLabel("pr_23", quantum, "pr_27");
	}

	/**
	 * Increments quantum's executed and looks how much quantum's process time have been already executed, 
	 * if it has spend all its quantum returns process to queue's tail and gets next process (process at queue's top),
	 * otherwise Increments run time to process in the cpu and its quantum and returns current   
	 *  
	 * @param queue			ready queue
	 * @param running_process	current process in the cpu not even completely finished
	 * @return	next process in the cpu
	 */
	public Process forwardTime(PriorityQueue<Process> queue, Process running_process) {
		running_process.addQexecuted(1); // Increments quantum
		
		if (running_process.getQexecuted() >= quantum) {
			running_process.setQexecuted(0);
			addProcess(running_process, queue); // Return process to queue 
			return queue.poll();
		} else {
			return running_process;
		}
	}
}
