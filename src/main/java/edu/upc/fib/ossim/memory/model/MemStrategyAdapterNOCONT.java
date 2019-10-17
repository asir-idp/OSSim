package edu.upc.fib.ossim.memory.model;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.upc.fib.ossim.utils.SoSimException;

/**
 * Abstract class that implements interface MemStrategy (Non Contiguous memory management)
 * 
 * @author Àlex
 */
public abstract class MemStrategyAdapterNOCONT implements MemStrategy {
	/**
	 * Non contiguous algorithms has nothing to do with policy 
	 */
	public void setPolicy(String policy) { }
		
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
	 * Removes all process' components from memory
	 * 
	 * @param memory	partitions linked list (memory)  	
	 * @param b			memory partition containing a process segment 
	 */
	public void removeProcessInMemory(List<MemPartition> memory, MemPartition b) {
		// Remove all program blocks from memory
    	int id = b.getAllocated().getPid(); 
    	Iterator<MemPartition> it = memory.iterator();
    	
    	while (it.hasNext()) {
    		MemPartition block = it.next();
    		if (block.getAllocated() != null && block.getAllocated().getPid() == id) block.setAllocated(null);
    	}
	}
	
	/**
	 * No validation is needed in this strategy
	 * 
	 */
	public void validateMemory(List<MemPartition> memory, int memory_size) throws SoSimException {
	}
	
	/**
     * Moves a process component to backing store
	 * 
	 * @param memory	partitions linked list (memory)
	 * @param swap		processes into backing store linked list (swap)  
	 * @param partition	memory partition allocating process component to swap	
	 * 
	 * @throws SoSimException	partition does not allocate any process' component
	 */
	public void swapOutProcess(List<MemPartition> memory, List<ProcessMemUnit> swap, MemPartition partition) throws SoSimException {
		if (partition.getAllocated() == null)  throw new SoSimException("me_09");
		ProcessComponent child = (ProcessComponent) partition.getAllocated();
		
		swap.add(child);
		child.setLoad(false);
		partition.setAllocated(null);
	}
	
	/**
	 * Removes from memory all other process' pages that belongs to the same process as the page in the backing store.
	 * 
	 * @param memory	partitions linked list (memory)	
	 * @param swap		processes into backing store linked list (swap)  
	 * @param swapped	process pages in the backing store  
	 */
	public void removeSwappedProcessComponents(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit swapped) {
		int pid = swapped.getPid();
		Iterator<MemPartition> it = memory.iterator();
		while (it.hasNext()) {
			MemPartition block = it.next();
			if (block.getAllocated() != null && block.getAllocated().getPid() == pid) {
				block.setAllocated(null);
			}
    	}
		Object[] swapList = swap.toArray();
		for (int i=0; i<swapList.length; i++) {
			if (((ProcessMemUnit) swapList[i]).getPid() == pid) {
				swap.remove(swapList[i]);
			}
		}
	}
	
	/**
	 * No initial memory information needed 
	 * 
	 * @return	null
	 */
	public  Vector<Vector<Vector<String>>> getXMLDataMemory(List<MemPartition> memory) {
		return null;
	}
}
