package edu.upc.fib.ossim.process.model;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Process Scheduling Model (Model - View - Presenter Pattern). Different scheduling algorithm are implemented 
 * through Strategy Pattern. Model manage a maximum of <code>"MAX_PROCESSES"</code> processes, 
 * maximum among ready queue and incoming queue.    
 * 
 * @author Alex Macia
 */
public class ContextProcess {
	public static final int MAX_PROCESSES = 20;
	private ProcessStrategy algorithm;
	// Separate queue's cause different orders   
	private PriorityQueue<Process> readyQueue; 		// Process on ready queue, variable order
	private PriorityQueue<Process> arrivingQueue; 	// Process not already arrived, ordered by init time
	private PriorityQueue<Process> ioQueue; 		// Process blocked doing IO operations
	private List<Process> doneQueue; 				// Process ended
	private PriorityQueue<Process> rqBkup; 		// Ready queue backup to restore initial state  
	private PriorityQueue<Process> aqBkup; 		// Not already arrived queue backup to restore initial state
	private Process runningProcess;				// Running process
	private Process selectedProcess;
	private int idle;

	/**
	 * Constructs a ContextProcess: sets a concrete algorithm strategy  
	 * 
	 * @param algorithm		default algorithm
	 */
    public ContextProcess(ProcessStrategy algorithm) {
        this.algorithm = algorithm;
        readyQueue = new PriorityQueue<Process>();
        arrivingQueue = new PriorityQueue<Process>();
        ioQueue = new PriorityQueue<Process>();
        doneQueue = new LinkedList<Process>();
        rqBkup = new PriorityQueue<Process>();
        aqBkup = new PriorityQueue<Process>();
        idle = 0;
    }
 
    /**
     * Returns processes count, processes in the ready queue and processes in the incoming queue 
     * 
     * @return	processes count
     */
    public int getProcessCount() {
		return arrivingQueue.size() + readyQueue.size();
	}
    
    /**
     * Change algorithm strategy and restores initial state (time = 0)    
     * 
     * @param algorithm 	new algorithm
     */
    public void setAlgorithm(ProcessStrategy algorithm){
    	this.algorithm = algorithm;
    	idle = 0;
    	backup(); // Rebuild requests due to adding processes depends on algorithm  
    	readyQueue.clear();
    	
    	// Can't rely on Iterators from PriorityQueue 
		Object[] pq = rqBkup.toArray();
		// Restores creation order (pid)
		for(int i=0;i<pq.length;i++) {
			Process p = (Process) pq[i];
			p.setOrder(p.getPid());
		}
		Arrays.sort(pq);
		for(int i=0;i<pq.length;i++) algorithm.addProcess(((Process) pq[i]).clone(), readyQueue);
    }

    /**
     * Gets selected process identifier
     * 
     * @return	process identifier
     */
	public int getSelectedProcessId() {
		return selectedProcess.getPid();
	}

	/**
	 * Gets selected process data: id, name, priority, initial time, duration, 
	 * periodic (true when duration = -1, false otherwise), bursts, and color
	 * 
	 * @return	selected process data
	 */
	public Vector<Object> getSelectedProcessData() {
		Vector<Object> data = new Vector<Object>();
		data.add(selectedProcess.getPid());
		data.add(selectedProcess.getName());
		data.add(selectedProcess.getPrio());
		data.add(selectedProcess.getTimesubmission());
		data.add(selectedProcess.isPeriodic());
		data.add(selectedProcess.getBurstsCycle()); // bursts
		data.add(selectedProcess.getColor());
		return data;
	}

	/**
	 * Selects a process identified by pid
	 * 
	 * @param pid	process identifier
	 * 
	 * @return process exist
	 */
	public boolean setSelectedProcess(int pid) {
		selectedProcess = getByPID(pid);
		return selectedProcess != null;
	}

	/**
	 * Returns current process in thwe cpu identifier or 0 if no process is running  
	 * 
	 * @return	running process identifier
	 */
    public int getPIDrunning() {
		// Returns running process's PID or null if not exists
		if (runningProcess != null) return runningProcess.getPid();
		else return 0;
	}
    
    /**
     * Returns sorted list iterator with ready processes identifiers, list order depends on current algorithm 
     * 
     * @return	Sorted list iterator
     */
    public Iterator<Integer> iteratorReady() {
		// Returns LinkedList with ready processes pid's
		return iterator(readyQueue);
	}

    private Iterator<Integer> iterator(PriorityQueue<Process> queue) {
		// Returns ordered iterator from queue 
		LinkedList<Integer> queueInteger = new LinkedList<Integer>();
		
		// Can't rely on Iterators from PriorityQueue 
		Object[] pq = queue.toArray();   
		Arrays.sort(pq);
		for(int i=0;i<pq.length;i++) queueInteger.add(new Integer(((Process) pq[i]).getPid()));
		
		return queueInteger.iterator();
	}
    
	private Process getByPID(int pid) {
		// Returns process's PID which processes is running queued and PID = pid or null if not exists
		Iterator<Process> it = readyQueue.iterator();
		Iterator<Process> ita = arrivingQueue.iterator();
		Iterator<Process> itio = ioQueue.iterator();
		Process p;
		
		if (getPIDrunning() == pid) return runningProcess; 
		else {
			while (it.hasNext()) {
				p = it.next();
				if (p.getPid() == pid) return p;
			}
			while (ita.hasNext()) {
				p = ita.next();
				if (p.getPid() == pid) return p;
			}
			while (itio.hasNext()) {
				p = itio.next();
				if (p.getPid() == pid) return p;
			}
		}
		return null;
	}

	/**
	 * Gets process information as shown in ready queue: pid, name and priority  
	 * 
	 * @param time	current time
	 * @param pid	process identifier
	 * @return	process information
	 */
	public Vector<String> getInfo(int pid) {
		Vector<String> info = new Vector<String>();
		Process p = getByPID(pid);
		
		info.add("PID " + pid);
		info.add(p.getName());
		info.add("prio " + p.getPrio());
		if (p.isPeriodic()) info.add("\u221e");
		
		return info;
	}

	/**
	 * Gets process name
	 * 
	 * @param pid	process identifier
	 * @return	process name
	 */
	public String getPname(int pid) {
		return getByPID(pid).getName();
	}

	/**
	 * Gets process color
	 * 
	 * @param pid	process identifier
	 * @return	process color
	 */
	public Color getColor(int pid) {
		return getByPID(pid).getColor();
	}

	/**
	 * Gets process submission time
	 * 
	 * @param pid	process identifier
	 * @return	process submission time
	 */
	public int getTimesubmission(int pid) {
		return getByPID(pid).getTimesubmission();
	}

	/**
	 * Is a process cpu burst? 
	 * 
	 * @param pid	process identifier
	 * @param i 	process cpu burst cycle moment
	 * @return	process cpu burst cycle moment is cpu 
	 */
	public boolean isCPUBurst(int pid, int i) {
		return getByPID(pid).getBurstsCycle().get(i) == 0;
	}
	
	/**
	 * Gets process size
	 * 
	 * @param pid	process identifier
	 * @return	process size
	 */
	public int getSize(int pid) {
		return getByPID(pid).getBurstsCycle().size();
	}

	/**
	 * Gets process priority
	 * 
	 * @param pid	process identifier
	 * @return	process priority
	 */
	public int getPprio(int pid) {
		return getByPID(pid).getPrio();
	}
	
	/**
	 * Gets process current burst moment
	 * 
	 * @param pid	process identifier
	 * @return	process current burst moment
	 */
	public int getCurrent(int pid) {
		return getByPID(pid).getCurrentBurst();
	}

	/**
	 * Returns a unique process identifier  
	 * 
	 * @return	unique process identifier
	 */
	public int getMaxpid() {
		return Process.getMaxpid();
	} 

	/**
	 * Returns process creation CPU I/O bursts table header
	 * 
	 * @return	process creation CPU I/O bursts table header
	 * 
	 */
	public Vector<Object> getFormTableHeader() {
		// Form program Header 
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("pr_70")); //burst
		header.add(Translation.getInstance().getLabel("pr_71")); //CPU
		header.add(Translation.getInstance().getLabel("pr_72")); //I/O
		return header;
	}
	
	/**
	 * Returns arriving queue table header 
	 * 
	 * @return	 arriving queue table header 
	 */
	public Vector<Object> getArrivingHeaderInfo() {
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("pr_30")); // PID
		header.add(Translation.getInstance().getLabel("pr_31")); // Name
		header.add(Translation.getInstance().getLabel("pr_37")); // Submission
		return header;
	}
	
	/**
	 * Returns incoming processes (Arriving queue) table data order by time to arrive  (process submission time): PID, name and time left to submission
	 * 
	 * @param time			current simulation time
	 * 
	 * @return	incoming processes information table data
	 */
	public Vector<Vector<Object>> getArrivingInfoData(int time) {
		// Arriving processes information data 
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> row = null;
		int pid = 0;

		// Arriving queue
		Iterator<Integer> it = iterator(arrivingQueue);
		while (it.hasNext()) {
			pid = it.next().intValue();
			row = new Vector<Object>();
			row.add(new ColorCell(new Integer(pid).toString(),getByPID(pid).getColor()));
			row.add(new ColorCell(getByPID(pid).getName(), Color.WHITE));
			row.add(new ColorCell(new Integer(getByPID(pid).getTimesubmission() - time).toString(), Color.WHITE));
			data.add(row);
		}
		
		if (data.size() == 0) return null;
		return data;
	}
	
	/**
	 * Returns blocked processes doing i/o operations table header 
	 * 
	 * @return	 blocked processes doing i/o operations table header 
	 */
	public Vector<Object> getIOHeaderInfo() {
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("pr_30")); // PID
		header.add(Translation.getInstance().getLabel("pr_31")); // Name
		header.add(Translation.getInstance().getLabel("pr_09")); // Time left
		return header;
	}
	
	/**
	 * Returns blocked processes doing i/o operations table data order by time left: PID, name and time left to end IO burst
	 * 
	 * @return	blocked processes doing i/o operations table data
	 */
	public Vector<Vector<Object>> getIOInfoData() {
		// Arriving processes information data 
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> row = null;
		int pid = 0;

		// Arriving queue
		Iterator<Integer> it = iterator(ioQueue);
		while (it.hasNext()) {
			pid = it.next().intValue();
			row = new Vector<Object>();
			row.add(new ColorCell(new Integer(pid).toString(),getByPID(pid).getColor()));
			row.add(new ColorCell(getByPID(pid).getName(), Color.WHITE));
			row.add(new ColorCell(new Integer(getByPID(pid).getCurrentBurstDuration()).toString(), Color.WHITE));
			data.add(row);
		}
		
		if (data.size() == 0) return null;
		return data;
	}
	
	
	/**
	 * Returns process information table header 
	 * 
	 * @return	process information table header
	 */
	public Vector<Object> getTableHeaderInfo() {
		return Process.getTableHeaderInfo();
	}
	
	/**
	 * Returns all processes information table data in an appropriate order: 
	 * finished, process in the cpu, ready queue, blocked doing io operations and arriving processes.       
	 * 
	 * @param time			current simulation time
	 * 
	 * @return	process information table data
	 * 
	 * @see Process#getProcessTableInfo()
	 */
	public Vector<Vector<Object>> getTableInfoData(int time) {
		// General information data 
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> row;
		int pid;

		// Finished processes
		Iterator<Process> it = doneQueue.iterator();
		while (it.hasNext()) {
			row = it.next().getProcessTableInfo(time);
			data.add(row);
		}

		// Running 
		if (runningProcess != null) {
			row = runningProcess.getProcessTableInfo(time);
			data.add(row);
		}
		
		// Ready queue
		Iterator<Integer> itr = iteratorReady();
		while (itr.hasNext()) {
			pid = itr.next().intValue();
			row = getByPID(pid).getProcessTableInfo(time);
			data.add(row);
		}
		
		// IO queue
		Iterator<Integer> itio = iterator(ioQueue);
		while (itio.hasNext()) {
			pid = itio.next().intValue();
			row = getByPID(pid).getProcessTableInfo(time);
			data.add(row);
		}
		
		// Arriving queue
		Iterator<Integer> ita = iterator(arrivingQueue);
		while (ita.hasNext()) {
			pid = ita.next().intValue();
			row = getByPID(pid).getProcessTableInfo(time);
			data.add(row);
		}
		
		if (data.size() == 0) return null;
		return data;
	}
	
	/**
	 * Returns statistical processes scheduling information:
	 * Efficiency, Throughput, average Turnaround time, average Waiting time and average Response Time
	 * 	
	 * @return statistical processes scheduling information
	 */
	public Vector<Vector<String>> getTableStatsInfo() {
		// Initial Statistics Information windows  
		Vector<Vector<String>> stats = new Vector<Vector<String>>();
		Vector<String> row;
		
		// Efficiency: # temps ocupat / (temps ocupat + temps ociòs)
		row = new Vector<String>();
		row.add("pr_68");
		row.add("--");
		stats.add(row);

		// Throughput: # process / unit of time
		row = new Vector<String>();
		row.add("pr_60");
		row.add("--");
		stats.add(row);
		
		// Avg Turnaround time: Avg Interval from the time of submission (incoming) to the time of completion. 
		row = new Vector<String>();
		row.add("pr_61");
		row.add("--");
		stats.add(row);
		
		// Avg Waiting time: Avg Amount of time that a process spends waiting in the ready queue
		row = new Vector<String>();
		row.add("pr_62");
		row.add("--");
		stats.add(row);
		
		// Avg Response Time: Avg Interval from the time of submission (incoming) until the first response is produced
		row = new Vector<String>();
		row.add("pr_63");
		row.add("--");
		stats.add(row);

		return stats;
	}
	
	/**
	 * Returns statistical processes scheduling information at a concrete simulation time:
	 * Throughput, average Turnaround time, average Waiting time and average Response Time
	 * 	
	 * @param time			current simulation time
	 *  
	 * @return statistical processes scheduling information
	 */
	public Vector<String> getTableStatsData(int time) {
		// Statistics Information windows  
		Vector<String> values = new Vector<String>();
		NumberFormat formatter = new DecimalFormat("#0.00");

		// Efficiency: # temps ocupat / (temps ocupat + temps ociòs)
		if (time == 0) values.add("--");
		else values.add(formatter.format((double) (time - idle)/ (double) time));		

		// Throughput: # process / unit of time
		if (time == 0) values.add("--");
		else values.add(formatter.format((double) doneQueue.size()/ (double) time));		
		
		// Avg Turnaround time: Avg Interval from the time of submission (incoming) to the time of completion. 
		if (time == 0) values.add("--");
		else values.add(formatter.format(avgTurnaroundTime()));
		
		// Avg Waiting time: Avg Amount of time that a process spends waiting in the ready queue
		if (time == 0) values.add("--");
		else values.add(formatter.format(avgWaitingTime()));
		
		// Avg Response Time: Avg Interval from the time of submission (incoming) until the first response is produced
		if (time == 0) values.add("--");
		else values.add(formatter.format(avgResponseTime()));

		return values;
	}
	
	private double avgTurnaroundTime() {
		if (doneQueue.size() == 0) return 0;
		Iterator<Process> it = doneQueue.iterator();
		Process p;
		double turnaround = 0;
		while (it.hasNext()) {
			p = it.next();
			turnaround += p.getTimecompletion() - p.getTimesubmission();
		}
		return turnaround/(double) doneQueue.size();
	}
	
	private double avgWaitingTime() {
		if (doneQueue.size() == 0) return 0;
		Iterator<Process> it = doneQueue.iterator();
		Process p;
		double waiting = 0;
		while (it.hasNext()) {
			p = it.next();
			waiting += p.getWaiting();
		}
		return waiting/(double) doneQueue.size();
	}
	
	private double avgResponseTime() {
		if (doneQueue.size() == 0) return 0;
		Iterator<Process> it = doneQueue.iterator();
		Process p;
		double response = 0;
		while (it.hasNext()) {
			p = it.next();
			response += p.getTimeresponse();
		}
		return response/(double) doneQueue.size();
	}

	/**
	 * Returns process xml information from ready queue processes 
	 * 
	 * @return	process xml information from ready queue processes
	 * 
	 * @see Process#getProcessXMLInfo() 
	 */
	public Vector<Vector<Vector<String>>> getXMLDataReady() {
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();
		Iterator<Process> it = readyQueue.iterator();
		while (it.hasNext()) {
			data.add(it.next().getProcessXMLInfo());
		}
		return data;
	}
	
	/**
	 * Returns process xml information from arriving queue processes 
	 * 
	 * @return	process xml information from arriving queue processes
	 * 
	 * @see Process#getProcessXMLInfo() 
	 */
	public  Vector<Vector<Vector<String>>> getXMLDataArriving() {
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();
		Iterator<Process> it = arrivingQueue.iterator();
		while (it.hasNext()) {
			data.add(it.next().getProcessXMLInfo());
		}
		return data;
	}
	
	/**************************************************************************************************/
	/*************************************  Specific Strategies ***************************************/
	/**************************************************************************************************/
	
	/**
	 * Adds a new process to an appropriate queue depending on time, that process becomes current selected process   
	 *  
	 * @param data	process data: pid, name, priority, submission time, periodic, burst cycle and color
	 * @param time	simulation time
	 */
    @SuppressWarnings("unchecked")
	public void addProcess(Vector<Object> data, int time) {
    	// Add process p to its queue depending on actual time and initial process time
    	// Common behavior
    	Process p = new Process(new Integer((String) data.get(0)).intValue(), (String) data.get(1), (Integer) data.get(2), (Integer) data.get(3), (Boolean) data.get(4), (Vector<Integer>) data.get(6), (Color) data.get(5));

    	if (p.getTimesubmission() > time) {
    		// At arriving Queue init time determines order
    		p.setOrder(p.getTimesubmission());
    		arrivingQueue.add(p);
    	} else {
    	// Specific behavior
    		algorithm.addProcess(p, readyQueue);
    	}
    	selectedProcess = p;
    }
    
    /**
	 * Updates an existing process    
	 *  
	 * @param data	process data: pid, name, priority, submission time, periodic, burst cycle and color
	 * @param time	simulation time
	 */
    @SuppressWarnings("unchecked")
	public void updProcess(Vector<Object> data, int time) {
    	// Common behavior
    	Process p = new Process(new Integer((String) data.get(0)).intValue(), (String) data.get(1), (Integer) data.get(2), (Integer) data.get(3), (Boolean) data.get(4), (Vector<Integer>) data.get(6), (Color) data.get(5));
    	
    	removeProcess();
    	if (p.getTimesubmission() > time) {
    		// At arriving Queue init time determines order
    		p.setOrder(p.getTimesubmission());
    		arrivingQueue.add(p);
    	} else {
    	// Specific behavior. Upd a process from arriving queue or a process at ready queue
    		if (selectedProcess.getTimesubmission() > time)  algorithm.addProcess(p, readyQueue);
    		else algorithm.updProcess(selectedProcess, p, readyQueue);
    	}
    	selectedProcess = p;
    }

    /**
     * Removes selected process from its queue 
     * 
     * @param time
     */
    public void removeProcess() {
    	// Removes process p from its queue
    	if (readyQueue.contains(selectedProcess)) readyQueue.remove(selectedProcess);
    	else arrivingQueue.remove(selectedProcess); 
    }
	
    /**
     * Gets current algorithm information
     * 
     * @param multiprogramming
     * 
     * @return current algorithm information
     */
    public String getAlgorithmInfo(boolean multiprogramming) {
    	return algorithm.getAlgorithmInfo(multiprogramming);
    }

    /**
	 * @see ProcessStrategy#setPreemptive(boolean)
	 */
	public void setPreemptive(boolean preemptive) {
		algorithm.setPreemptive(preemptive);
	}
    
    /**
     * Forwards simulation time 1 unit. Common tasks such as initial (time 0) state back up, increment all processes 
     * waiting time in the ready queue, look at arriving queue to move processes to the ready queue and 
     * move finished processes to the finished queue are implemented here, algorithm concrete tasks such as 
     * execute running process 1 time unit are implemented into concrete strategies.  Returns true when simulation ends 
     * (no more processes in any queue)
     * 
     * @param time			current simulation time
     * @param multiprogram  scheduling is multiprogramming 
     * @param preemptive	scheduling is preemptive
     * @param quantum		scheduling quantum size
     * 
     * @return simulation ends 
     * 
     * @see ProcessStrategy
     */
    public boolean forwardTime(int time, boolean multiprogram, boolean preemptive, int quantum) {
    	// Common behavior
    	if (time == 0) {
    		if (runningProcess == null) { // avoid pause before 0 seconds
    			if (readyQueue.isEmpty() && arrivingQueue.isEmpty()) return true;    			
    			backup(); // backup to restore initial state
    			runningProcess = readyQueue.poll();
    		}
    	} else {
			incrementWaiting(); // Increments waiting time at ready queue
			queueArriving(time); // Add arriving processes to ready queue
    		// Increments running process runtime and prepares next  
    		if (runningProcess == null) {
    			idle++;
    			if (readyQueue.isEmpty() && arrivingQueue.isEmpty() && ioQueue.isEmpty()) return true;
    			
    			checkIO(multiprogram);	// move back IO blocked process to ready queue. 
    			
    			// Multiprogramming always try to get next ready while io operations, 
    			// monoprogramming continues until io finish.
    			if (multiprogram || (ioQueue.isEmpty() && runningProcess == null))  runningProcess = readyQueue.poll();
    		} else {
    			checkIO(true);	// Increments IO times. 
    			runningProcess.incCPU();
    			runningProcess.incCurrent();
				    			
 				if (!runningProcess.isPeriodic() && runningProcess.getCurrent() >= runningProcess.getBurstsCycle().size()) {
					runningProcess.setTimecompletion(time);
					doneQueue.add(runningProcess); // Finished, get next
					if (readyQueue.isEmpty() && arrivingQueue.isEmpty() && ioQueue.isEmpty()) {
						runningProcess = null;
						return true;
					}
					Process next = readyQueue.poll(); 
 					runningProcess = next;
				} else {
					// Check to move running process to IO
					if (runningProcess.isCurrentIO()) {
						runningProcess.setOrder(runningProcess.getCurrentBurstDuration());
						runningProcess.setQexecuted(0);
						ioQueue.add(runningProcess);
				   		if (multiprogram) runningProcess = readyQueue.poll();
				   		else runningProcess = null;
					} else {
						// Specific behavior
						runningProcess = algorithm.forwardTime(readyQueue, runningProcess);
					}
				}
    		}
    	}
		if (runningProcess != null && runningProcess.getTimeresponse() < 0) runningProcess.setTimeresponse(time - runningProcess.getTimesubmission()); // Only first response. A process may be preempted before
    	return false;
    }

	private void incrementWaiting() {
		// Increments waiting time at ready queue
		Iterator<Process> it = readyQueue.iterator();
		while (it.hasNext()) {
			Process p = it.next();
			p.incWaiting();
		}
	}
	
	private void checkIO(boolean multiprogram) {
		// Increments process current burst moment, and checks to move back IO blocked process to ready queue or
		// running is monoprogramming
		List<Process> pending = new LinkedList<Process>();
		
		Iterator<Process> it = ioQueue.iterator();
		while (it.hasNext()) {
			Process p = it.next();
			p.incCurrent();
			if (!p.isCurrentIO()) pending.add(p);
			else p.setOrder(p.getCurrentBurstDuration());
		}

		it = pending.iterator();
		while (it.hasNext()) {
			Process p = it.next();
			ioQueue.remove(p);

			if (multiprogram) algorithm.addProcess(p, readyQueue);
			else runningProcess = p;
		}
	}
	
	private void queueArriving(int time) {
		// Add arriving processes to ready queue
		List<Process> pending = new LinkedList<Process>();
		
		Iterator<Process> it = arrivingQueue.iterator();
		while (it.hasNext()) {
			Process p = it.next();
			if (p.getTimesubmission() == time) pending.add(p);
		}

		it = pending.iterator();
		while (it.hasNext()) {
			Process p = it.next();
			arrivingQueue.remove(p);
			
			algorithm.addProcess(p, readyQueue);
		}
	}

    private void backup() {
    	// backup to restore initial state
    	doneQueue.clear();
    	ioQueue.clear();
    	
    	rqBkup.clear();
    	Iterator<Process> it = readyQueue.iterator();
		while (it.hasNext()) rqBkup.add(it.next().clone());

		aqBkup.clear();
		it = arrivingQueue.iterator();
		while (it.hasNext()) aqBkup.add(it.next().clone());
    }

    /**
     * Restores model to initial state, time 0
     * 
     */
    public void restoreBackup() {
    	// Restore initial state (Time 0) from backup's
    	idle = 0;
    	readyQueue.clear();
    	readyQueue.addAll(rqBkup);
    	arrivingQueue.clear();
    	arrivingQueue.addAll(aqBkup);
    	runningProcess = null;
    	doneQueue.clear();
    	ioQueue.clear();
    }
}

