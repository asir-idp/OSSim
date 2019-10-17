package edu.upc.fib.ossim.process.model;

import java.util.PriorityQueue;

/**
 * Strategy Interface for process scheduling model (Strategy Pattern).
 * Any process scheduling algorithm must implement this strategy.  
 * 
 * @author Alex Macia
 */
public interface ProcessStrategy {
	/**
	 * Gets algorithm information including main settings values  
	 * 
	 * @param multiprogramming	scheduling multiprogramming state
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo(boolean multiprogramming);
	
	/**
	 * Sets algorithm to be preemptive 
	 * 
	 * @param preemptive is algorithm preemptive
	 */
	public void setPreemptive(boolean preemptive);
		
	/**
	 * Execute running process 1 time unit and returns next process in the cpu
	 *  
	 * @param queue			ready queue
	 * @param running_process	current process in the cpu not even completely finished
	 * @return	next process in the cpu
	 */
	public Process forwardTime(PriorityQueue<Process> queue, Process running_process);
	
	/** 
	 * Adds process to ready queue 
	 * 
	 * @param p		process
	 * @param queue	queue
	 */
	public void addProcess(Process p, PriorityQueue<Process> queue);
	
	/** 
	 * Updates process from ready queue 
	 * 
	 * @param pold		old process to remove
	 * @param new		new process to add 
	 * @param queue	queue
	 */
	public void updProcess(Process pold, Process pnew, PriorityQueue<Process> queue);
}

