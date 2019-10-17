package edu.upc.fib.ossim.memory.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Memory Management Strategy implementation for Variable-size partitions algorithm
 * 
 * @author Alex Macia
 */
public class MemStrategyVAR extends MemStrategyAdapterCONT {
	
	public MemStrategyVAR(String policy) {
		super(policy);
	}

	/**
	 * Gets Variable-size partitions algorithm information including allocation policy   
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return super.getAlgorithmInfo("me_23");
	}

	/**
	 * Returns initial algorithm partition size. All memory. 
	 * 
	 * @param OSsize		operating system size	
	 * @param memory_size	memory size
	 * 
	 * @return  initial algorithm partition size. All memory. 
	 */
	public int getInitPartitionSize (int OSsize,  int memory_size) {
		return memory_size;
	}
	
	/**
	 * No validation is needed in this strategy
	 * 
	 */
	public void validateMemory(List<MemPartition> memory, int memory_size) { }
	
	/**
     * Compacts and merge free memory partitions
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
	 * Allocates allocate process into candidate partition, if allocate size < candidate size, divides partition  
	 *
	 * @param memory		partitions linked list (memory)  
	 * @param candidate		candidate partition
	 * @param allocate		process to allocate
	 */
	public void allocateCandidate(List<MemPartition> memory, MemPartition candidate, ProcessMemUnit allocate) {
		memory.remove(candidate);
		int size = candidate.getSize();
		candidate.setSize(allocate.getSize());   			
		candidate.setAllocated(allocate);
		memory.add(candidate);
		if (size > candidate.getSize()) {
			// Create empty partition
			MemPartition b = new MemPartition(candidate.getStart()+candidate.getSize(), size - candidate.getSize());
			memory.add(b);
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
