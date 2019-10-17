package edu.upc.fib.ossim.process.model;
import java.awt.Color;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Process definition (Process scheduling context), instance are comparable to sort them, and cloneable to clone them.  
 * 
 * @author Alex Macia
 */
public class Process implements Comparable<Process>, Cloneable { 
	private static int maxpid = 1;
	private int pid;
	private String name;
	private int prio;  // more value --> more priority 
	private int timesubmission;	
	private boolean periodic;
	private Vector<Integer> burstsCycle;
	private int current;
	private int waiting;
	private int cpu;
	private int qexecuted; // Quantum consumed
	private int timecompletion;
	private int timeresponse;
	private double iorate;
	private int order; // value to compare
	private Color color;
	
	/** 
	 * Constructs a process
	 * 
	 * @param pid	process identifier
	 * @param name	process name	
	 * @param prio	process priority
	 * @param timesubmission	process initial time. (entering ready queue)
	 * @param periodic	true means endless process that repeats burst cycle indefinitely, otherwise	only once
	 * @param burstsCycle process bursts vector. CPU or I/O bursts, values 0 or 1  
	 * @param color	process  color
	 */
	public Process(int pid, String name, int prio, int timesubmission, boolean periodic, Vector<Integer> burstsCycle, Color color) {
		this.pid = pid;
		this.name = name;
		this.prio = prio;
		this.timesubmission = timesubmission;
		this.periodic = periodic;
		this.burstsCycle = burstsCycle;
		this.color = color;
		this.timeresponse = -1;
		int ioburst = 0;
		for (int i=0; i < burstsCycle.size(); i++) ioburst += burstsCycle.get(i);
		this.iorate = (double) ioburst / burstsCycle.size();
		maxpid++;
	}

	/**
	 * Gets process identifier
	 * 
	 * @return process identifier
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * Gets process name
	 * 
	 * @return process name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets process color
	 * 
	 * @return process color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Gets process submission time
	 * 
	 * @return process submission time
	 */
	public int getTimesubmission() {
		return timesubmission;
	}

	/**
	 * Is process periodic? 	
	 * 
	 * @return Is process periodic? 	
	 */
	public boolean isPeriodic() {
		return periodic;
	}
	
	/**
	 * Gets process bursts cycle vector. 	
	 * 
	 * @return process bursts cycle vector
	 */
	public Vector<Integer> getBurstsCycle() {
		return burstsCycle;
	}
	
	/**
	 * Gets process current execution moment. (CPU + I/O)
	 * 
	 * @return process current execution moment
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 * Gets process current bursts moment. 	
	 * 
	 * @return process current bursts moment
	 */
	public int getCurrentBurst() {
		if (periodic) return current%burstsCycle.size();
		else return current;
	}

	/**
	 * Increments current process burst moment
	 *  
	 */
	public void incCurrent() {
		this.current++;
	}
	
	/**
	 * Gets process waiting time
	 * 
	 * @return process waiting time
	 */
	public int getWaiting() {
		return waiting;
	}
	
	/**
	 * Increments waiting process time
	 *  
	 */
	public void incWaiting() {
		this.waiting++;
	}

	/**
	 * Increments cpu process time
	 *  
	 */
	public void incCPU() {
		this.cpu++;
	}

	/**
	 * Gets process priority
	 * 
	 * @return process priority
	 */
	public int getPrio() {
		return prio;
	}
	
	/**
	 * Gets process completion time
	 * 
	 * @return process completion time
	 */
	public int getTimecompletion() {
		return timecompletion;
	}

	/**
	 * Sets process completion time
	 * 
	 * @param timecompletion	
	 */
	public void setTimecompletion(int timecompletion) {
		this.timecompletion = timecompletion;
	}

	/**
	 * Gets process response time
	 * 
	 * @return process response time
	 */
	public int getTimeresponse() {
		return timeresponse;
	}

	/**
	 * Sets process response time 
	 * 
	 * @param timeresponse
	 */
	public void setTimeresponse(int timeresponse) {
		this.timeresponse = timeresponse;
	}
	
	/**
	 * Gets process quantum run time
	 * 
	 * @return process quantum run time
	 */
	public int getQexecuted() {
		return qexecuted;
	}

	/**
	 * Adds time to process quantum run time
	 * 
	 * @param x	time to add
	 */
	public void addQexecuted(int x) {
		this.qexecuted += x;
	}

	/**
	 * Sets process quantum run time 
	 * 
	 * @param qexecuted	time
	 */
	public void setQexecuted(int qexecuted) {
		this.qexecuted = qexecuted;
	}

	/**
	 * Gets process order
	 * 
	 * @return	order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Sets process order
	 * 
	 * @param order	process order
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * Gets unique process identifier
	 * 
	 * @return	unique process identifier
	 */
	public static int getMaxpid() {
		return maxpid;
	}

	/**
	 * Gets current burst duration, from current burst moment to different burst or process end's.
	 * When process burst cycle is periodic, current burst reaches process end's and first burst 
	 * is the same as last one, it adds first burst duration. 
	 * 
	 * @return Gets current burst duration
	 */
	public int getCurrentBurstDuration() {
		int cbduration = 0;
		int cb = burstsCycle.get(getCurrentBurst());
		int i = getCurrentBurst();
		while (i < burstsCycle.size() && burstsCycle.get(i) == cb) {
			cbduration++;
			i++;
		}
		if (periodic && i >= burstsCycle.size() && burstsCycle.get(0) == cb) {  
			i = 0;
			while (i < getCurrentBurst() && burstsCycle.get(i) == cb) {
				cbduration++;
				i++;
			}
		}
		
		return cbduration;
	}
	
	/**
	 * Is current burst IO?
	 * 
	 * @return current burst is IO
	 */
	public boolean isCurrentIO() {
		return burstsCycle.get(getCurrentBurst()) == 1;
	}
	
	/**
	 * Returns process information table header 
	 * 
	 * @return	process information table header
	 */
	public static Vector<Object> getTableHeaderInfo() {
		// Process information table header 
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("pr_30"));
		header.add(Translation.getInstance().getLabel("pr_31"));
		header.add(Translation.getInstance().getLabel("pr_32"));
		header.add(Translation.getInstance().getLabel("pr_37"));
		header.add(Translation.getInstance().getLabel("pr_34")); // Periodic
		header.add(Translation.getInstance().getLabel("pr_35"));
		header.add(Translation.getInstance().getLabel("pr_65")); // Response
		header.add(Translation.getInstance().getLabel("pr_36"));
		header.add(Translation.getInstance().getLabel("pr_64"));
		header.add(Translation.getInstance().getLabel("pr_66")); // CPU rate
		header.add(Translation.getInstance().getLabel("pr_67")); // I/O rate
		return header;
	}
	
	/**
	 * Returns process information table row, cells are ColorCell instances, 
	 * pid cell background color is process color, other cell are painted in white     
	 * 
	 * @param currenttime current simulation time
	 * 
	 * @return	process information table data row
	 * 
	 * @see ColorCell
	 */
	public Vector<Object> getProcessTableInfo(int currenttime) {
		// Process information table header 
		Vector<Object> info = new Vector<Object>();
		info.add(new ColorCell(new Integer(pid).toString(), color));
		info.add(new ColorCell(name, Color.WHITE));
		info.add(new ColorCell(new Integer(prio).toString(), Color.WHITE));
		info.add(new ColorCell(new Integer(timesubmission).toString(), Color.WHITE));
		if (periodic) info.add(new ColorCell("\u2713", Color.WHITE));
		else info.add(new ColorCell("-", Color.WHITE));
		if (currenttime > 0 && currenttime >= timesubmission) { // Ready queue 
			info.add(new ColorCell(new Integer(cpu).toString(), Color.WHITE));
			if (timeresponse >= 0) info.add(new ColorCell(new Integer(timeresponse).toString(), Color.WHITE)); // Response Time
			else info.add(new ColorCell("", Color.WHITE));
			info.add(new ColorCell(new Integer(waiting).toString(), Color.WHITE));  // Waiting time
			if (periodic) info.add(new ColorCell("\u221e", Color.WHITE)); // infinite
			else {
				if (timecompletion > 0) info.add(new ColorCell(new Integer(timecompletion - timesubmission).toString(), Color.WHITE)); // Turnaround Time
				else info.add(new ColorCell("", Color.WHITE));
			}
			double cpurate = 0;
			if (cpu + waiting > 0) cpurate  = (double) cpu / (cpu + waiting);
			info.add(new ColorCell(new Double(cpurate).toString(), Color.WHITE)); // CPU rate	
		} else {
			info.add(new ColorCell("", Color.WHITE));
			info.add(new ColorCell("", Color.WHITE));
			info.add(new ColorCell("", Color.WHITE));  
			info.add(new ColorCell("", Color.WHITE)); 
			info.add(new ColorCell("", Color.WHITE)); 
		}
		info.add(new ColorCell(new Double(iorate).toString(), Color.WHITE)); // IO rate
		return info;
	}
	
	/**
	 * Returns process xml information, pairs attribute name - attribute value: pid, name, prio, init, duration, bursts (vector converted to String 1 0 0 1 ..., blank between bursts) and color
	 * 
	 * @return	process xml information
	 */
	public Vector<Vector<String>> getProcessXMLInfo() {
		// Process xml information  
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> attribute;		
		
		attribute = new Vector<String>();
		attribute.add("pid");
		attribute.add(Integer.toString(pid));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("name");
		attribute.add(name);
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("prio");
		attribute.add(Integer.toString(prio));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("submission");
		attribute.add(Integer.toString(timesubmission));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("periodic");
		attribute.add(Boolean.toString(periodic));
		data.add(attribute);
		
		String sbursts = ""; // Convert bursts to String 1 0 0 1 ..., blank between bursts
		for(int i = 0; i < burstsCycle.size(); i++) {
			sbursts += burstsCycle.get(i) + " ";
		}
		attribute = new Vector<String>();
		attribute.add("bursts");
		attribute.add(sbursts);
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("color");
		attribute.add(Integer.toString(color.getRGB()));
		data.add(attribute);
		
		return data;
	}
	
	/**
	 * Compare this process with p 
	 * 
	 * @return 	comparison result 
	 */
	public int compareTo(Process p) {
		if (this.order == p.getOrder()) return this.pid - p.getPid();
		else return this.order - p.getOrder();
	}
	
	/**
	 * Clones this process
	 * 
	 * @return cloned process
	 */
	protected Process clone() {
	    Process clone = null;
		try {
			clone = (Process) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    return clone;
	}
}
