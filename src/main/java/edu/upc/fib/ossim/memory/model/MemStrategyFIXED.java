package edu.upc.fib.ossim.memory.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.upc.fib.ossim.utils.SoSimException;

/**
 * Memory Management Strategy implementation for Fixed-size partitions algorithm
 * 
 * @author Alex Macia
 */
public class MemStrategyFIXED extends MemStrategyAdapterCONT {

	public MemStrategyFIXED(String policy) {
		super(policy);
	}

	/**
	 * Gets Fixed-size partitions algorithm information including allocation policy   
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return super.getAlgorithmInfo("me_22");
	}
	
	/**
	 * Returns initial algorithm partition size. Only Operating System partition. 
	 * 
	 * @param OSsize		operating system size	
	 * @param memory_size	memory size
	 * 
	 * @return  initial algorithm partition size. Operating System size. 
	 */
	public int getInitPartitionSize (int OSsize,  int memory_size) {
		return OSsize;
	}
	
	
	/**
	 * Partitions always can be selected at time = 0
	 * 
	 * @return	true
	 */
	public boolean isSelectable() {
		return true;
	}
	
	/**
	 * Returns false, Fixed partitions algorithm has no external fragmentation
	 * 
	 * @return false
	 */
	public boolean hasExternalFragmentation() {
		return false;
	}
	
	/**
	 * Initial memory validation, all memory must be divided into partitions 
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param memory_size	memory size
	 * @throws SoSimException	memory not completely partitioned
	 */
	public void validateMemory(List<MemPartition> memory, int memory_size) throws SoSimException {
		// All memory must be partitioned
		Object[] memOrdered = memory.toArray();
    	Arrays.sort(memOrdered);
    	
		MemPartition partition = (MemPartition) memOrdered[0];
		int end = partition.getStart() + partition.getSize();

    	for(int i=1;i<memOrdered.length;i++) {
			partition = (MemPartition) memOrdered[i];
			if (end != partition.getStart()) throw new SoSimException("me_06");
			end += partition.getSize();
		}
    	
    	if (end != memory_size) throw new SoSimException("me_06");
	}

	/**
	 * No compaction is needed in this strategy
	 * 
	 */
	public void compaction(List<MemPartition> memory, int memory_size) { // Do nothing
	}
	
	/**
	 * Allocates allocate process into candidate partition
	 *
	 * @param memory		partitions linked list (memory)  
	 * @param candidate		candidate partition
	 * @param allocate		process to allocate
	 */
	public void allocateCandidate(List<MemPartition> memory, MemPartition candidate, ProcessMemUnit allocate) {
		 candidate.setAllocated(allocate);
	}
	
	/**
	 * Returns initial memory xml information 
	 * 
	 * @param memory main memory partitions list 
	 * 
	 * @return	initial memory xml information
	 * 
	 * @see MemPartition#getMemPartitionXMLInfo()
	 */
	public  Vector<Vector<Vector<String>>> getXMLDataMemory(List<MemPartition> memory) {
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();
		Iterator<MemPartition> it = memory.iterator();
		while (it.hasNext()) {
			MemPartition b = it.next();
			if (b.getStart() != 0) data.add(b.getMemPartitionXMLInfo()); // Except SO
		}
		return data;
	}
}
