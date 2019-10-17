package edu.upc.fib.ossim.memory.model;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

import edu.upc.fib.ossim.utils.SoSimException;


/**
 * Strategy Interface for memory management model (Strategy Pattern).
 * Any memory management algorithm must implement this strategy.  
 * 
 * @author Alex Macia
 */
public interface MemStrategy {
	/**
	 * Gets algorithm information including main settings values  
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo();
	
	/**
	 * Set algorithm allocation policy
	 */
	public void setPolicy(String policy);
	
	/**
	 * Initializes memory and allocates operating system 
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param strSO			operating system process name
	 * @param size			operating system size	
	 * @param color			operating system background color
	 * @param memory_size	memory size
	 */
	public void initMemory(List<MemPartition> memory, String strSO, int size, Color color, int memory_size);

	/**
	 * Partition can be selected at time = 0
	 * 
	 * @return	partition can be selected
	 */
	public boolean isSelectable();
	
	/**
	 * Returns true if algorithm has external fragmentation
	 * 
	 * @return true if algorithm has external fragmentation
	 */
	public boolean hasExternalFragmentation();
	
	/**
	 * Returns additional information for process components
	 * 
	 * @param component		process component to get information of
	 * @return	additional process component information
	 */
	public String getProcessComponentInfo(ProcessMemUnit component);
	
	/**
	 * Gets process size info. Painter view. 
	 * 
	 * @return size info
	 */
	public String getComponentSizeInfo(ProcessComplete p); 

	/**
	 * Returns data from a process' components. (Only non contiguous memory management strategies)  
	 * 
	 * @param process	process to get components data of
	 * @return	data from a process' components
	 */
	public Vector<Vector<Object>> getProcessComponentsData(ProcessMemUnit process);
	
	/**
	 * Returns memory occupation table header 
	 * 
	 * @return	memory occupation table header
	 */
	public Vector<Object> getTableHeaderInfo();
	
	/**
	 * Returns a partition occupation data
	 * 
	 * @param m			memory partition
	 * @return 	a partition occupation data
	 */
	public Vector<Object> getTableBlockInfo(MemPartition m);
	
	/**
	 * Returns process form table header. (Only non contiguous memory management strategies)  
	 * 
	 * @return	process form table header
	 */
	public Vector<Object> getFormTableHeader();
	
	/**
	 * Returns process form table initial data. (Only non contiguous memory management strategies)  
	 * 
	 * @return 	process form table initial data
	 */
	public Vector<Vector<Object>> getFormTableInitData();
	
	/**
	 * Returns process allocation tables header, pages table (pagination) or segments table (segmentation)
	 *  
	 * @return	process allocation tables header      
	 */
	public Vector<Object> getMemProcessTableHeader();
	
	/**
	 * Returns process allocation tables data, pages table (pagination) or segments table (segmentation)
	 *  
	 * @param memory	partitions linked list (memory)  
	 * @param p			process to get data of			
	 * 
	 * @return	process allocation tables data
	 */
	public Vector<Vector<Object>> getMemProcessTableData(List<MemPartition> memory, ProcessComplete p);
	
	/**
	 * Adds components to a process.  (Only non contiguous memory management strategies, in pagination components 
	 * are pages and in segmentation components are segments)  
	 * 
	 * @param p		process
	 * @param d		components data 
	 */
	@SuppressWarnings("rawtypes")
	public void addProcessComponents(ProcessComplete p,  Vector<Vector> d);
	
	/**
	 * Removes a process from memory, in non contiguous memory management strategies, also remove all other process components
	 * 
	 * @param memory	partitions linked list (memory)  	
	 * @param b			memory partition containing process
	 */
	public void removeProcessInMemory(List<MemPartition> memory, MemPartition b);
	
	/**
	 * Initial memory validation before simulation starts 
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param memory_size	memory size
	 * @throws SoSimException	any possible validation problem
	 */
	public void validateMemory(List<MemPartition> memory, int memory_size) throws SoSimException;
	
	/**
     * Compacts and merge free memory partitions
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param memory_size	memory size
	 */
	public void compaction(List<MemPartition> memory, int memory_size);
	
	/**
	 * Allocates a process into memory, in contiguous memory management strategies, an allocation policy determines
	 * the way a free partition is selected to allocate the process, in non contiguous memory management strategies allocates all its components,
	 * some of them may be initially into backing store (not loaded),  
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param swap			processes into backing store linked list (swap)  
	 * @param allocate		process to allocate
	 * @param memory_size	memory size
	 * 
	 * @throws SoSimException	process can not be allocated
	 */
	public void allocateProcess(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit allocate, int memory_size) throws SoSimException;
	
	/**
     * Allocates swapped process from backing store into memory. 
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param swap			processes into backing store linked list (swap)  
	 * @param swapped		swapped process to allocate
	 * @param memory_size	memory size
	 * 
	 * @throws SoSimException	process can not be allocated
	 */
	public void swapInProcessComponent(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit swapped, int memory_size) throws SoSimException;
	
	/**
     * Moves a process to backing store
	 * 
	 * @param memory	partitions linked list (memory)
	 * @param swap		processes into backing store linked list (swap)  
	 * @param partition	memory partition allocating process to swap	
	 * 
	 * @throws SoSimException	partition does not allocate any process
	 */
	public void swapOutProcess(List<MemPartition> memory, List<ProcessMemUnit> swap, MemPartition partition) throws SoSimException;
	
	/**
	 * Removes from memory all other process' components that belongs to the same process as the component in the backing store.
	 *  (Only non contiguous memory management strategies)  
	 * 
	 * @param memory	partitions linked list (memory)	
	 * @param swap		processes into backing store linked list (swap)  
	 * @param swapped	process component in the backing store  
	 */
	public void removeSwappedProcessComponents(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit swapped); 
	
	/**
	 * Returns address translation, given a process logical address gets its corresponding physical address or
	 * a translation error  
	 * 
	 * @param b				memory partition containing process
	 * @param logicalAddr	process logical address 
	 * @param memory		partitions linked list (memory)
	 * 
	 * @return	address translation
	 */
	public String getAddTransPhysical(MemPartition b, int logicalAddr, List<MemPartition> memory);
	
	
	/**
	 * Returns initial memory xml information 
	 * 
	 * @param memory main memory partitions list 
	 * 
	 * @return	initial memory xml information
	 */
	public  Vector<Vector<Vector<String>>> getXMLDataMemory(List<MemPartition> memory);
}
