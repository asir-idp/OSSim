package edu.upc.fib.ossim.disk.model;

import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import edu.upc.fib.ossim.utils.SoSimException;


/**
 * Disk Scheduling Model (Model - View - Presenter Pattern). Different scheduling algorithm are implemented 
 * through Strategy Pattern. Model manage a maximum of<code>"MAX_REQUESTS"</code>.    
 * 
 * @author Alex Macia
 */
public class ContextDisk {
	public static final int MAX_REQUESTS = 40;
	private static final int HEADS = 1;
	private static final int CYLINDERS = 16; 
	private static final int SECTORS = 12;  

	private DiskStrategy algorithm;
	private List<DiskBlockRequest> requests;	// Block request
	private List<DiskBlockRequest> requestsArriving;	// Block future requests 
	private List<DiskBlockRequest> requestServed;	// Block requests already served
	private List<DiskBlockRequest> rqBkup; 	// Block request backup to restore initial state  
	private List<DiskBlockRequest> raBkup; 	// Block future requests backup to restore initial state
	private DiskBlockRequest selectedRequest;
	private int initHeadPosition;
	
	/**
	 * Constructs a ContextDisk: sets an initial algorithm strategy,
	 * 
	 * @param algorithm		default algorithm
	 */
    public ContextDisk(DiskStrategy algorithm) {
        this.algorithm = algorithm;
        DiskState.getInstance().initState(SECTORS, CYLINDERS);
        requests = new LinkedList<DiskBlockRequest>();
        requestsArriving = new LinkedList<DiskBlockRequest>();
        requestServed = new LinkedList<DiskBlockRequest>();
        rqBkup = new LinkedList<DiskBlockRequest>();
        raBkup = new LinkedList<DiskBlockRequest>();
    }
 
    /**
     * Returns requests count, total number already served, not yet served and incoming requests
     * 
     * @return	requests count
     */
	public int getRequestCount() {
		return requests.size() + requestsArriving.size() + requestServed.size();
	}
    
	/**
	 * Gets number of cylinders
	 * 
	 * @return number of cylinders
	 */
	public static int getCylinders() {
		return CYLINDERS;
	}

	/**
	 * Gets number of sectors
	 * 
	 * @return number of sectors
	 */
	public static int getSectors() {
		return SECTORS;
	}
	
	/**
	 * Returns total blocks. HEADS*CYLINDERS*SECTORS
	 * 
	 * @return total blocks
	 */
	public static int getNBLOCKS() {
		return HEADS*CYLINDERS*SECTORS;
	}
	
	 /**
     * Change algorithm strategy
     * 
	 * @param algorithm		default algorithm
     * 
     */
	public void setAlgorithm(DiskStrategy algorithm){
    	this.algorithm = algorithm;
    }

	/**
	 * Gets selected request identifier
	 * 
	 * @return selected request identifier
	 */
	public int getSelectedRequestId() {
		return selectedRequest.getBid();
	}

	/**
	 * Gets selected request data: block number, time and color
	 * 
	 * @return selected request data
	 */
	public Vector<Object> getSelectedRequestData() {
		if (selectedRequest == null) return null; 
		Vector<Object> data = new Vector<Object>();
		data.add(getNBLOCKS()); // MAx blocks
		data.add(selectedRequest.getBid());
		data.add(selectedRequest.getInit());
		data.add(selectedRequest.getColor());
		return data;
	}
	
	/**
	 * Selects a requests identified by bid
	 * 
	 * @param bid requests identifier
	 * 
	 * @return request exist
	 */
	public boolean setSelectedRequest(int bid) {
		selectedRequest = getByBID(bid);
		return selectedRequest != null;
	}

	/**
	 * Sets head position (block number)
	 * 
	 * @param headPosition	position
	 */
    public void setHeadPosition(int headPosition) {
    	DiskState.getInstance().setHeadPosition(headPosition);
	}

    /**
     * Gets head position (block number)
     * 
     * @return	head position
     */
    public int getHeadPosition() {
		return DiskState.getInstance().getHeadPosition();
	}

    /**
     * Returns list iterator with queued requests identifiers (block numbers) 
     * 
     * @return	list iterator
     */
    public Iterator<Integer> iteratorRequests() {
		// Returns LinkedList with ready requests bid's
		LinkedList<Integer> queueInteger = new LinkedList<Integer>();
		
		Iterator<DiskBlockRequest> it = requests.iterator();
		while (it.hasNext()) {
			queueInteger.add(new Integer(it.next().getBid()));
		}
		
		return queueInteger.iterator();
	}

    /**
     * Returns list iterator with served requests identifiers (block numbers) 
     * 
     * @return	list iterator
     */
	public Iterator<Integer> iteratorRequestsServed() {
		// Returns LinkedList with served request ordered 
		LinkedList<Integer> queueInteger = new LinkedList<Integer>();

		Iterator<DiskBlockRequest> it = requestServed.iterator();
		while (it.hasNext()) {
			DiskBlockRequest request = it.next();
			
			Vector<Integer> limits = request.getLimits();
			for(int i = 0; i < limits.size(); i++) {
				queueInteger.add(limits.get(i));
			}
			queueInteger.add(new Integer(request.getBid()));
		}
		
		return queueInteger.iterator();
	}
	
	/**
	 * Gets served requests count and possible limits reached by head (SCAN, CSCAN)
	 * 
	 * @return	served requests count
	 */
	public int getRequestsServed() {
		// Returns total served requests
		int total = 0;
		Iterator<DiskBlockRequest> it = requestServed.iterator();
		while (it.hasNext()) {
			DiskBlockRequest request = it.next();
			total += request.getLimits().size();
			total++;
		}
		return total;
	}

	private DiskBlockRequest getByBID(int bid) {
		// Returns block's ID queued and BID = pid or null if not exists
		DiskBlockRequest p;

		Iterator<DiskBlockRequest> it = requests.iterator();
		while (it.hasNext()) {
			p = it.next();
			if (p.getBid() == bid) return p;
		}

		it = requestServed.iterator();
		while (it.hasNext()) {
			p = it.next();
			if (p.getBid() == bid) return p;
		}

		it = requestsArriving.iterator();
		while (it.hasNext()) {
			p = it.next();
			if (p.getBid() == bid) return p;
		}
		
		return null;
	}

	/**
	 * Gets request information        
	 * 
	 * @param pid	request identifier
	 * @return	request information
	 */
	public Vector<String> getInfo(int pid) {
		Vector<String> info = new Vector<String>();
		info.add(new Integer(pid).toString());
		return info;
	}
	
	/**
	 * Gets request color        
	 * 
	 * @param bid	request identifier (block number)
	 * @return	request color
	 */
	public Color getColor(int bid) {
		if (getByBID(bid) == null) return null;
		else return getByBID(bid).getColor();
	}

	/**
	 * Gets request time        
	 * 
	 * @param bid	request identifier (block number)
	 * @return	request time
	 */
	public int getTinit(int bid) {
		return getByBID(bid).getInit();
	}

	/**
	 * Returns scheduling information table header 
	 * 
	 * @return	scheduling information table header
	 * 
	 * @see DiskBlockRequest#getHeaderInfo()
	 */
	public Vector<Object> getTableHeaderInfo() {
		return DiskBlockRequest.getHeaderInfo();
	}
	
	/**
	 * Returns scheduling information table data 
	 * 
	 * @return	scheduling information table data
	 * 
	 * @see DiskBlockRequest#getBlockInfo(int)
	 */
	public Vector<Vector<Object>> getTableInfoData() {
		// General information data properly ordered 
		
		// Served first. Ordered by accumulate, init, block id
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		Iterator<DiskBlockRequest> it = requestServed.iterator();
		while (it.hasNext()) {
			DiskBlockRequest d = it.next(); 
			data.add(d.getBlockInfo(SECTORS));
		}

		// Requests. List order
		it = requests.iterator();
		while (it.hasNext()) {
			DiskBlockRequest d = it.next(); 
			data.add(d.getBlockInfo(SECTORS));
		}

		// Arriving requests last. List order
		it = requestsArriving.iterator();
		while (it.hasNext()) {
			DiskBlockRequest d = it.next(); 
			data.add(d.getBlockInfo(SECTORS));
		}

		if (data.size() == 0) return null;
		return data;
	}

	/**
	 * Returns scheduling (requests) xml information  
	 * 
	 * @return	scheduling (requests) xml information
	 * 
	 * @see DiskBlockRequest#getRequestXMLInfo()
	 */
	public Vector<Vector<Vector<String>>> getXMLDataRequests() {
		// Save request and arriving not served.
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();
		Iterator<DiskBlockRequest> it = requests.iterator();
		while (it.hasNext()) {
			data.add(it.next().getRequestXMLInfo());
		}
		it = requestsArriving.iterator();
		while (it.hasNext()) {
			data.add(it.next().getRequestXMLInfo());
		}
		return data;
	}

	/**************************************************************************************************/
	/*************************************  Specific Strategies ***************************************/
	/**************************************************************************************************/
	
	/**
	 * Adds a new request that will be proceeded at a concrete time
	 * 
	 * @param d		request data: block number, time, color
	 * @param time	simulation current time
	 * @throws SoSimException	requested block already exists or request time is previous to current time	
	 */
    public void addRequest(Vector<Object> d, int time) throws SoSimException {
    	// Add Request b to its queue 
    	backup();
    	DiskBlockRequest b = new DiskBlockRequest((Integer) d.get(0), (Integer) d.get(1), (Color) d.get(2));
    	if (getByBID(b.getBid()) != null) throw new SoSimException("dk_04"); // Block requests already exist
    	if (b.getInit() < time) throw new SoSimException("dk_02"); 
    	if (b.getInit() == time) requests.add(b);
    	if (b.getInit() > time) requestsArriving.add(b);
   		selectedRequest = b;
    }
    
	/**
	 * Updates a request, manage possible queue movement
	 * 
	 * @param d		request data: block number, time, color
	 * @param time	simulation current time
	 * @throws SoSimException	requested block already exists or request time is previous to current time	
	 */
    public void updateRequest(Vector<Object> d, int time) throws SoSimException {
    	// Add Request b to its queue 
    	backup();
    	DiskBlockRequest b = new DiskBlockRequest((Integer) d.get(0), (Integer) d.get(1), (Color) d.get(2));
    	if (b.getBid() != selectedRequest.getBid() && getByBID(b.getBid()) != null) throw new SoSimException("dk_04"); // Block requests already exist
    	if (b.getInit() < time) throw new SoSimException("dk_02"); 
    	
    	int position = 0;
    	if (requests.contains(selectedRequest)) {
    		position = requests.indexOf(selectedRequest);
    		requests.remove(selectedRequest);
    		if (b.getInit() == time) requests.add(position, b); // Same position
    		else requestsArriving.add(b); // At end 
    	}
    	if (requestsArriving.contains(selectedRequest)) {
    		position = requestsArriving.indexOf(selectedRequest);
    		requestsArriving.remove(selectedRequest);
    		if (b.getInit() == time) requests.add(b); // At end
    		else requestsArriving.add(position, b); // Same position 
    	}
   		selectedRequest = b;
    }

    /**
     * Removes selected request
     * 
     * @param time	simulation current time	
     * @throws SoSimException request time is previous to current time	
     */
    public void removeRequest(int time) throws SoSimException {
    	// Removes request selected from its queue
    	backup();
    	if (selectedRequest.getInit() < time) throw new SoSimException("dk_03"); 
    	requests.remove(selectedRequest);
    	requestsArriving.remove(selectedRequest);
    }
	
    /**
     * Gets current algorithm information
     * 
     * @return current algorithm information
     * 
     * @see DiskStrategy#getAlgorithmInfo()
     */
    public String getAlgorithmInfo() {
    	return algorithm.getAlgorithmInfo();
    }
    
    /**
     * Forwards simulation subtime 1 unit. Serves requests on head and 
     * moves head forward (keeping it in the cylinder)
     * 
     * @see DiskStrategy#getNextRequest(List, DiskState, int)
     */
    public void forwardDecimal() {
    	// Common behavior
    	DiskBlockRequest next = algorithm.getNextRequest(requests);
    	if (next != null && next.getBid() == DiskState.getInstance().getHeadPosition()) {
    		serveRequest(next);
    	}

    	int headCylinder = DiskState.getInstance().getHeadPosition()/DiskState.getInstance().getSectors();
    	int nextSector = DiskState.getInstance().getHeadPosition()+1;
    	
    	DiskState.getInstance().setHeadPosition(nextSector);
    	
    	int nextHeadCylinder = DiskState.getInstance().getHeadPosition()/DiskState.getInstance().getSectors();
    	
    	if (nextHeadCylinder > headCylinder) { // keeps head into cylinder
    		nextSector = DiskState.getInstance().getHeadPosition()-DiskState.getInstance().getSectors();
    		DiskState.getInstance().setHeadPosition(nextSector); 
    	}
    }
    
    /**
     * Forwards simulation time 1 unit. Queue incoming requests which scheduled time
     * is current time, moves head seeking next request.
     * 
     * @param time		current simulation time
     * 
     * @return simulation ends
     * 
     * @see DiskStrategy#getNextRequest(List, DiskState, int)
     * @see DiskStrategy#moveHeadPosition(DiskBlockRequest, DiskState)
     */
    public void forwardTime(int time) {
    	// Common behavior
    	if (time == 0) {
    		backup();
    	} else {
        	queueArriving(time); // Add arriving requests init = time	
        	
    		// Requests depends on the variable head position
    		// Specific behavior
    		DiskBlockRequest next = algorithm.getNextRequest(requests);  

    		algorithm.moveHeadPosition(next);
    	}
    }
	
	private void queueArriving(int time) {
		// Add arriving requests init = time 
		List<DiskBlockRequest> pending = new LinkedList<DiskBlockRequest>();
		
		Iterator<DiskBlockRequest> it = requestsArriving.iterator();
		while (it.hasNext()) {
			DiskBlockRequest b = it.next();
			if (b.getInit() == time) pending.add(b);
		}

		it = pending.iterator();
		while (it.hasNext()) {
			DiskBlockRequest b = it.next();
			requestsArriving.remove(b);
			requests.add(b);
		}
	}
    
    private void serveRequest(DiskBlockRequest request) {
    	algorithm.serveRequest(request);
    	
    	requests.remove(request);
		requestServed.add(request);

    	// Order by accumulate asc movement desc
		Collections.sort(requestServed);
    }
    
    /**
     * Back up's scheduling initial state, head position and requests at time = 0
     * 
     */
    public void backup() {
    	// backup to restore initial state
    	initHeadPosition = DiskState.getInstance().getHeadPosition();
    	
    	rqBkup.clear();
    	Iterator<DiskBlockRequest> it = requests.iterator();
		while (it.hasNext()) rqBkup.add(it.next().clone());

    	raBkup.clear();
    	it = requestsArriving.iterator();
		while (it.hasNext()) raBkup.add(it.next().clone());
    }

    /**
     * Restores model to initial state, time 0
     * 
     */
    public void restoreBackup() {
    	// Restore initial state (Time 0) from backup's
    	requestServed.clear();
    	
    	requests.clear();
    	Iterator<DiskBlockRequest> it = rqBkup.iterator();
		while (it.hasNext()) requests.add(it.next().clone());

		requestsArriving.clear();
    	it = raBkup.iterator();
		while (it.hasNext()) requestsArriving.add(it.next().clone());

		DiskState.getInstance().initState(SECTORS, CYLINDERS);
		DiskState.getInstance().setInitHeadPosition(initHeadPosition);
		DiskState.getInstance().setHeadPosition(initHeadPosition);
    }
}

