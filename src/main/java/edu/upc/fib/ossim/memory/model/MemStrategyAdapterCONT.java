package edu.upc.fib.ossim.memory.model;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;

/**
 * Abstract class that implements interface MemStrategy (Contiguous memory management)
 * 
 * @author Àlex
 */
public abstract class MemStrategyAdapterCONT implements MemStrategy {
	protected String policy; // allocation policy: "FF", "BF", "WF"

	public MemStrategyAdapterCONT(String policy) {
		super();
		this.policy = policy;
	}
	
	/**
	 * Set algorithm allocation policy
	 */
	public void setPolicy(String policy) {
		this.policy = policy; 
	}
	
	/**
	 * Gets Fixed-size partitions algorithm information including allocation policy   
	 * 
	 * @param key label's key
	 * 
	 * @return	algorithm information
	 */
	protected String getAlgorithmInfo(String key) {
		if ("FF".equals(policy)) return Translation.getInstance().getLabel(key, "me_55"); 
		if ("BF".equals(policy)) return Translation.getInstance().getLabel(key, "me_56");
		if ("WF".equals(policy)) return Translation.getInstance().getLabel(key, "me_57");
		return "";
	}
	
	/**
	 * Initializes memory, creates a partition sized as operating system and allocates it.   
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param strSO			operating system process name
	 * @param OSsize		operating system size	
	 * @param color			operating system background color
	 * @param memory_size	memory size
	 */
	public void initMemory(List<MemPartition> memory, String strSO, int OSsize,  Color color, int memory_size) {
		memory.clear();
		
    	//	Add SO.
		MemPartition m = new MemPartition(0, getInitPartitionSize (OSsize, memory_size));
		memory.add(m);
		
		ProcessComplete so = new ProcessComplete(0, strSO, OSsize, -1, color);
		try {
			allocateProcess(memory, null, so, memory_size);
		} catch (Exception e) {
			System.out.println("Error initializing memory - contiguous memory management");
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns initial algorithm partition size
	 * 
	 * @param OSsize		operating system size	
	 * @param memory_size	memory size
	 * 
	 * @return  initial algorithm partition size
	 */
	protected abstract int getInitPartitionSize (int OSsize,  int memory_size);
	
	/**
	 * Partition never can be selected at time = 0
	 * 
	 * @return false
	 */
	public boolean isSelectable() {
		return false;
	}
	
	/**
	 * Returns true if algorithm has external fragmentation
	 * 
	 * @return true
	 */
	public boolean hasExternalFragmentation() {
		return true;
	}
	
	/**
	 * Returns "". There is no component info in this strategy
	 * 
	 * @param component		unused
	 * @return	""
	 */
	public String getProcessComponentInfo(ProcessMemUnit component) {
		return "";
	}

	/**
	 * Gets process size info. Painter view. 
	 * Size in units  (eg. "Size 30 u.)
	 * 
	 * @return size info
	 */
	public String getComponentSizeInfo(ProcessComplete p) {
		return Translation.getInstance().getLabel("me_32") + " " + p.getSize() + " u.";
	}

	/**
	 * Returns null. There is no component data in this strategy  
	 * 
	 * @param process	unused
	 * @return	null
	 */
	public Vector<Vector<Object>> getProcessComponentsData(ProcessMemUnit process) {
		return null;
	}
	
	/**
	 * Returns memory occupation table header: address, partition size, pid, name, process size, duration  
	 * 
	 * @return	memory occupation table header
	 */
	public Vector<Object> getTableHeaderInfo() {
		// Block information table header 
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("me_35")); // Address
		header.add(Translation.getInstance().getLabel("me_36")); // Partition size	
		header.add(Translation.getInstance().getLabel("me_30")); // PID	
		header.add(Translation.getInstance().getLabel("me_31")); // Name
		header.add(Translation.getInstance().getLabel("me_32")); // Size (Process)
		header.add(Translation.getInstance().getLabel("me_33")); // Duration
		return header;
	}
	
	/**
	 * Returns a partition occupation data, including process data if partition allocates one.
	 * Cells are ColorCell instance
	 * 
	 * @param m			memory partition
	 * @return 	a partition occupation data
	 * 
	 * @see ProcessMemUnit#getInfo()
	 * @see ColorCell
	 */
	public Vector<Object> getTableBlockInfo(MemPartition m) {
		// Block information table header 
		Vector<Object> info = new Vector<Object>();
		info.add(new ColorCell(new Integer(m.getStart()).toString(), Color.WHITE));
		info.add(new ColorCell(new Integer(m.getSize()).toString(), Color.WHITE));
		if (m.getAllocated() != null) info.addAll(m.getAllocated().getInfo());
		else {
			for (int i=2; i<getTableHeaderInfo().size();i++) info.add(new ColorCell("", Color.WHITE));
		}
		return info;
	}
	
	/**
	 * Returns null. There is no component data in this strategy  
	 * 
	 * @return	null
	 */
	public Vector<Object> getFormTableHeader() {
		return null;
	}
	
	/**
	 * Returns null. There is no component data in this strategy  
	 * 
	 * @return	null
	 */
	public Vector<Vector<Object>> getFormTableInitData() {
		return null;
	}
	
	
	/**
	 * Returns null. There is no component data in this strategy  
	 * 
	 * @return	null
	 */
	public Vector<Object> getMemProcessTableHeader() {
		return null;
	}
	
	/**
	 * Returns null. There is no component data in this strategy  
	 * 
	 * @param memory	unused
	 * @param p			unused	
	 * @return	null
	 */
	public Vector<Vector<Object>> getMemProcessTableData(List<MemPartition> memory, ProcessComplete p) {
		return null;
	}
	
	/**
	 * Do nothing. There is no component data in this strategy
	 * 
	 * @param p			unused
	 * @param d 		unused
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void addProcessComponents(ProcessComplete p,  Vector<Vector> d) {
		// Do nothing
	}
	
	/**
	 * Removes a process from memory
	 * 
	 * @param memory	partitions linked list (memory)  	
	 * @param b			memory partition containing process
	 */
	public void removeProcessInMemory(List<MemPartition> memory, MemPartition b) {
    	b.setAllocated(null);
	}
	
	/**
	 * Allocates a process into memory according to an allocation policy:
	 * <ul>
	 * <li>"FF" (First Fit): process is allocated into the first partition big enough to hold it</li>  
	 * <li>"BF" (Best Fit): process is allocated into the smallest partition big enough to hold it</li>
	 * <li>"WF" (Worst Fit): process is allocated into the biggest partition big enough to hold it</li>
	 * </ul>
	 *  
	 * @param memory		partitions linked list (memory)  
	 * @param swap			unused  
	 * @param allocate		process to allocate
	 * @param memory_size	memory size
	 * 
	 * @throws SoSimException	process can not be allocated
	 */
	public void allocateProcess(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit allocate, int memory_size) throws SoSimException {
		Object[] memOrdered = memory.toArray();
    	Arrays.sort(memOrdered);
    	int i = 0;
    	MemPartition candidate = null; 
    	
    	// First Fit
		if ("FF".equals(policy)) {
	    	while (i<memOrdered.length && candidate == null) {
	    		MemPartition partition = (MemPartition) memOrdered[i];
	    		if (partition.getAllocated() == null && partition.getSize() >= allocate.getSize()) {
	    			// Allocates only necessary
	    			candidate = partition;// First candidate
	    		}
	    		i++;
			}
		}
		// Best Fit. In case of doubt, select first
		if ("BF".equals(policy)) {
	    	while (i<memOrdered.length) {
	    		MemPartition partition = (MemPartition) memOrdered[i];
	    		if (partition.getAllocated() == null && partition.getSize() >= allocate.getSize()) {
	    			if (candidate == null) candidate = partition;// First candidate
	    			if (partition.getSize() < candidate.getSize()) candidate = partition;
	    		}
	    		i++;
			}
		}
		// Worst Fit. In case of doubt, select first
		if ("WF".equals(policy)) {
	    	while (i<memOrdered.length) {
	    		MemPartition partition = (MemPartition) memOrdered[i];
	    		if (partition.getAllocated() == null && partition.getSize() >= allocate.getSize()) {
	    			if (candidate == null) candidate = partition;// First candidate
	    			if (partition.getSize() > candidate.getSize()) candidate = partition;
	    		}
	    		i++;
			}
		}
		
		if (candidate != null) allocateCandidate(memory, candidate, allocate);
		else throw new SoSimException("me_08");
	}

	protected abstract void allocateCandidate(List<MemPartition> memory, MemPartition candidate, ProcessMemUnit allocate);
	
	/**
     * Allocates swapped process from backing store into memory according to an allocation policy. 
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param swap			unused  
	 * @param swapped		swapped process to allocate
	 * @param memory_size	memory size
	 * 
	 * @throws SoSimException	process can not be allocated
	 * 
	 * @see #allocateProcess(List, List, ProcessMemUnit, int, String)
	 */
	public void swapInProcessComponent(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit swapped, int memory_size) throws SoSimException {
		allocateProcess(memory, swap, swapped, memory_size);
	}
	
	/**
     * Moves a process to backing store
	 * 
	 * @param memory	partitions linked list (memory)
	 * @param swap		processes into backing store linked list (swap)  
	 * @param partition	memory partition allocating process to swap	
	 * 
	 * @throws SoSimException	partition does not allocate any process
	 */
	public void swapOutProcess(List<MemPartition> memory, List<ProcessMemUnit> swap, MemPartition partition) throws SoSimException {
		if (partition.getAllocated() == null)  throw new SoSimException("me_09");
		swap.add(partition.getAllocated());
		partition.setAllocated(null);
	}
	
	/**
	 * Removes swapped from swap
	 * 
	 * @param memory	unused
	 * @param swap		processes into backing store linked list (swap)  
	 * @param swapped 	process in the backing store  
	 * 
	 */
	public void removeSwappedProcessComponents(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit swapped) {
		swap.remove(swapped);
	}
	
	/**
	 * Returns address translation, given a process logical address gets its corresponding physical address
	 * 
	 * @param b				memory partition containing process
	 * @param logicalAddr	process logical address, belong to process logical space 
	 * @param memory		partitions linked list (memory)
	 * 
	 * @return	address translation. format "@9999"
	 */
	public String getAddTransPhysical(MemPartition b, int logicalAddr, List<MemPartition> memory) {
		// logicalAddr belongs to process logical space
		return "@" + new Integer(b.getStart() + logicalAddr).toString();
	}
}
