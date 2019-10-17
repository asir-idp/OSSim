package edu.upc.fib.ossim.memory.model;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Memory Management Strategy implementation for Segmentation algorithm
 * 
 * @author Alex Macia
 */
public class MemStrategySEG extends MemStrategyAdapterNOCONT {
	/**
	 * Gets Segmentation algorithm information   
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("me_25"); 
	}

	/**
	 * Initializes memory, creates a segment sized as operating system and allocates it.   
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param strSO			operating system process name
	 * @param size			operating system size	
	 * @param color			operating system background color
	 * @param memory_size	memory size
	 */
	public void initMemory(List<MemPartition> memory, String strSO, int size,  Color color, int memory_size) {
		memory.clear();
    	
    	// Create all memory partition
		MemPartition m = new MemPartition(0, memory_size);
		memory.add(m);
		
		//	Add SO.
		ProcessComplete so = new ProcessComplete(0, strSO, size, -1, color);
		
		ProcessComponent pc = new ProcessSegment(so, 0, size, true);
		so.addBlock(pc);
		
		try {
			allocateProcess(memory, null, so, memory_size);
		} catch (Exception e) {
			System.out.println("Error initializing memory - non contiguous memory management (Segmentation)");
			e.printStackTrace();
		}
	}

	/**
	 * Returns segment description: code, stack or data  
	 * 
	 * @param component		process component to get information of
	 * @return	segment description
	 */
	public String getProcessComponentInfo(ProcessMemUnit component) {
		int bid = ((ProcessComponent) component).getBid();
		return ProcessSegment.getKey(bid);
	}
	
	/**
	 * Gets process size info. Painter view. 
	 * Size for each segment  (eg Segs. 10+5+3 u.)
	 * 
	 * @return size info
	 */
	public String getComponentSizeInfo(ProcessComplete p) {
		String res = "Segs." + " ";
		for (int i=0; i< p.getNumBlocks(); i++) {
			ProcessComponent pc = p.getBlock(i);
			res += pc.getSize();
			if (i != p.getNumBlocks() - 1) res += "+"; 
		}
		res += " u.";
		return res;
	}

	/**
	 * Returns data from a process' segments. For each segments: description (code, stack, data), size and if is load into memory  
	 * 
	 * @param process	process to get components data of
	 * @return	data from a process' segments
	 */
	public Vector<Vector<Object>> getProcessComponentsData(ProcessMemUnit process) {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		for (int i = 0; i < ((ProcessComplete) process).getNumBlocks(); i++) {
			ProcessMemUnit block = ((ProcessComplete) process).getBlock(i);
			
			Vector<Object> row = new Vector<Object>();
			int bid = ((ProcessComponent) block).getBid();
			row.add(ProcessSegment.getKey(bid));	// Adds String indicating segment 
			row.add(block.getSize());
			row.add(((ProcessComponent) block).isLoad());
			
			data.add(row);
		}
		return data;
	}

	/**
	 * Returns memory occupation table header: address, segment size, segment description, pid, name, process size, duration  
	 * 
	 * @return	memory occupation table header
	 */
	public Vector<Object> getTableHeaderInfo() {
		// Block information table header 
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("me_35")); // Address
		header.add(Translation.getInstance().getLabel("me_36")); // Size (Segment)
		header.add(Translation.getInstance().getLabel("me_30")); // PID
		header.add(Translation.getInstance().getLabel("me_75")); // Segment
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
		info.add(new ColorCell("" + m.getStart(), Color.WHITE));
		info.add(new ColorCell("" + m.getSize(), Color.WHITE));
		if (m.getAllocated() != null) info.addAll(m.getAllocated().getInfo());
		else {
			for (int i=2; i<getTableHeaderInfo().size();i++) info.add(new ColorCell("", Color.WHITE));
		}
		return info;
	}	
	
	/**
	 * Returns process form table header. Process segments table: segment description, size and if is initially load   
	 * 
	 * @return	process form table header
	 */
	public Vector<Object> getFormTableHeader() {
		Vector<Object> header = new Vector<Object>();
    	header.add(Translation.getInstance().getLabel("me_75"));
    	header.add(Translation.getInstance().getLabel("me_77"));
    	header.add(Translation.getInstance().getLabel("me_76"));
		return header;
	}

	/**
	 * Returns process form table initial data. Any process has 3 segments: code, data and stack, 
	 * initially every segment would be load into memory and its size is 1 kb   
	 * 
	 * @return 	process form table initial data
	 */
	public Vector<Vector<Object>> getFormTableInitData() {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    	Vector<Object> row0 = new Vector<Object>();
    	row0.add(Translation.getInstance().getLabel("me_72"));
    	row0.add(1);
    	row0.add(true);
    	data.add(row0);
    	Vector<Object> row1 = new Vector<Object>();
    	row1.add(Translation.getInstance().getLabel("me_73"));
    	row1.add(1);
    	row1.add(true);
    	data.add(row1);
    	Vector<Object> row2 = new Vector<Object>();
    	row2.add(Translation.getInstance().getLabel("me_74"));
    	row2.add(1);
    	row2.add(true);
    	data.add(row2);
		return data;
	}
	
	/**
	 * Returns process allocation tables header, process' segments information: segment description, size, 
	 * start memory address and a valid field that indicates if segment is load or it is in the backing store  
	 *  
	 * @return	process allocation tables header      
	 */
	public Vector<Object> getMemProcessTableHeader() {
		Vector<Object> header = new Vector<Object>();
    	header.add(Translation.getInstance().getLabel("me_75"));	// Segment
    	header.add(Translation.getInstance().getLabel("me_77"));	// Size
    	header.add(Translation.getInstance().getLabel("me_35"));	// Address
    	header.add(Translation.getInstance().getLabel("me_39"));	// Valid?
		return header;
	}
	
	/**
	 * Returns process allocation tables data, process' segments information: segment description, size, 
	 * start memory address and a valid field that indicates if segment is load or it is in the backing store 
	 *  
	 * @param memory	partitions linked list (memory)  
	 * @param p			process to get data of			
	 * 
	 * @return	process allocation tables data
	 */
	public Vector<Vector<Object>> getMemProcessTableData(List<MemPartition> memory, ProcessComplete p) {
    	Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> segment = null;
		Iterator<MemPartition> it;
		MemPartition m;
		
    	for (int i = 0; i < p.getNumBlocks(); i++) {
    		int start = -1;
    		ProcessComponent child = (ProcessComponent) p.getBlock(i);
    		segment = new Vector<Object>();
    		if (child.isLoad()) {
    			// Search segment
    			it = memory.iterator();
    			while (it.hasNext() && start < 0) {
    				m = it.next();
    				if (m.getAllocated() != null && m.getAllocated().equals(child)) start = m.getStart(); 
    			}
    		}
    		segment.add(new ColorCell(ProcessSegment.getKey(child.getBid()), Color.WHITE)); // Segment
			segment.add(new ColorCell("" + child.getSize(), Color.WHITE)); // Size.
			if (start < 0) {
    			segment.add(new ColorCell("", Color.WHITE)); // Address.
    			segment.add(new ColorCell("i", Color.WHITE)); // Invalid
    		} else {
    			segment.add(new ColorCell("" + start, Color.WHITE)); // Address.
    			segment.add(new ColorCell("v", Color.WHITE)); // Valid
    		}
    		data.add(segment);
    	}
	
		return data;
	}
	
	/**
	 * Adds segments to a process. 
	 * 
	 * @param p		process
	 * @param d		segments data 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addProcessComponents(ProcessComplete p,  Vector<Vector> d) {
		for (int i = 0; i< d.size(); i++) {
			Vector<Object> data = d.elementAt(i);
			ProcessComponent pc = new ProcessSegment(p, i, (Integer) data.elementAt(1), (Boolean) data.elementAt(2));
			p.addBlock(pc);
		}
	}

	/**
     * Compacts and merge free memory holes between segments
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param memory_size	memory size
	 */
	public void compaction(List<MemPartition> memory, int memory_size) {
		List<MemPartition> progsAllocated = new LinkedList<MemPartition>();
		Object[] memOrdered = memory.toArray();
		Arrays.sort(memOrdered);
		int i = 0;
		while (i<memOrdered.length) {
			MemPartition partition = (MemPartition) memOrdered[i];
    		if (partition.getAllocated() != null) {
    			progsAllocated.add(partition);
    		}
    		i++;
		}
		
		int end = 0;
		memory.clear(); // Empty memory
		Iterator<MemPartition> it = progsAllocated.iterator();
		while (it.hasNext()) {
			MemPartition memProg = it.next();
			memProg.setStart(end);
			end += memProg.getSize();
			memory.add(memProg);
		}
		
		if (end < memory_size) {
			// Create partition with all available memory 
			MemPartition b = new MemPartition(end, memory_size - end);
			memory.add(b);
		}
	}
	
	/**
	 * Allocates all process' segments into memory, 
	 * some of them may be initially into backing store (not loaded),  
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param swap			processes into backing store linked list (swap)  
	 * @param allocate		process to allocate
	 * @param memory_size	memory size
	 * @param policy		unused
	 * 
	 * @throws SoSimException	all process' segments can not be allocated
	 */
	public void allocateProcess(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit allocate, int memory_size) throws SoSimException {
    	ProcessComplete parent = allocate.getParent();
    	ProcessMemUnit child;
    	List<MemPartition> candidates = new LinkedList<MemPartition>();
    	MemPartition b;
    	
    	// Back up Memory
    	List<MemPartition> bq_bkup = new LinkedList<MemPartition>();
		Iterator<MemPartition> it = memory.iterator();
		while (it.hasNext()) bq_bkup.add(it.next().clone());
    	
    	
    	// Checking memory holes
    	for (int j = 0; j < parent.getNumBlocks(); j++) {
    		child = parent.getBlock(j);
    		if (((ProcessComponent) child).isLoad()) { // Should be allocated
    			Object[] memOrdered = memory.toArray();
    	    	Arrays.sort(memOrdered);
    			int i = 0;	
        		MemPartition candidate = null;
    			while (i<memOrdered.length && candidate == null) {
    				MemPartition partition = (MemPartition) memOrdered[i];
    	    		if (!candidates.contains(partition) && partition.getAllocated() == null && partition.getSize() >= child.getSize()) {
    	    			// Allocates only necessary
    	    			candidate = partition;// First candidate
    	    		}
    	    		i++;
    			}
        		if (candidate != null) {
        			// Allocates only necessary
        			memory.remove(candidate);
        			int size = candidate.getSize();
        			candidate.setSize(child.getSize());   			
        			candidate.setAllocated(child);
        			memory.add(candidate);
        			if (size > candidate.getSize()) {
        				// Create empty partition
        				b = new MemPartition(candidate.getStart()+candidate.getSize(), size - candidate.getSize());
        				memory.add(b);
        			}
        		}
        		else {
        		// Restore memory
        	    	memory.clear();
        			it = bq_bkup.iterator();
        			while (it.hasNext()) memory.add(it.next().clone());
        			throw new SoSimException("me_08");
        		}
    		} else swap.add(child); // Not loaded
    	}
	}
	
	/**
     * Allocates swapped process segment from backing store into memory 
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param swap			processes into backing store linked list (swap)  
	 * @param swapped		swapped process to allocate
	 * @param memory_size	memory size
	 * 
	 * @throws SoSimException	process segment can not be allocated
	 * 
	 */
	public void swapInProcessComponent(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit swapped, int memory_size) throws SoSimException {
		Object[] memOrdered = memory.toArray();
    	Arrays.sort(memOrdered);
    	MemPartition b;
    	
    	int i = 0;	
    	MemPartition candidate = null;
		while (i<memOrdered.length && candidate == null) {
    		MemPartition partition = (MemPartition) memOrdered[i];
    		if (partition.getAllocated() == null && partition.getSize() >= swapped.getSize()) { 
    			candidate = partition;// First candidate
    		}
    		i++;
		}
		
		if (candidate != null) {
			// Allocates only necessary
			memory.remove(candidate);
			int size = candidate.getSize();
			candidate.setSize(swapped.getSize());   			
			candidate.setAllocated(swapped);
			((ProcessComponent) swapped).setLoad(true);
			memory.add(candidate);
			if (size > candidate.getSize()) {
				// Create empty partition
				b = new MemPartition(candidate.getStart()+candidate.getSize(), size - candidate.getSize());
				memory.add(b);
			}
		}
		else throw new SoSimException("me_08");
	}

	/**
	 * Returns address translation, given a process logical address gets its corresponding physical address, 
	 * if segment is not into memory gives a segment fault
	 * 
	 * @param b				memory partition containing process
	 * @param logicalAddr	process logical address, belong to process logical space 
	 * @param memory		partitions linked list (memory)
	 * 
	 * @return	address translation or segment fault error. format "@9999"
	 */
	public String getAddTransPhysical(MemPartition b, int logicalAddr, List<MemPartition> memory) {
		// logicalAddr belongs to process logical space

		ProcessComplete p = b.getAllocated().getParent();
		// Segment ?
		
		int startsegment = 0;
		ProcessComponent programSegment = null; 
		boolean found = false;
		int i = 0;
		while (i < p.getNumBlocks() && !found) {
			programSegment = p.getBlock(i);
			if (startsegment + programSegment.getSize() > logicalAddr) found = true;
			else startsegment += programSegment.getSize(); 
			i++;
		}

		int offset = logicalAddr - startsegment;

		// Segment not loaded 
		if (!programSegment.isLoad()) return Translation.getInstance().getLabel("me_87"); // segment fault 

		Iterator<MemPartition> it = memory.iterator();
		MemPartition block = null;
		found = false;
		while (it.hasNext() && !found) {
			block = it.next();
			if (block.getAllocated() != null && block.getAllocated().equals(programSegment)) found = true;  
		}
		if (found) return "@" + (block.getStart() + offset);
		else return ""; // never
	}	
}
