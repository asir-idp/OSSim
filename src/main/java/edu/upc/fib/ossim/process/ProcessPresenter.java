package edu.upc.fib.ossim.process;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

import edu.upc.fib.ossim.process.model.ContextProcess;
import edu.upc.fib.ossim.process.model.ProcessStrategyFCFS;
import edu.upc.fib.ossim.process.model.ProcessStrategyPrio;
import edu.upc.fib.ossim.process.model.ProcessStrategyRR;
import edu.upc.fib.ossim.process.model.ProcessStrategySJF;
import edu.upc.fib.ossim.process.view.ArrivingPainter;
import edu.upc.fib.ossim.process.view.FormProcess;
import edu.upc.fib.ossim.process.view.IOPainter;
import edu.upc.fib.ossim.process.view.PanelProcess;
import edu.upc.fib.ossim.process.view.ProcessSettings;
import edu.upc.fib.ossim.process.view.ProcessorPainter;
import edu.upc.fib.ossim.process.view.QueuePainter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.InfoDialog;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.TimerPanel;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Process presenter manage concrete process scheduling behaviors, it builds main panel adding concrete components,
 * manage specific events and implements xml processing  
 * 
 * @author Alex Macia
 * 
 * @see Presenter
 */
public class ProcessPresenter extends Presenter {
	
	private static final int INFO_WIDTH = 790;
	private static final int INFO_HEIGTH = 200;
	private static final int READY_WIDTH = 750;
	private static final int READY_HEIGTH = 200;
	private static final int ARRIV_WIDTH = 219;
	private static final int ARRIV_HEIGTH = 191;
	private static final int IO_WIDTH = 219;
	private static final int IO_HEIGTH = 191;
	private static final int CPU_WIDTH = 300;
	private static final int CPU_HEIGHT = 190;
	public static final String PROCS_PAINTER = "processes";
	public static final String ARRIVING_PAINTER = "arriving";
	public static final String IO_PAINTER = "io";
	public static final String PROCESSOR_PAINTER = "processor";
	private Vector<String[]> menuItemsMem;
	private ContextProcess context;
	/**************************************************************************************************/
	/*************************************   Class  management  ***************************************/
	/**************************************************************************************************/
	
	/**
	 * @see Presenter
	 */
	public ProcessPresenter(boolean openSettings) {
		super(openSettings);
	}
	
	/**
	 * Constructs main panel adding all components and its pop up menus. Timer panel, three painters builds this panel, the ready queue, 
	 * the arriving processes queue and the processor, besides a settings dialog and an information dialog are initialized    
	 *  
	 */
	public PanelTemplate createPanelComponents() {
		timecontrols = new TimerPanel(this, TIMER_VELOCITY);
		createMenuItems();
		super.addPainter(new QueuePainter(this, menuItemsMem, "pr_01", READY_WIDTH, READY_HEIGTH), PROCS_PAINTER);
		super.addPainter(new ArrivingPainter(this, "pr_02", "", context.getArrivingHeaderInfo(), menuItemsMem, ARRIV_WIDTH, ARRIV_HEIGTH), ARRIVING_PAINTER);
		super.addPainter(new IOPainter(this, "pr_08", "", context.getIOHeaderInfo(), IO_WIDTH, IO_HEIGTH), IO_PAINTER);
		super.addPainter(new ProcessorPainter(this, CPU_WIDTH, CPU_HEIGHT), PROCESSOR_PAINTER);
		settings = new ProcessSettings(this, "sch_set");
		info = new InfoDialog(this, "pr_41", "sch_info", false, INFO_WIDTH, INFO_HEIGTH, context.getTableStatsInfo(), context.getTableHeaderInfo());
		return new PanelProcess(this, "pr_42");
	}
	
	private void createMenuItems() {
		menuItemsMem = new Vector<String[]>();
		String[] item1 = {"painter_upd", "pr_06", "update.png"};
		String[] item2 = {"painter_del", "pr_07", "trash.png"};
		menuItemsMem.add(item1);
		menuItemsMem.add(item2);
	}
	
	/**
	 * Maps ProcessPresenter concrete actions.
	 * 
	 * For instance<br/> <code>actions.put(action command, number);</code><br/>
	 * <ul>
	 * action command from component that generate the event<br/> 
	 * number between 20 and 30 
	 * </ul>
	 */
	public void mapActionsSpecific() {
		actions.put("panel_add",20);
		actions.put("painter_upd",21);
		actions.put("painter_del",22);
		actions.put("FCFS",23);
		actions.put("SJF",24);
		actions.put("PRI",25);
		actions.put("RR",26);
		actions.put("PRE",27);
		actions.put("MUL",28);
	}
	
	/**
	 * Creates process scheduling model. Model implements Strategy Pattern, it can be accessed 
	 * through <b>context</b>, different algorithms are implemented in concrete <b>strategies</b>. Initial
	 * context strategy is FCFS   
	 */
	public void createContext() {
		context = new ContextProcess(new ProcessStrategyFCFS());
	}
	
	/**************************************************************************************************/
	/*************************************   Events management  ***************************************/
	/**************************************************************************************************/
	
	/**
	 * Receive multiples events:
	 * <ul>
	 * <li>setting's quantum component change state event. Updates algorithm information (Round Robin algorithm) </li>
	 * <li>form's process duration component change state event. Updates number of bursts</li>
	 * <ul>
	 */
	public void stateChangedSpecific(ChangeEvent e) {
		JSpinner spin = (JSpinner) e.getSource();
		
		if ("quantum".equals(spin.getName())) { 
			// Quantum update algorithm information
			context.setAlgorithm(new ProcessStrategyRR(((ProcessSettings) settings).getQuantumSize()));
			panel.setLabel(getAlgorithmInfo());
		} 
		
		if ("bursts".equals(spin.getName())) { 
			// Change process bursts through bursts spinner	
			int rows;
			rows = (Integer) spin.getValue();
			((FormProcess) getForm()).updateBurstsTable(rows);
		} 
	}
	
	/** 
	 * Manage list events generated by information table and process form's bursts table, 
	 * On information table shows a pop up depending on selected row, on form's bursts table
	 * manage process' I/O - CPU bursts selection
	 * 
	 * @param e	list selection event
	 */
	 
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		
		if (!e.getValueIsAdjusting()) {
			if (lsm.getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
				// burst table
				JTable burstsTable = ((FormProcess) form).getTable();
				
				for (int r = 0; r < burstsTable.getRowCount(); r++) {
					if (burstsTable.isRowSelected(r) && !burstsTable.isColumnSelected(0)) {
						if (burstsTable.isCellSelected(r, 1)) {
							burstsTable.setValueAt(new ColorCell("", Color.LIGHT_GRAY), r, 1);
							burstsTable.setValueAt(new ColorCell("", Color.WHITE), r, 2);
						} else {
							burstsTable.setValueAt(new ColorCell("", Color.WHITE), r, 1);
							burstsTable.setValueAt(new ColorCell("", Color.LIGHT_GRAY), r, 2);
						}
					}
				}
			} else {
				Integer pid = null;
				Integer row = this.getPainter(ARRIVING_PAINTER).detectMouseOver(0, 0);
				if (row != null) {
					pid = new Integer((String) ((ArrivingPainter) this.getPainter(ARRIVING_PAINTER)).getValueAt(row, 0));
					boolean popup = selectElement(pid, this.getPainter(ARRIVING_PAINTER));
					if (popup) this.getPainter(ARRIVING_PAINTER).showPopupMenu();
				}
			}
		}
	}

	
	/**
	 * Adds process scheduling concrete implementation to play event (Template Pattern).
	 * Change pop up menu items in the queues and starts forwarding time   
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 * 
	 */
	public boolean actionPlay() {
		// Remove context menu items
		this.getPainter(PROCS_PAINTER).clearMenu();
		this.getPainter(ARRIVING_PAINTER).clearMenu();
		return context.forwardTime(timecontrols.getTime(), ((ProcessSettings) settings).getMultiprogramming(), ((ProcessSettings) settings).getPreemptive(), ((ProcessSettings) settings).getQuantumSize());
	}
	
	/**
	 * Adds process scheduling concrete implementation to stop event (Template Pattern).
	 * Restore pop up menu items in the queues and restores queue's initial state   
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 * 
	 */
	public void actionStop() {
		// Restore context menu items
		this.getPainter(PROCS_PAINTER).addMenuItem(menuItemsMem.get(0));
		this.getPainter(PROCS_PAINTER).addMenuItem(menuItemsMem.get(1));
		this.getPainter(ARRIVING_PAINTER).addMenuItem(menuItemsMem.get(0));
		this.getPainter(ARRIVING_PAINTER).addMenuItem(menuItemsMem.get(1));
		context.restoreBackup();
	}
	
	/**
	 * Adds process scheduling concrete implementation to timer event (Template Pattern).
	 * Forwards time using process scheduling context.    
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 * 
	 */
	public boolean actionTimer() {
		return context.forwardTime(timecontrols.getTime(), ((ProcessSettings) settings).getMultiprogramming(), ((ProcessSettings) settings).getPreemptive(), ((ProcessSettings) settings).getQuantumSize());
	}
	
	/**
	 * Implements process scheduling concrete action events (Template Pattern).
	 * <ul>
	 * <li>Adds a new process. Opens FormProcess and updates model with user input</li>
	 * <li>Updates a process. Opens FormProcess and updates model with user input</li>
	 * <li>Removes a process. Opens a confirmation dialog, and if so deletes process</li>
	 * <li>Changes current algorithm, updates model (context) and algorithm information</li>
	 * <li>Changes preemptive value, updates algorithm information</li> 
	 * </ul>    
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 */
	public void actionSpecific(String actionCommand) throws SoSimException {
		Vector<Object> values;
		Vector<Object> d;
		int action = actions.get(actionCommand).intValue();
		
		switch (action) {
		case 20:
			// Add process
			// Control max processes creation
			if (context.getProcessCount() >= ContextProcess.MAX_PROCESSES) throw new SoSimException("pr_01", "(max. : " + ContextProcess.MAX_PROCESSES + ")");
			
			values = new Vector<Object>();
			values.add(timecontrols.getTime());
			values.add(context.getMaxpid());
			d = openForm(new FormProcess(this, Translation.getInstance().getLabel("pr_42"), createHelp("sch_new"), values));

			if (d != null) {
				context.addProcess(d, timecontrols.getTime());
			}
			break;
		case 21:
			// Update process
			values = new Vector<Object>();
			values.add(timecontrols.getTime());
			values.addAll(context.getSelectedProcessData());
			d = openForm(new FormProcess(this, Translation.getInstance().getLabel("pr_06"), createHelp("sch_new"), values));

			if (d != null) {
				context.updProcess(d, timecontrols.getTime());
			}
			break;
		case 22:
			// Remove process
			context.removeProcess();
			break;

			// Settings actions		

		case 23:
			// First Come First Served
			((ProcessSettings) settings).selectPreemptive(false);
			((ProcessSettings) settings).enableMultiprogramming(true);
			((ProcessSettings) settings).enablePreemptive(false);
			((ProcessSettings) settings).visibleQuantum(false);
			context.setAlgorithm(new ProcessStrategyFCFS());
			panel.setLabel(getAlgorithmInfo());
			break;
		case 24:
			// Shortest Time First
			((ProcessSettings) settings).enableMultiprogramming(false);
			((ProcessSettings) settings).selectMultiprogramming(true);
			((ProcessSettings) settings).enablePreemptive(true);
			((ProcessSettings) settings).visibleQuantum(false);
			context.setAlgorithm(new ProcessStrategySJF(((ProcessSettings) settings).getPreemptive()));
			panel.setLabel(getAlgorithmInfo());
			break;
		case 25:
			// Priority
			((ProcessSettings) settings).enableMultiprogramming(false);
			((ProcessSettings) settings).selectMultiprogramming(true);
			((ProcessSettings) settings).enablePreemptive(true);
			((ProcessSettings) settings).visibleQuantum(false);
			context.setAlgorithm(new ProcessStrategyPrio(((ProcessSettings) settings).getPreemptive()));
			panel.setLabel(getAlgorithmInfo());
			break;
		case 26:
			// Round Robin
			((ProcessSettings) settings).enableMultiprogramming(false);
			((ProcessSettings) settings).selectMultiprogramming(true);
			((ProcessSettings) settings).selectPreemptive(true);
			((ProcessSettings) settings).enablePreemptive(false);
			((ProcessSettings) settings).visibleQuantum(true);
			context.setAlgorithm(new ProcessStrategyRR(((ProcessSettings) settings).getQuantumSize()));
			panel.setLabel(getAlgorithmInfo());
			break;
		case 27:
			// Preemtive
			context.setPreemptive(((ProcessSettings) settings).getPreemptive());
			panel.setLabel(getAlgorithmInfo());
			break;
		case 28:
			// Multiprogramming
			panel.setLabel(getAlgorithmInfo());
			break;
		}
	}
	
	/**
	 * Selects model object representing process identified by id, 
	 * No element can be selected if simulation has started
	 *  
	 * @param id	process identifier
	 * @param pt 	unused
	 * 
	 * @return element can be selected
	 */
	public boolean selectElement(Integer id, PainterTemplate pt) {
		if (started) return false; // Nothing to do in queue once started
		return context.setSelectedProcess(id.intValue());
	}
	
	/**************************************************************************************************/
	/*************************************   Process management ***************************************/
	/**************************************************************************************************/
	
	/**
	 * Updates information panel data according to current simulation time   
	 */
	public void updateInfo() {
		// Update possible value changed. Running process and ready queue and optionally arriving queue and IO operations
		info.updateValues(context.getTableStatsData(timecontrols.getTime()));
		info.initData(context.getTableInfoData(timecontrols.getTime()));
		((ArrivingPainter) this.getPainter(ARRIVING_PAINTER)).initData(context.getArrivingInfoData(timecontrols.getTime()));
		((IOPainter) this.getPainter(IO_PAINTER)).initData(context.getIOInfoData());
	}
	
	/**
	 * Translates information panel, incoming queue and io queue labels
	 * 
	 */
	public void updateLabels() {
		// Update info dialog
		info.updateLabels(context.getTableHeaderInfo());
		((ArrivingPainter) this.getPainter(ARRIVING_PAINTER)).updateLabels(context.getArrivingHeaderInfo(), "pr_02");
		((IOPainter) this.getPainter(IO_PAINTER)).updateLabels(context.getIOHeaderInfo(), "pr_08");
	}

	/**
	 * Returns ready queue iterator 
	 * 
	 * @param i	unused
	 * 
	 * @return   ready queue  iterator
	 */
	public Iterator<Integer> iterator(int i) {
		return context.iteratorReady();
	}

	/**
	 * @see ContextProcess#getAlgorithmInfo(boolean, boolean, int) 
	 */
	public String getAlgorithmInfo() {
		return context.getAlgorithmInfo(((ProcessSettings) settings).getMultiprogramming());
	}
	
	/**
	 * @see ContextProcess#getInfo(int)
	 * 
	 */
	public Vector<String> getInfo(int pid) {
		return context.getInfo(pid);
	}

	/**
	 * @see ContextProcess#getPname(int)
	 */
	public String getPname(int pid) {
		return context.getPname(pid);
	}

	/**
	 * @see ContextProcess#getColor(int)
	 */
	public Color getColor(int pid) {
		return context.getColor(pid);
	}

	/**
	 * @see ContextProcess#getTimesubmission(int)
	 */
	public int getTimesubmission(int pid) {
		return context.getTimesubmission(pid);
	}

	/**
	 * @see ContextProcess#isCPUBurst(int, int)
	 */
	public boolean isCPUBurst(int pid, int i) {
		return context.isCPUBurst(pid, i);
	}

	/**
	 * @see ContextProcess#getSize(int)
	 */
	public int getSize(int pid) {
		return context.getSize(pid);
	}

	/**
	 * @see ContextProcess#getPprio(int)
	 */
	public int getPprio(int pid) {
		return context.getPprio(pid);
	}
	
	/**
	 * @see ContextProcess#getCurrent(int)
	 */
	public int getCurrent(int pid) {
		return context.getCurrent(pid);
	}

	/**
	 * @see ContextProcess#getMaxpid()
	 */
	public int getMaxpid() {
		return context.getMaxpid();
	} 
	
	/**
	 * @see ContextProcess#getPIDrunning()
	 */
	public int getRunning() {
		return context.getPIDrunning();
	}
	
	/**
	* @see ContextProcess#getFormTableHeader()
	*/
	public Vector<Object> getFormTableHeader() {
		// Form program Header 
		return context.getFormTableHeader();
	}
	
	/**************************************************************************************************/
	/*************************************   XML management ***************************************/
	/**************************************************************************************************/

	/**
	 * Returns XML root element for process scheduling simulation files
	 * 
	 */
	public String getXMLRoot() {
		// Returns XML root element 
		return  Functions.getInstance().getPropertyString("xml_root_pro");
	}
	
	/**
	 * Returns XML direct root children for process scheduling simulation files:
	 *  "params", "ready_queue" and "arriving_queue". 
	 * Every child groups simulation persistent object    
	 * 
	 */
	public Vector<String> getXMLChilds() {
		Vector<String> childs = new Vector<String>();
		childs.add("params");
		childs.add("ready_queue");
		childs.add("arriving_queue");
		return childs;
	}
	
	/**
	 * Returns all model information from a concrete child identified by <code>child</code>.
	 * 
	 * @see  #getXMLChilds()
	 */
	public Vector<Vector<Vector<String>>> getXMLData(int child) {
		Vector<Vector<Vector<String>>> data = null;
		
		switch (child) {
		case 0: 	// Params
			data = new Vector<Vector<Vector<String>>>();
			Vector<Vector<String>> param = new Vector<Vector<String>>();
			Vector<String> attribute = new Vector<String>();		
			attribute.add("management");
			attribute.add(settings.getAlgorithm());
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("multiprogramming");
			attribute.add(Boolean.toString(((ProcessSettings) settings).getMultiprogramming()));
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("preemptive");
			attribute.add(Boolean.toString(((ProcessSettings) settings).getPreemptive()));
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("quantum");
			attribute.add(Integer.toString(((ProcessSettings) settings).getQuantumSize()));
			param.add(attribute);
			data.add(param);
			break;
		case 1: 	// Ready queue
			data = context.getXMLDataReady(); 
			break;
		case 2: 	// Incoming queue
			data = context.getXMLDataArriving();
			break;
		}
		return data;
	}

	/**
	 * Builds all model information from a concrete child identified by <code>child</code>
	 * 
  	 * @see  #getXMLChilds()
  	 */
	public void putXMLData(int child, Vector<Vector<Vector<String>>> data) throws SoSimException {
		try {
			switch (child) {
			case 0: 	// Params
				String actionCommand = data.get(0).get(0).get(1);
				String sMultiProgramming = data.get(0).get(1).get(1);
				String sPreemptive = data.get(0).get(2).get(1);
				String sQuantum = data.get(0).get(3).get(1);

				boolean multiprogramming = new Boolean(sMultiProgramming).booleanValue();
				boolean preemptive = new Boolean(sPreemptive).booleanValue();
				int quantum = new Integer(sQuantum).intValue();

				settings.selectAlgorithm(actionCommand);
				((ProcessSettings) settings).selectMultiprogramming(multiprogramming);
				((ProcessSettings) settings).setQuantumSize(quantum);
				((ProcessSettings) settings).selectPreemptive(preemptive);
				context.setPreemptive(preemptive);
				actionSpecific(actionCommand); // Updates management.
				break;
			case 1:	
			case 2: 	

				for (int i=0; i<data.size(); i++) { // Processes
					Vector<Vector<String>> process = data.get(i);
					Vector<Object> processData = new Vector<Object>();  

					processData.add(process.get(0).get(1)); // pid. Value at position 1
					processData.add(process.get(1).get(1)); 			 // name. Value at position 1
					processData.add(new Integer(process.get(2).get(1))); // prio. Value at position 1
					processData.add(new Integer(process.get(3).get(1))); // submission. Value at position 1
					processData.add(new Boolean(process.get(4).get(1))); // periodic. Value at position 1
					processData.add(new Color(new Integer(process.get(6).get(1)))); // color. Value at position 1 (RGB value)

					Vector<Integer> bursts= new Vector<Integer>();
					String[] sbursts = process.get(5).get(1).split(" ");
					for(int k = 0; k < sbursts.length; k++) {
						bursts.add(new Integer(sbursts[k]));
					}
					processData.add(bursts);

					context.addProcess(processData, timecontrols.getTime());
				}
				break;
			}
		} catch (Exception e) {
			throw new SoSimException("all_04");
		}
	}


}
