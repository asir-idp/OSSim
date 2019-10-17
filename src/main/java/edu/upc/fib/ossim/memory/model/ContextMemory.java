package edu.upc.fib.ossim.memory.model;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Memory Management Model (Model - View - Presenter Pattern). Different management algorithm are implemented 
 * through Strategy Pattern. Model manage a maximum of<code>"MAX_PROCESSES"</code> processes.    
 * 
 * @author Alex Macia
 */
public class ContextMemory {
	public static final int MAX_PROCESSES = 20;
	private MemStrategy algorithm;
	private int memorySize; 
	private int osSize;
	// Separate queue's cause different orders   
	private List<ProcessMemUnit> processQueue; 	// Processes arriving, creation ordered 
	private List<MemPartition> memory; 	// Memory structure, ordered by init address 
	private List<ProcessMemUnit> swap; 	// Processes swapped out
	
	private List<ProcessMemUnit> pqBkup; 		// Programs arriving backup to restore initial state  
	private List<MemPartition> bqBkup; 		// Memory structure backup to restore initial state
	private ProcessMemUnit selectedProcess;
	private MemPartition selectedPartition;
	private ProcessMemUnit selectedSwap;

	/**
	 * Constructs a ContextMemory: sets main parameters (os, memory and page size), a concrete algorithm strategy and
	 * initialize memory allocating the operating system 
	 * 
	 * @param memorySize	memory size
	 * @param osSize		operating system size
	 * @param pageSize		page size
	 * @param algorithm		default algorithm
	 *  
	 */
    public ContextMemory(int memorySize, int osSize, int pageSize, MemStrategy algorithm) {
        this.memorySize = memorySize;
        this.osSize = osSize;
    	this.algorithm = algorithm;
    	
        processQueue = new LinkedList<ProcessMemUnit>();
        memory = new LinkedList<MemPartition>();
        swap = new LinkedList<ProcessMemUnit>();
        pqBkup = new LinkedList<ProcessMemUnit>();
        bqBkup = new LinkedList<MemPartition>();
        
        //	Add OS.
        algorithm.initMemory(memory, Translation.getInstance().getLabel("me_90"), osSize, Color.lightGray, memorySize);
    }
 
    /**
     * Returns memory size
     * 
     * @return memory size
     */
	public int getMemorySize() {
		return memorySize;
	}

	/**
	 * Returns operating system size
	 * 
	 * @return operating system size
	 */
	public int getSOSize() {
		return osSize;
	}

	/**
     * Returns processes count  
     * 
     * @return	processes count
     */
 	public int getProcessCount() {
		return processQueue.size();
	}

 	/**
 	 * Sets memory parameters, initialize memory, loads operating system and clear processes queue 
 	 * 
 	 * @param memorySize	memory size
 	 * @param osSize		operating system size
 	 */
	public void setMemorySizeParams(int memorySize, int osSize){
    	this.memorySize = memorySize;
    	this.osSize = osSize;
    	algorithm.initMemory(memory, Translation.getInstance().getLabel("me_90"), osSize, Color.lightGray, memorySize);
    	processQueue.clear();
    }
	
	/**
     * Change algorithm strategy, initialize memory, loads operating system
     * and clear processes queue    
     * 
     * @param algorithm	new algorithm
     */
    public void setAlgorithm(MemStrategy algorithm){
    	this.algorithm = algorithm;
    	algorithm.initMemory(memory, Translation.getInstance().getLabel("me_90"), osSize, Color.lightGray, memorySize);
    	processQueue.clear();
    }

    /**
	 * @see MemStrategy#setPolicy(String)
	 */
	public void setPolicy(String policy) {
		algorithm.setPolicy(policy); 
	}
    
	/**
	 * Gets selected process data: id, name, size, duration and color and (optional) its components. 
	 * Only in segmentation and pagination programs are divided in components (pages or segments)
	 * 
	 * @return	selected process data
	 * 
	 * @see MemStrategy#getProcessComponentsData(ProcessMemUnit)
	 */
	public Vector<Object> getSelectedProcessData() {
		Vector<Object> data = new Vector<Object>();
		data.add(selectedProcess.getPid());
		data.add(selectedProcess.getParent().getName());
		data.add(selectedProcess.getSize());
		data.add(selectedProcess.getParent().getDuration());
		data.add(selectedProcess.getParent().getColor());
		data.add(algorithm.getProcessComponentsData(selectedProcess));
		return data;
	}

	/**
	 * Selects a process identified by id
	 * 
	 * @param id	process identifier
	 * 
	 * @return process exist
	 */
	public boolean setSelectedProcess(int id) {
		selectedProcess = getByPID(id);
		return selectedProcess != null;
	}

	/**
	 * Selects a process or one of its components in the backing store identified by compoundId. 
	 * compoundId = 1000 * pid + component seq identifier
	 * 
	 * @param compoundId	process component identifier
	 * 
	 * @return swapped process (or component) exist
	 */
	public boolean setSelectedSwap(int compoundId) {
		// Returns ProgramComponent from swap queue or null if not exists
		Iterator<ProcessMemUnit> it = swap.iterator();
		ProcessMemUnit p;

		while (it.hasNext()) {
			p = it.next();
			int id = p.getPid() * 1000;
			
			if (p.getParent().getNumBlocks() != 0) id += ((ProcessComponent) p).getBid(); 
		
			if (id == compoundId) {
				selectedSwap = p;
				return true;
			}
		}
		return false;
	}

	/**
	 * Selects a memory partition identified by id (start address), OS partition can't be selected,
	 * neither while running partitions with no process allocated except. When not running  
	 * concrete selection strategies depends on current algorithm 
	 * 
	 * @param start	memory partition identifier (start address)
	 * @param started simulation is running
	 * 
	 * @return partition exists and can be selected
	 */
	public boolean setSelectedPartition(int start, boolean started) {
		selectedPartition = getByStart(start);
		if (selectedPartition == null) return false; 
		if (selectedPartition.getAllocated() != null && selectedPartition.getAllocated().getParent().getPid() == 0) return false;
		if (started) {
			if (selectedPartition.getAllocated() == null) return false;
			else return true;
		} else return algorithm.isSelectable(); // only true for FIX 
	}
	
	/**
	 * Returns selected memory partition start address
	 * 
	 * @return	selected memory partition start address
	 */
    public int getSelectedPartitionId() {
		return selectedPartition.getStart();
	}

	/**
	 * Returns selected memory partition data, memory size, start address and partition size
	 * 
	 * @return	memory partition data
	 */
    public Vector<Object> getSelectedPartitionData() {
    	Vector<Object> data = new Vector<Object>();
    	data.add(getMemorySize()-1); 
		data.add(selectedPartition.getStart());
		data.add(selectedPartition.getSize());
		return data;
    }

    /**
     * Returns list iterator with queued processes identifiers 
     * 
     * @return	list iterator
     */
    public Iterator<Integer> iteratorProcesses() {
    	// Returns ordered iterator from queue
    	// Returns LinkedList with program's PID's
		LinkedList<Integer> queueInteger = new LinkedList<Integer>();
		
		Iterator<ProcessMemUnit> it = processQueue.iterator();
		while (it.hasNext()) queueInteger.add(it.next().getPid());
		
		return queueInteger.iterator();
	}

    /**
     * Returns list iterator with memory partitions identifiers (start addresses) 
     * 
     * @return	list iterator
     */
    public Iterator<Integer> iteratorPartitions() {
    	// Returns ordered iterator from queue
    	// Returns LinkedList with partition's start address
		LinkedList<Integer> queueInteger = new LinkedList<Integer>();
		
		Iterator<MemPartition> it = memory.iterator();
		while (it.hasNext()) queueInteger.add(it.next().getStart());
		
		return queueInteger.iterator();
	}

    /**
     * Returns list iterator with processes in backing store (totally or partially) identifiers 
     * 
     * @return	list iterator
     */
    public Iterator<Integer> iteratorSwap() {
		// Returns ordered iterator from queue
		// Returns LinkedList with swapped process pid's
		LinkedList<Integer> queueObjects = new LinkedList<Integer>();
		
		Iterator<ProcessMemUnit> it = swap.iterator();
		while (it.hasNext()) {
			int id = it.next().getPid();
			if (!queueObjects.contains(id)) queueObjects.add(id); // If various units of the same process  
		}
		
		return queueObjects.iterator();
	}

	private ProcessMemUnit getByPID(int pid) {
		// Returns Program's PID from programs queue, swap, memory list or null if not exists
		Iterator<ProcessMemUnit> it = processQueue.iterator();
		Iterator<MemPartition> itb = memory.iterator();
		Iterator<ProcessMemUnit> itw = swap.iterator();
		ProcessMemUnit p;
		MemPartition b;
		
		while (it.hasNext()) {
			p = it.next();
			if (p.getPid() == pid) return p;
		}
		while (itb.hasNext()) {
			b = itb.next();
			if (b.getAllocated() != null && b.getAllocated().getPid() == pid) return b.getAllocated();
		}
		while (itw.hasNext()) {
			p = itw.next();
			if (p.getPid() == pid) return p;
		}
		return null;
	}

	private MemPartition getByStart(int start) {
		// Returns Block's start from partitions queue or null if not exists
		Iterator<MemPartition> it = memory.iterator();
		MemPartition b;
		
		while (it.hasNext()) {
			b = it.next();
			if (b.getStart() == start) return b;
		}
		return null;
	}

	/**
	 * Gets process information, pid and name       
	 * 
	 * @param pid	process identifier
	 * @return	process information
	 */
	public Vector<String> getInfo(int pid) {
		// Painters program info
		Vector<String> info = new Vector<String>();
		ProcessMemUnit p = getByPID(pid);

		info.add("PID " + p.getPid());
		info.add(p.getParent().getName());
		if (p.getParent().getDuration() == -1) info.add(Translation.getInstance().getLabel("me_33") + " \u221e"); 
		else info.add(Translation.getInstance().getLabel("me_33") + " " + p.getParent().getDuration());

		return info;
	}

	/**
	 * @see MemStrategy#getComponentSizeInfo()
	 */
	public String getComponentSizeInfo(int pid) {
		ProcessMemUnit p = getByPID(pid);
		return algorithm.getComponentSizeInfo(p.getParent());
	}
	
	/**
	 * Returns total number of components of a process identified by pid.
	 * In contiguous memory management process have only 1 component. 
	 *
	 * @return total number of components of a process
	 */
	public int getTotalComponents(int pid) {
		ProcessMemUnit p = getByPID(pid);
		if (p.getParent().getNumBlocks() == 0) return 1;
		else return p.getParent().getNumBlocks();
	}

	/**
	 * Returns size of a concrete process component.
	 * In contiguous memory management returns process size. 
	 *
	 * @return size of a concrete process component
	 */
	public int getSizeOfComponents(int pid, int i) {
		ProcessMemUnit p = getByPID(pid);
		if (p.getParent().getNumBlocks() == 0) return p.getSize();
		else return p.getParent().getBlock(i).getSize();
	}
	
	/**
	 * Returns when a process component is swapped or it would be swapped 
	 * 
	 * @see a process component is swapped or it would be swapped (Process.isLoad()) 
	 */
	public boolean isComponentSwapped(int pid, int i) {
		ProcessMemUnit p = getByPID(pid);
		if (p.getParent().getNumBlocks() != 0) return !p.getParent().getBlock(i).isLoad(); // Concrete component
		return swap.contains(p);
	}

	/**
	 * Returns when a process component is a page
	 * 
	 * @see a process component is a page 
	 */
	public boolean isComponentPage(int pid, int i) {
		ProcessMemUnit p = getByPID(pid);
		if (p.getParent().getNumBlocks() == 0) return false;
		else return p.getParent().getBlock(i).isPage();
	}

	/**
	 * Gets process (if exists) color
	 * 
	 * @param pid	process identifier
	 * @return	process color
	 */
	public Color getColor(int pid) {
		if (getByPID(pid) == null) return null;
		else return getByPID(pid).getParent().getColor();
	}

	/**
	 * Gets process duration
	 * 
	 * @param pid	process identifier
	 * @return	process duration
	 */
	public int getDuration(int pid) {
		return getByPID(pid).getParent().getDuration();
	}

	/**
	 * Gets process size
	 * 
	 * @param pid	process identifier
	 * @return	process size
	 */
	public int getSize(int pid) {
		return getByPID(pid).getSize();
	}
	
	/**
	 * Gets unique process identifier
	 * 
	 * @return	unique process identifier
	 */
	public int getMaxpid() {
		return ProcessComplete.getMaxpid();
	} 

	/**
	 * Gets memory partition size
	 * 
	 * @param start	memory partition start address
	 * @return	memory partition size
	 */
	public int getMemSize(int start) {
		return getByStart(start).getSize();
	}

	/**
	 * Gets process size allocated at partition identified by start or -1 
	 * if no process is allocated
	 * 
	 * @param start	memory partition start address
	 * @return	process size allocated at partition identified by start or -1
	 */
	public int getMemProcessSize(int start) {
		if (getByStart(start).getAllocated() != null) return getByStart(start).getAllocated().getSize();
		else return -1;
	}

	/**
	 * Gets process color allocated at partition identified by start or null 
	 * if no process is allocated
	 * 
	 * @param start	memory partition start address
	 * @return	process color allocated at partition identified by start or null
	 */
	public Color getMemProcessColor(int start) {
		if (getByStart(start).getAllocated() != null) return getByStart(start).getAllocated().getParent().getColor();
		else return null;
	}

	/** 
	 * @see MemStrategy#hasExternalFragmentation()
	 */
	public boolean hasExternalFragmentation() {
		return algorithm.hasExternalFragmentation();
	}
	
	/**
	 * Gets process information allocated at partition identified by start or null 
	 * if no process is allocated
	 * 
	 * @param start	memory partition start address
	 * @return	process information allocated at partition identified by start or null
	 * 
	 * @see MemStrategy#getProcessComponentInfo(ProcessMemUnit)
	 */
	public Vector<String> getMemProcessInfo(int start) {
		// Painters memory info
		Vector<String> info = new Vector<String>();
		if (getByStart(start).getAllocated() != null) {
			ProcessMemUnit p = getByStart(start).getAllocated();
			if (p.getParent().getPid() == 0) info.add("ID " + p.getPid() +  " (" + p.getParent().getName() + ")");
			else info.add("ID " + p.getPid() + " - " +  algorithm.getProcessComponentInfo(p) +  " (" + p.getParent().getName() + ")");
			return info;
		}
		else return null;
	}

	/**
	 * Returns process information for address translation form 
	 * 
	 * @return	process information for address translation form
	 */
	public String getAddTransProgInfo() {
		ProcessComplete p = selectedPartition.getAllocated().getParent();
		return "PID " + p.getPid() + " (" + p.getParent().getName() + ") " + Translation.getInstance().getLabel("me_77") + ":" + p.getParent().getSize() + " u.";
	}
	
	/**
	 * Returns logical address translation to physical 
	 * 
	 * @param logicalAddr	process logical address
	 * @return	logical address translation to physical
	 */
	public String getAddTransPhysical(int logicalAddr) {
		// Work in progress
		ProcessComplete p = selectedPartition.getAllocated().getParent();
		if (logicalAddr >= p.getSize()) return Translation.getInstance().getLabel("me_85"); // Illegal address
		return algorithm.getAddTransPhysical(selectedPartition, logicalAddr, memory);
	}
	
	/**
	 * @see MemStrategy#getXMLDataMemory()
	 */
	public  Vector<Vector<Vector<String>>> getXMLDataMemory() {
		return algorithm.getXMLDataMemory(memory);
	}
	
	/**
	 * Returns processes queue xml information 
	 * 
	 * @return	processes queue xml information
	 * 
	 * @see ProcessMemUnit#getXMLInfo()
	 */
	public Vector<Vector<Vector<String>>> getXMLDataPrograms() {
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();
		Iterator<ProcessMemUnit> it = processQueue.iterator();
		while (it.hasNext()) {
			data.add(it.next().getXMLInfo());
		}
		return data;
	}
	
	/**************************************************************************************************/
	/*************************************  Specific Strategies ***************************************/
	/**************************************************************************************************/

	/**
	 * Returns memory information table header 
	 * 
	 * @return	memory information table header
	 * 
	 * @see MemStrategy#getTableHeaderInfo()
	 */
	public Vector<Object> getTableHeaderInfo() {
		// Information table Header 
		return algorithm.getTableHeaderInfo();
	}
	
	/**
	 * Returns memory information table data depending on current algorithm      
	 * 
	 * @return	memory information table data
	 * 
	 * @see MemStrategy#getTableBlockInfo(MemPartition, int)
	 */
	public Vector<Vector<Object>> getTableInfoData() {
		// General information data 
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		Iterator<MemPartition> it = memory.iterator();
		while (it.hasNext()) {
			MemPartition m = it.next();
			data.add(algorithm.getTableBlockInfo(m));
		}

		if (data.size() == 0) return null;
		return data;
	}

	/**
	 * Returns memory partition information table data depending on current algorithm      
	 * 
	 * @return	memory information table data
	 * 
	 * @see MemStrategy#getTableBlockInfo(MemPartition, int)
	 */
	public Vector<Object> getMemBlockInfo(int start) {
		// Information table data
		MemPartition m = getByStart(start);
		return algorithm.getTableBlockInfo(m);
	}
	
	/**
	 * Returns process creation table header depending on current algorithm      
	 * 
	 * @return	process creation table header depending on current algorithm      
	 * 
	 * @see MemStrategy#getFormTableHeader()
	 */
	public Vector<Object> getFormTableHeader() {
		// Form program Header 
		return algorithm.getFormTableHeader();
	}

	/**
	 * Returns process creation table initial data (pagination only)      
	 * 
	 * @return	process creation table initial data (pagination only)      
	 * 
	 * @see MemStrategy#getFormTableInitData(int)
	 */
	public Vector<Vector<Object>> getFormTableInitData() {
		// Form program Initial Data 
		return algorithm.getFormTableInitData();
	}
	
	/**
	 * Returns allocation tables header, pages table (pagination) or segments table (segmentation)
	 *  
	 * @return	allocation tables header      
	 * 
	 * @see MemStrategy#getMemProcessTableHeader()
	 */
	public Vector<Object> getMemProcessTableHeader() {
		// Allocation tables header, segmentation table and page table 
		return algorithm.getMemProcessTableHeader();
	}
	
	/**
	 * Returns allocation tables data, pages table (pagination) or segments table (segmentation)
	 *  
	 * @return	allocation tables data      
	 * 
	 * @see MemStrategy#getMemProcessTableData(List, ProcessComplete, int)
	 *
	 * @throws SoSimException	no process is allocated into selected partition 
	 */
	public Vector<Vector<Object>> getMemProcessTableData() throws SoSimException {
		// Allocation tables, segmentation table and page table 
		if (selectedPartition.getAllocated() == null) throw new SoSimException("me_09");
		return algorithm.getMemProcessTableData(memory, selectedPartition.getAllocated().getParent());
	}
	
	/**
	 * Adds a new process to processes queue, in pagination and segmentation 
	 * also create its components, pages or segments      
	 *  
	 * @param data	process data: pid, name, size, duration and color
	 * @param components	in pagination, pages data, in segmentation, segments data   
	 * 
	 * @see MemStrategy#addProcessComponents(ProcessComplete, Vector, int)
	 */
    @SuppressWarnings("rawtypes")
	public void addProgram(Vector<Object> data, Vector<Vector> components) {
    	// Add Program p to program's queue 
    	ProcessComplete p = new ProcessComplete(Integer.parseInt((String) data.get(0)), (String) data.get(1), (Integer) data.get(2), (Integer) data.get(3), (Color) data.get(4));
    	processQueue.add(p);
    	algorithm.addProcessComponents(p, components);
    }

	/**
	 * Update a process from processes queue, in pagination and segmentation 
	 * also updates its components, pages or segments      
	 *  
	 * @param data	process data: pid, name, size, duration and color
	 * @param components	in pagination, pages data, in segmentation, segments data   
	 * 
	 * @see MemStrategy#addProcessComponents(ProcessComplete, Vector, int)
	 */
    @SuppressWarnings("rawtypes")
	public void updProgram(Vector<Object> data, Vector<Vector> components) {
    	// Add Program p to program's queue 
    	int i = processQueue.indexOf(selectedProcess);
    	processQueue.remove(selectedProcess);
    	ProcessComplete p = new ProcessComplete(Integer.parseInt((String) data.get(0)), (String) data.get(1), (Integer) data.get(2), (Integer) data.get(3), (Color) data.get(4));
    	processQueue.add(i, p);
    	algorithm.addProcessComponents(p, components);
    }
    
    /**
     * Removes selected process from processes queue
     */
    public void removeProgram() {
    	// Removes Program p from its queue
    	processQueue.remove(selectedProcess);
    }
	
    /**
     * Removes process allocated at selected partition. 
     * 
     * @throws SoSimException	selected partition has no process allocated or is trying to remove operating system
     * 
     * @see MemStrategy#removeProcessInMemory(List, MemPartition)	
     */
    public void removeProgramInMem() throws SoSimException {
    	// Can't remove SO 
    	if (selectedPartition.getStart() == 0) throw new SoSimException("me_04"); 
    	if (selectedPartition.getAllocated() == null) throw new SoSimException("me_09");
    	algorithm.removeProcessInMemory(memory, selectedPartition);
    }
    
	/**
	 * Adds a new memory partition, this partition becomes selected partition 
	 *  
	 * @param data	partition data: start address and size
	 * 
	 * @throws SoSimException	new partition overlaps with any other existing partition or ends over memory size
	 * 
	 */
    public void addMemPartition(Vector<Object> data) throws SoSimException {
    	backup();
    	MemPartition b = new MemPartition((Integer) data.get(0), (Integer) data.get(1));
		// Can't create partition at 0 address. SO
    	if (b.getStart() >= 0 && b.getStart() < osSize) throw new SoSimException("me_05");
    	// Detect possible overlapping
    	Iterator<MemPartition> it =  memory.iterator();
    	
    	boolean end = false;
    	while (it.hasNext() && !end) {
    		MemPartition aux = it.next();
    		if (aux.getStart() == b.getStart()) throw new SoSimException("me_05");
    		if (aux.getStart() < b.getStart() && (aux.getStart() + aux.getSize()) > b.getStart()) throw new SoSimException("me_05");
    		if (b.getStart() < aux.getStart() &&  (b.getStart() +  b.getSize()) > aux.getStart()) throw new SoSimException("me_05");
    	}
    	
    	if (b.getStart() + b.getSize() > memorySize) throw new SoSimException("me_07");
    	
    	memory.add(b);	
		selectedPartition = b;
    }

    /**
     * Removes selected partition from memory
     * 
     * @throws SoSimException
     */
    public void removeMemPartition() throws SoSimException {
    	// Can't remove SO 
    	backup();
    	if (selectedPartition.getStart() == 0) throw new SoSimException("me_04"); 
    	memory.remove(selectedPartition);
    }
    
    /**
     * Compacts and merge free memory partitions, it is used in segmentation and
     * variable size partitioning 
     * 
     * @see MemStrategy#compaction(List, int)
     */
    public void compaction() {
    	algorithm.compaction(memory, memorySize);
    }
    
    /**
     * Removes a selected process from backing store. In non contiguous 
     * memory management strategies, process components are also removed from memory
     *  
     * @see MemStrategy#removeSwappedProcessComponents(List, ProcessMemUnit)
     */
    public void removeSwappedProgram() {
    	// Removes Process p from swap queue
    	algorithm.removeSwappedProcessComponents(memory, swap, selectedSwap);
    }

    /**
     * Allocates selected process from backing store into memory. 
     * 
     * @throws SoSimException		there is no memory available to allocate the process
     * 
     * @see MemStrategy#swapInProcessComponent(List, List, ProcessMemUnit, int)
     */
    public void swapInProgramComponent() throws SoSimException {
    	// From backing Store to memory	
		algorithm.swapInProcessComponent(memory, swap, selectedSwap, memorySize);
		swap.remove(selectedSwap);
    }

    /**
     * Moves process allocated at selected partition to backing store
     * 
     * @throws SoSimException	no process is allocated at selected partition
     * 
     * @see MemStrategy#swapOutProcess(List, List, MemPartition)
     */
    public void swapOutProgramComponent() throws SoSimException {
    	// From memory to backing Store
		algorithm.swapOutProcess(memory, swap, selectedPartition);
    }
    
    /**
     * Gets current algorithm information
     * 
     * @return current algorithm information
     * 
     * @see MemStrategy#getAlgorithmInfo(String, int)
     */
    public String getAlgorithmInfo() {
    	return algorithm.getAlgorithmInfo();
    }

    /**
     * Forwards simulation time 1 unit. Common tasks such as initial (time 0) state back up, 
     * release terminated processes from memory and allocate next process into memory according to
     * concrete strategy. In fixed-size management an additional validation is done at initial time, memory
     * must be completely partitioned.  Returns true when simulation ends 
     * (no more processes in any queue)
     * 
     * @param time		current simulation time
     * 
     * @return simulation ends
     * 
     * @see MemStrategy#validateMemory(List, int)
     * @see MemStrategy#allocateProcess(List, List, ProcessMemUnit, int)
     */
    public boolean forwardTime(int time) throws SoSimException {
    	if (time == 0) {
    		backup(); // backup to restore initial state
    		algorithm.validateMemory(memory, memorySize);
    		if (processQueue.isEmpty()) return true;
    	} else {
    		// Release terminated programs from memory 
    		if (memory.size() > 0) releasePrograms(memory);

    		// Allocate new programs into memory. Programs ordered by init time
    		if (processQueue.size() > 0) {
    			algorithm.allocateProcess(memory, swap, processQueue.get(0), memorySize);
    			processQueue.remove(0);
    		}
    	}
    	return false;
   	}

    private void releasePrograms(List<MemPartition> memory) {
    	// Release terminated programs from memory, and decrements duration 
    	Iterator<MemPartition> it = memory.iterator();
    	List<ProcessMemUnit> updated = new LinkedList<ProcessMemUnit>(); 
    	
    	while (it.hasNext()) {
    		MemPartition b = it.next();
    		ProcessMemUnit p = b.getAllocated();
    		if (p != null && p.getParent().getDuration() >= 0) {  // Duration -1 infinite
				if (!updated.contains(p.getParent())) {  // Only update program once
					updated.add(p.getParent());
					p.getParent().setDuration(p.getParent().getDuration() - 1);
				}
    			if (p.getParent().getDuration() == 0) {
    				releaseSwap(p.getParent()); 
    				b.setAllocated(null);
    			}
    		}
    	}
    }
    
    private void releaseSwap(ProcessComplete p) {
    	// Release programs components from swap
    	List<ProcessMemUnit> remove = new LinkedList<ProcessMemUnit>();

    	Iterator<ProcessMemUnit> it = swap.iterator();
    	while (it.hasNext()) {
    		ProcessMemUnit pc = it.next();
    		if (pc.getParent().equals(p)) remove.add(pc); 
    	}
    	
    	swap.removeAll(remove);
    }
    
    private void backup() {
    	// backup to restore initial state
    	swap.clear();
    	pqBkup.clear();
    	Iterator<ProcessMemUnit> it = processQueue.iterator();
		while (it.hasNext()) pqBkup.add(it.next().clone());

		bqBkup.clear();
		Iterator<MemPartition> itb = memory.iterator();
		while (itb.hasNext()) bqBkup.add(itb.next().clone());
    }

    /**
     * Restores model to initial state, time 0
     * 
     */
    public void restoreBackup() {
    	// Restore initial state (Time 0) from backup's
    	swap.clear();
    	processQueue.clear();
    	processQueue.addAll(pqBkup);
    	memory.clear();
    	memory.addAll(bqBkup);
    	pqBkup.clear();
    	bqBkup.clear();
    	//backup();
    }
}

