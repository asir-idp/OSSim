package edu.upc.fib.ossim.process.model;

import java.util.PriorityQueue;

import edu.upc.fib.ossim.utils.Translation;

/**
 * Abstract class that implements interface ProcessStrategy (Priority family: Priority, SJF)
 * 
 * @author Àlex
 */
public abstract class ProcessStrategyAdapterPrio implements ProcessStrategy {
	protected boolean preemptive;
	
	public ProcessStrategyAdapterPrio(boolean preemptive) {
		super();
		this.preemptive = preemptive;
	}
	
	/**
	 * Sets algorithm to be preemptive 
	 * 
	 * @param preemptive is algorithm preemptive
	 */
	public void setPreemptive(boolean preemptive) {
		this.preemptive = preemptive;
	}

	/**
	 * Gets shortest job first algorithm information and its preemptive state   
	 * 
	 * @param multiprogramming	scheduling multiprogramming state
	 * @param key	label's key
	 * @return	algorithm information
	 */
	protected String getAlgorithmInfo(boolean multiprogramming, String key) {
		if (preemptive) {
			if (multiprogramming) return Translation.getInstance().getLabel(key, "pr_24", "pr_26");
			else return Translation.getInstance().getLabel(key, "pr_24", "pr_27");
		} else {
			if (multiprogramming) return Translation.getInstance().getLabel(key, "pr_25", "pr_26");
			else  return Translation.getInstance().getLabel(key, "pr_25", "pr_27");
		}
	}
	
	/** 
	 * Removes pold process from ready queue and then adds the new one in the proper order 
	 * 
	 * @param pold		old process to remove
	 * @param pold		new process to add 
	 * @param queue	queue
	 */
	public void updProcess(Process pold, Process pnew, PriorityQueue<Process> queue) {
		queue.remove(pold);
		addProcess(pnew, queue);
	}
}
