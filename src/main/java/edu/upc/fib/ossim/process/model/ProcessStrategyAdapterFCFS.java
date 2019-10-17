package edu.upc.fib.ossim.process.model;

import java.util.PriorityQueue;

/**
 * Abstract class that implements interface ProcessStrategy (FCFS family: FCFS, RR)
 * 
 * @author Àlex
 */
public abstract class ProcessStrategyAdapterFCFS implements ProcessStrategy {
	private static int order = 0;
	
	/**
	 * Nothing to do 
	 * 
	 * @param preemptive unused
	 */
	public void setPreemptive(boolean preemptive) { }
	
	/** 
	 * Adds process directly to queue's tail  
	 * 
	 * @param p		process
	 * @param queue	queue
	 */
	public void addProcess(Process p, PriorityQueue<Process> queue){
   		// FCFS add's process directly to the end. 
		p.setOrder(order);
		order++;
		queue.add(p);
	}
	
	/** 
	 * Removes pold process from ready queue and then adds the new one in the same order 
	 * 
	 * @param pold		old process to remove
	 * @param pold		new process to add 
	 * @param queue	queue
	 */
	public void updProcess(Process pold, Process pnew, PriorityQueue<Process> queue) {
		int oldorder = pold.getOrder();
		pnew.setOrder(oldorder);
		queue.add(pnew);
	}
}
