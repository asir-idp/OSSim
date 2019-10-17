package edu.upc.fib.ossim.memory;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.memory.model.ContextMemory;
import edu.upc.fib.ossim.memory.model.MemStrategyFIXED;
import edu.upc.fib.ossim.memory.model.MemStrategyPAG;
import edu.upc.fib.ossim.memory.model.MemStrategySEG;
import edu.upc.fib.ossim.memory.model.MemStrategyVAR;
import edu.upc.fib.ossim.memory.view.FormAddress;
import edu.upc.fib.ossim.memory.view.FormBlock;
import edu.upc.fib.ossim.memory.view.FormProcess;
import edu.upc.fib.ossim.memory.view.FormProcessPag;
import edu.upc.fib.ossim.memory.view.FormProcessSeg;
import edu.upc.fib.ossim.memory.view.MemoryPainter;
import edu.upc.fib.ossim.memory.view.MemorySettings;
import edu.upc.fib.ossim.memory.view.PanelMemory;
import edu.upc.fib.ossim.memory.view.QueuePainter;
import edu.upc.fib.ossim.memory.view.SwapPainter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.InfoDialog;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.TimerPanel;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Memory presenter manage concrete memory management behaviors, it builds main panel adding concrete components,
 * manage specific events and implements xml processing  
 * 
 * @author Alex Macia
 * 
 * @see Presenter
 */
public class MemoryPresenter extends Presenter  {

	private static final int TABLE_WIDTH = 200;
	private static final int TABLE_HEIGTH = 100;
	private static final int INFO_WIDTH = 500;
	private static final int INFO_HEIGTH = 200;
	public static final int MEMORY_WIDTH = 240;
	public static final int MEMORY_HEIGHT = 380;
	public static final int PROGRAMS_WIDTH = 500;
	public static final int PROGRAMS_HEIGHT = 200;
	public static final int LEGEND_WIDTH = 240;
	public static final int LEGEND_HEIGTH = 30;

	public static final String PROGS_PAINTER = "programs";
	public static final String MEM_PAINTER = "memory";
	public static final String SWAP_PAINTER = "swap";

	private Vector<String[]> menuItemsMem;
	private Vector<String[]> menuItemsProg;
	private Vector<String[]> menuItemsRun;
	private Vector<String[]> menuItemsSwap;
	private String mgnActionCommand; // Keeps current memory management
	private int pageSize;
	private ContextMemory context;
	private boolean allocationFailure;
	/**************************************************************************************************/
	/*************************************   Class  management  ***************************************/
	/**************************************************************************************************/

	/**
	 * Constructs a ProcessPresenter
	 * 
	 * @see Presenter
	 */
	public MemoryPresenter(boolean openSettings) {
		super(openSettings);
		allocationFailure = false;
	}

	/**
	 * Constructs main panel adding all components and its pop up menus. Timer panel, three painters builds this panel, the memory, 
	 * the processes queue and the backing store (swap), besides a settings dialog and an information dialog are initialized    
	 *  
	 * @see MemoryPainter
	 * @see QueuePainter
	 * @see SwapPainter
	 * @see MemorySettings
	 * @see InfoDialog
	 */
	public PanelTemplate createPanelComponents() {
		timecontrols = new TimerPanel(this, TIMER_VELOCITY);
		createMenuItems();
		super.addPainter(new MemoryPainter(this, menuItemsMem, MEMORY_WIDTH, MEMORY_HEIGHT), MEM_PAINTER);
		super.addPainter(new QueuePainter(this, "me_01", menuItemsProg, PROGRAMS_WIDTH, PROGRAMS_HEIGHT), PROGS_PAINTER);
		super.addPainter(new SwapPainter(this, "me_02", menuItemsSwap, PROGRAMS_WIDTH, PROGRAMS_HEIGHT), SWAP_PAINTER);
		settings = new MemorySettings(this, "mem_set");
		info = new InfoDialog(this, "me_41", "mem_info", false, INFO_WIDTH, INFO_HEIGTH, null, context.getTableHeaderInfo(), context.getTableInfoData());
		return new PanelMemory(this, "me_42");
	}

	private void createMenuItems() {
		menuItemsMem = new Vector<String[]>();
		String[] item1 = {"painter_upd", "me_08", "update.png"};
		String[] item2 = {"painter_del", "me_09", "trash.png"};
		String[] item13 = {"partition_add", "me_53", "partition.png"};
		menuItemsMem.add(item1);
		menuItemsMem.add(item2);
		menuItemsMem.add(item13);
		menuItemsProg = new Vector<String[]>();
		String[] item3 = {"prog_upd", "me_06", "update.png"};
		String[] item4 = {"prog_del", "me_07", "trash.png"};
		menuItemsProg.add(item3);
		menuItemsProg.add(item4);
		menuItemsRun = new Vector<String[]>();
		String[] item5 = {"progmem_del", "me_07", "trash.png"};
		String[] item6 = {"swap_out", "me_10", "swap.png"};
		String[] item11 = {"address_trans", "me_14", "at.png"};
		String[] item9 = {"page_table", "me_12", "info.png"};
		String[] item10 = {"segment_table", "me_13", "info.png"};
		String[] item12 = {"defrag", "me_54", "def.png"};
		menuItemsRun.add(item5);
		menuItemsRun.add(item6);
		menuItemsRun.add(item11);
		menuItemsRun.add(item9);
		menuItemsRun.add(item10);
		menuItemsRun.add(item12);
		menuItemsSwap = new Vector<String[]>();
		String[] item7 = {"swap_del", "me_07", "trash.png"};
		String[] item8 = {"swap_in", "me_11", "swap.png"};
		menuItemsSwap.add(item7);
		menuItemsSwap.add(item8);
	}

	/**
	 * Maps MemoryPresenter concrete actions.
	 * 
	 * For instance<br/> <code>actions.put(action command, number);</code><br/>
	 * <ul>
	 * action command from component that generate the event<br/> 
	 * number between 60 and 90 
	 * </ul>
	 */
	public void mapActionsSpecific() {
		actions.put("panel_add",60);
		actions.put("prog_upd",61);
		actions.put("partition_add",62);
		actions.put("defrag",63);
		actions.put("painter_upd",64);
		actions.put("painter_del",65);
		actions.put("prog_del",66);
		actions.put("progmem_del",67);
		actions.put("swap_out",68);
		actions.put("swap_del",69);
		actions.put("swap_in",70);
		actions.put("FIX",71);
		actions.put("VAR",72);
		actions.put("PAG",73);
		actions.put("SEG",74);
		actions.put("FF",75);
		actions.put("BF",76);
		actions.put("WF",77);
		actions.put("PSIZE",78);
		actions.put("SOSIZE",79);
		actions.put("page_table",80);
		actions.put("segment_table",81);
		actions.put("address_trans",82);
	}

	/**
	 * Creates memory management model. Model implements Strategy Pattern, it can be accessed 
	 * through <b>context</b>, different algorithms are implemented in concrete <b>strategies</b>. Initial
	 * context strategy is Fixed-size partitions   
	 */
	public void createContext() {
		context = new ContextMemory(MemorySettings.MIN_MEMSIZE, MemorySettings.SO_VALUES[2], 1, new MemStrategyFIXED("FF"));
		mgnActionCommand = "FIX";
		pageSize = 1;
	}

	/**************************************************************************************************/
	/*************************************   Events management  ***************************************/
	/**************************************************************************************************/

	/**
	 * Receive multiples events:
	 * <ul>
	 * <li>setting's memory size component change state event. Updates algorithm information</li>
	 * <li>form's process size component change state event. Updates number of process pages (Only pagination)</li>
	 * <ul>
	 */
	public void stateChangedSpecific(ChangeEvent e) {
		JSpinner size = (JSpinner) e.getSource();

		if ("memSize".equals(size.getName())) {
			// Change memory size
			if (confirmChange(context.getMemorySize() != ((MemorySettings) settings).getMemSize())) {
				context.setMemorySizeParams(((MemorySettings) settings).getMemSize(), ((MemorySettings) settings).getSOSize());
				panel.setLabel(getAlgorithmInfo());
				repaintPainters();
			} else {
				((MemorySettings) settings).setMemSize(context.getMemorySize());
			}
		} 

		if ("size".equals(size.getName())) {
			// Change a process size
			int progSize = (Integer) size.getValue();
			if (progSize%pageSize == 0) ((FormProcessPag) getForm()).updatePageTable(progSize/pageSize);
			else ((FormProcessPag) getForm()).updatePageTable(progSize/pageSize +1);
		}
	}

	/**
	 * Adds memory management concrete implementation to play event (Template Pattern).
	 * Change pop up menu items in the queues and starts forwarding time   
	 * @throws SoSimException 
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 * 
	 */
	// Method overridden
	public boolean actionPlay() throws SoSimException {
		// Change popup menu
		super.getPainter(PROGS_PAINTER).clearMenu();
		super.getPainter(MEM_PAINTER).clearMenu();
		super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(0));
		super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(1));
		super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(2));
		if ("VAR".equals(mgnActionCommand)) super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(5));
		if ("PAG".equals(mgnActionCommand)) super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(3));
		if ("SEG".equals(mgnActionCommand)) {
			super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(4));
			super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(5));
		}

		return context.forwardTime(timecontrols.getTime());
	}

	/**
	 * Adds memory management concrete implementation to stop event (Template Pattern).
	 * Restore pop up menu items in the queues and restores queue's initial state   
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 * 
	 */
	// Method overridden
	public void actionStop() {
		allocationFailure = false;
		context.restoreBackup();
		// Change popup menu
		super.getPainter(PROGS_PAINTER).clearMenu();
		super.getPainter(PROGS_PAINTER).addMenuItem(menuItemsProg.get(0));
		super.getPainter(PROGS_PAINTER).addMenuItem(menuItemsProg.get(1));

		super.getPainter(MEM_PAINTER).clearMenu();
		if ("FIX".equals(mgnActionCommand)) {
			super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(0));
			super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(1));
			super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(2));
		}
	}

	/**
	 * Adds memory management concrete implementation to timer event (Template Pattern).
	 * Forwards time using memory management context.    
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 * 
	 */
	// Method overridden
	public boolean actionTimer() throws SoSimException {
		try {
			boolean end = context.forwardTime(timecontrols.getTime());
			allocationFailure = false;
			return end;
		} catch (SoSimException e) {
			// Error allocating. Error message only once and don't stop simulation
			if ("me_08".equals(e.getKey())) {
				if (!allocationFailure) {
					JOptionPane.showMessageDialog(panel,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					allocationFailure = true;
				}
				return false;
			} else throw e; 
		}
	}

	/**
	 * Implements management concrete concrete action events (Template Pattern).
	 * <ul>
	 * <li>Adds a new process. Opens FormProcess and updates model with user input</li>
	 * <li>Updates a process. Opens FormProcess and updates model with user input</li>
	 * <li>Adds a new partition (</li>
	 * <li>Compacts memory (</li>
	 * <li>Updates a partition</li>
	 * <li>Deletes a partition</li>
	 * <li>Deletes a queued process</li>
	 * <li>Deletes a process allocated into memory</li>
	 * <li>Swaps out a process, from memory to backing Store</li>
	 * <li>Delete a process from backing store</li>
	 * <li>Swaps in a process, from backing Store to memory</li>
	 * <li>Changes current algorithm, updates model (context) and algorithm information</li>
	 * <li>Translates panel labels</li>
	 * <li>Updates page size</li>
	 * <li>Updates operating system size</li>
	 * <li>Shows pages table</li>
	 * <li>Shows segments table</li>
	 * <li>Shows address translation form</li> 
	 * </ul>    
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 */
	@SuppressWarnings("rawtypes")
	public void actionSpecific(String actionCommand) throws SoSimException {
		String blockTitle  = "";
		Vector<Object> values;
		Vector<Object> d = null;
		Vector<Vector> c;
		int action = actions.get(actionCommand).intValue();

		switch (action) {
		case 60:
			// Control max processes creation
			if (context.getProcessCount() >= ContextMemory.MAX_PROCESSES) throw new SoSimException("me_15", "(max. : " + ContextMemory.MAX_PROCESSES + ")");

			values = new Vector<Object>();
			values.add(context.getMaxpid()); 
			// Add process
			if ("PAG".equals(mgnActionCommand)) {
				blockTitle = Translation.getInstance().getLabel("me_70");
				d = openForm(new FormProcessPag(this, Translation.getInstance().getLabel("me_42"), createHelp("mem_new"),values, blockTitle));
			}
			if ("SEG".equals(mgnActionCommand)) {
				blockTitle = Translation.getInstance().getLabel("me_71");
				d = openForm(new FormProcessSeg(this, Translation.getInstance().getLabel("me_42"), createHelp("mem_new"),values, blockTitle));
			}

			if ("FIX".equals(mgnActionCommand) || "VAR".equals(mgnActionCommand)) {
				d = openForm(new FormProcess(this, Translation.getInstance().getLabel("me_42"), createHelp("mem_new"),values));
			}

			c = ((FormProcess) getForm()).getComponentsData();

			if (d != null) context.addProgram(d, c);
			break;
		case 61:	
			// Update process
			if ("PAG".equals(mgnActionCommand)) {
				blockTitle = Translation.getInstance().getLabel("me_70");
				d = openForm(new FormProcessPag(this, Translation.getInstance().getLabel("me_42"), createHelp("mem_new"),context.getSelectedProcessData(), blockTitle));
			}
			if ("SEG".equals(mgnActionCommand)) {
				blockTitle = Translation.getInstance().getLabel("me_71");
				d = openForm(new FormProcessSeg(this, Translation.getInstance().getLabel("me_42"), createHelp("mem_new"),context.getSelectedProcessData(), blockTitle));
			}

			if ("FIX".equals(mgnActionCommand) || "VAR".equals(mgnActionCommand)) {
				d = openForm(new FormProcess(this, Translation.getInstance().getLabel("me_42"), createHelp("mem_new"),context.getSelectedProcessData()));
			}			
			c = ((FormProcess) getForm()).getComponentsData();

			if (d != null) {
				//context.removeProgram();
				context.updProgram(d, c);
			}
			break;
		case 62:	
			// Add partition 
			values = new Vector<Object>();
			values.add(this.getMemorySize()-1); 
			d = openForm(new FormBlock(this, Translation.getInstance().getLabel("me_43"), createHelp("mem_part"), values));

			if (d != null) {
				context.addMemPartition(d);
			}
			break;
		case 63:	
			// Defragment Memory
			context.compaction();
			break;
		case 64:	
			// Update partition
			if (timecontrols.isRunning()) JOptionPane.showMessageDialog(panel,Translation.getInstance().getError("me_01"),"Error",JOptionPane.ERROR_MESSAGE);
			else {
				d = openForm(new FormBlock(this, Translation.getInstance().getLabel("me_43"), createHelp("mem_part"), context.getSelectedPartitionData()));

				if (d != null) {
					context.removeMemPartition();
					context.addMemPartition(d);
				}
			}
			break;
		case 65:	
			// Delete partition
			if (timecontrols.isRunning()) JOptionPane.showMessageDialog(panel,Translation.getInstance().getError("me_01"),"Error",JOptionPane.ERROR_MESSAGE);
			else {
				context.removeMemPartition();
			}
			break;
		case 66:	
			// Delete program
			context.removeProgram();
			break;
		case 67:	
			// Delete program component allocated in memory
			context.removeProgramInMem(); 
			break;

			// Swapping actions

		case 68:	
			// From memory to backing Store
			context.swapOutProgramComponent();
			break;
		case 69:	
			// Delete all program from swap		
			context.removeSwappedProgram();
			break;
		case 70:	
			// From backing Store to memory			
			try {
				context.swapInProgramComponent();
			} catch (SoSimException e) {
				// Error allocating. 
				if ("me_08".equals(e.getKey())) JOptionPane.showMessageDialog(panel,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				else throw e; 
			}

			break;

			// Settings actions
		case 71:
		case 72:	
		case 73:	
		case 74:
			if (confirmChange(!mgnActionCommand.equals(actionCommand))) {
				// Updates action command
				mgnActionCommand = actionCommand;

				if (action == 71) {
					context.setAlgorithm(new MemStrategyFIXED(((MemorySettings) settings).getPolicy()));

					((MemorySettings) settings).paginationSetVisible(false);
					((MemorySettings) settings).policyEnable(true);
					super.getPainter(MEM_PAINTER).clearMenu();
					super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(0));
					super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(1));
					super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(2));
				}
				if (action == 72) {
					context.setAlgorithm(new MemStrategyVAR(((MemorySettings) settings).getPolicy()));
					((MemorySettings) settings).paginationSetVisible(false);
					((MemorySettings) settings).policyEnable(true);
					super.getPainter(MEM_PAINTER).clearMenu();
				}
				if (action == 73) {
					context.setAlgorithm(new MemStrategyPAG(pageSize));
					((MemorySettings) settings).paginationSetVisible(true);
					((MemorySettings) settings).policyEnable(false);
					super.getPainter(MEM_PAINTER).clearMenu();
				}
				if (action == 74) {
					context.setAlgorithm(new MemStrategySEG());
					((MemorySettings) settings).paginationSetVisible(false);
					((MemorySettings) settings).policyEnable(false);
					super.getPainter(MEM_PAINTER).clearMenu();
				}
				settings.pack();
				panel.setLabel(getAlgorithmInfo());
				info.dispose();
				info = new InfoDialog(this, "me_41", "mem_info", false, INFO_WIDTH, INFO_HEIGTH, null, context.getTableHeaderInfo(), context.getTableInfoData());
			} else {
				((MemorySettings) settings).setAlgorithm(mgnActionCommand);
			}
			break;

			// Policy actions

		case 75:	
		case 76:	
		case 77:	
			context.setPolicy(((MemorySettings) settings).getPolicy());
			panel.updateLabels();
			break;
		case 78: // Change page size
			if (confirmChange(pageSize != ((MemorySettings) settings).getPageSize())) {
				pageSize = ((MemorySettings) settings).getPageSize();
				context.setAlgorithm(new MemStrategyPAG(pageSize));
				context.setMemorySizeParams(((MemorySettings) settings).getMemSize(), ((MemorySettings) settings).getSOSize());
				panel.setLabel(getAlgorithmInfo());
			} else {
				((MemorySettings) settings).setPageSize(pageSize);
			}
			break;

		case 79: // Change SO size
			if (confirmChange(context.getSOSize() != ((MemorySettings) settings).getSOSize())) {
				context.setMemorySizeParams(((MemorySettings) settings).getMemSize(), ((MemorySettings) settings).getSOSize());
				panel.setLabel(getAlgorithmInfo());
			} else {
				((MemorySettings) settings).setSOSize(context.getSOSize());
			}
			break;

		case 80:	
			// Show pages table
			InfoDialog pageTable =	new InfoDialog(this, "me_12", "mem_detailinfo", true, TABLE_WIDTH, TABLE_HEIGTH, null, context.getMemProcessTableHeader(), context.getMemProcessTableData()); 
			pageTable.setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
			pageTable.setVisible(true);
			break;
		case 81:	
			// Show segments table
			InfoDialog segmentTable = new InfoDialog(this, "me_13", "mem_detailinfo", true, TABLE_WIDTH, TABLE_HEIGTH, null, context.getMemProcessTableHeader(), context.getMemProcessTableData());
			segmentTable.setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
			segmentTable.setVisible(true);
			break;

		case 82:	
			// Address translation 
			values = new Vector<Object>();
			values.add(context.getAddTransProgInfo()); 
			d = openForm(new FormAddress(this, Translation.getInstance().getLabel("me_14"), createHelp("mem_addr"), values));
			break;
		}
	}

	private boolean confirmChange(boolean change) {
		if (change && ((!context.iteratorProcesses().hasNext() || 
				(context.iteratorProcesses().hasNext() && 
						JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(panel, Translation.getInstance().getError("me_10"), "Warning", JOptionPane.OK_CANCEL_OPTION))))) {
			return true;
		}
		return false;
	}

	/**
	 * Manage mouse pressed events generated by painters<br/>
	 * Over a painter, mouse entered, selects painter's object under mouse icon and opens a pop up menu showing possible actions allowed for concrete painter.
	 * 
	 * @param e	mouse event
	 */
	protected void painterMousePressed(MouseEvent e) {
		boolean popup = false;
		if ("painter".equals(e.getComponent().getName())) {
			PainterTemplate pt = getPainter(e.getComponent());
			if (pt != null) {
				Integer id = pt.detectMouseOver(e.getX(), e.getY());

				if (id != null) {
					if (e.isPopupTrigger()) {
						if (selectElement(id, pt)) {
							// Normal popup menu
							popup = true;
							pt.showPopupMenu();
							// Returns running at popup events
						} else {
							if (("VAR".equals(mgnActionCommand) || "SEG".equals(mgnActionCommand)) && "memory".equals(pt.getAlias())) {
								// Variable-sized and segmentation allows defragmentation always
								popup = true;
								super.getPainter(MEM_PAINTER).clearMenu();
								super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(5));
								pt.showPopupMenu();

								super.getPainter(MEM_PAINTER).clearMenu();
								super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(0));
								super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(1));
								super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(2));
								if ("VAR".equals(mgnActionCommand)) super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(5));
								if ("SEG".equals(mgnActionCommand)) {
									super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(4));
									super.getPainter(MEM_PAINTER).addMenuItem(menuItemsRun.get(5));
								}
							}
						}
					}
					repaintPainters();
				} else {
					if ("FIX".equals(mgnActionCommand) && "memory".equals(pt.getAlias()) && e.isPopupTrigger() && !started) {
						popup = true;
						super.getPainter(MEM_PAINTER).clearMenu();
						super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(2));
						pt.showPopupMenu();
						super.getPainter(MEM_PAINTER).clearMenu();
						super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(0));
						super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(1));
						super.getPainter(MEM_PAINTER).addMenuItem(menuItemsMem.get(2));
						repaintPainters();
					}
				}
			}
			if (!popup && wasrunning) timecontrols.play();
		}
	}

	/**
	 * Selects all possible model objects identified by id: a process (queued or into backing store) or a partition
	 *  
	 * @param id	object identifier
	 * @param pt 	object's parent painter 
	 *  
	 * @return element can be selected 
	 */
	public boolean selectElement(Integer id, PainterTemplate pt) {
		if ("queue".equals(pt.getAlias())) {
			if (started) return false; // Nothing to do in queue once started
			else return context.setSelectedProcess(id.intValue());
		}
		if ("swap".equals(pt.getAlias())) return context.setSelectedSwap(id.intValue());
		if ("memory".equals(pt.getAlias())) return context.setSelectedPartition(id.intValue(), started);
		return false;
	}

	/**************************************************************************************************/
	/*************************************   Request management ***************************************/
	/**************************************************************************************************/

	/**
	 * Updates information panel data according to current memory occupation   
	 */
	public void updateInfo() {
		// Update possible value changed.
		info.updateHeader(context.getTableHeaderInfo());
		info.initData(context.getTableInfoData());
	}

	/**
	 * Translates information panel
	 * 
	 */
	public void updateLabels() {
		// Update info dialog
		info.updateLabels(context.getTableHeaderInfo());
	}

	/**
	 * Returns model processes iterator. Parameter <code>i</code> determines from which queue 
	 * 
	 * @param i	identifies iterator source queue: 0-memory partitions, 1-queued processes, 2-processes into backing store
	 * 
	 * @return   a model processes iterator
	 */
	public Iterator<Integer> iterator(int i) {
		switch (i) {
		case 0:
			return context.iteratorPartitions();
		case 1:
			return context.iteratorProcesses();
		case 2:
			return context.iteratorSwap();
		default:
			return null;
		}
	}

	/**
	 * Returns algorithm information from model
	 * 
	 * @see ContextMemory#getAlgorithmInfo() 
	 */
	public String getAlgorithmInfo() {
		return context.getAlgorithmInfo();
	}

	/**
	 * @see ContextMemory#getInfo(int)
	 */
	public Vector<String> getInfo(int pid) {
		return context.getInfo(pid);
	}

	/**
	 * @see ContextMemory#getComponentSizeInfo(int)
	 */
	public String getComponentSizeInfo(int pid) {
		return context.getComponentSizeInfo(pid);
	}

	/**
	 * @see ContextMemory#getTotalComponentsint()
	 */
	public int getTotalComponents(int pid) {
		return context.getTotalComponents(pid);
	}

	/**
	 * @see ContextMemory#getSizeOfComponents(int, int)
	 */
	public int getSizeOfComponents(int pid, int i) {
		return context.getSizeOfComponents(pid, i);
	}

	/**
	 * @see ContextMemory#isComponentSwapped(int, int)
	 */
	public boolean isComponentSwapped(int pid, int i) {
		return context.isComponentSwapped(pid, i);
	}

	/**
	 * @see ContextMemory#isComponentPage(int, int)
	 */
	public boolean isComponentPage(int pid, int i) {
		return context.isComponentPage(pid, i);
	}

	/**
	 * @see ContextMemory#getColor(int)
	 */
	public Color getColor(int bid) {
		return context.getColor(bid);
	}

	/**
	 * @see ContextMemory#getDuration(int)
	 */
	public int getDuration(int pid) {
		return context.getDuration(pid);
	}

	/**
	 * @see ContextMemory#getSize(int)
	 */
	public int getSize(int pid) {
		return context.getSize(pid);
	}

	/**
	 * @see ContextMemory#getMemSize(int)
	 */
	public int getMemSize(int start) {
		return context.getMemSize(start);
	}

	/**
	 * @see ContextMemory#getMemProcessSize(int)
	 */
	public int getMemProcessSize(int start) {
		return context.getMemProcessSize(start);
	}

	/**
	 * @see ContextMemory#getMemProcessColor(int)
	 */
	public Color getMemProcessColor(int start) {
		return context.getMemProcessColor(start);
	}

	/**
	 * @see ContextMemory#hasExternalFragmentation()
	 */
	public boolean hasExternalFragmentation() {
		return context.hasExternalFragmentation();
	}

	/**
	 * @see ContextMemory#getMemProcessInfo(int)
	 */
	public Vector<String> getMemProgramInfo(int start) {
		return context.getMemProcessInfo(start);
	}

	/**
	 * @see ContextMemory#getFormTableHeader()
	 */
	public Vector<Object> getFormTableHeader() {
		// Form program Header 
		return context.getFormTableHeader();
	}

	/**
	 * @see ContextMemory#getFormTableInitData()
	 */
	public Vector<Vector<Object>> getFormTableInitData() {
		// Form program Initial Data 
		return context.getFormTableInitData();
	}

	/**
	 * @see ContextMemory#getMemorySize()
	 */
	public int getMemorySize() {
		return context.getMemorySize();
	}

	/**
	 * @see ContextMemory#getAddTransPhysical(int)
	 */
	public String getAddTransPhysical(int logicalAddr) {
		return context.getAddTransPhysical(logicalAddr);
	}

	/**************************************************************************************************/
	/*************************************   XML management ***************************************/
	/**************************************************************************************************/

	/**
	 * Returns XML root element for memory management simulation files
	 * 
	 */
	public String getXMLRoot() {
		// Returns XML root element 
		return Functions.getInstance().getPropertyString("xml_root_mem");
	}

	/**
	 * Returns XML direct root children for memory management simulation files:
	 * "params", "memory" and "programs_queue". 
	 * Every child groups simulation persistent object    
	 */
	public Vector<String> getXMLChilds() {
		Vector<String> childs = new Vector<String>();
		childs.add("params");
		childs.add("memory");
		childs.add("programs_queue");
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
			attribute.add(mgnActionCommand);
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("memorySize");
			attribute.add(Integer.toString(context.getMemorySize()));
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("soSize");
			attribute.add(Integer.toString(context.getSOSize()));
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("pageSize");
			attribute.add(Integer.toString(pageSize));
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("policy");
			attribute.add(((MemorySettings) settings).getPolicy());
			param.add(attribute);
			data.add(param);
			break;
		case 1: 	// Memory
			data = context.getXMLDataMemory(); // Except SO block. Creates at init
			break;
		case 2: 	// Programs
			data = context.getXMLDataPrograms();
			break;
		}
		return data;
	}

	/**
	 * Builds all model information from a concrete child identified by <code>child</code>
	 * 
	 * @see  #getXMLChilds()
	 */
	@SuppressWarnings("rawtypes")
	public void putXMLData(int child, Vector<Vector<Vector<String>>> data) throws SoSimException {
		try {
			switch (child) {
			case 0: 	// Params
				String actionCommand = data.get(0).get(0).get(1);
				String sMemSize = data.get(0).get(1).get(1);
				String sSOSize = data.get(0).get(2).get(1);
				String sPageSize = data.get(0).get(3).get(1);
				String policy = data.get(0).get(4).get(1);


				int soSize = Integer.parseInt(sSOSize);
				int memSize = Integer.parseInt(sMemSize);
				pageSize = Integer.parseInt(sPageSize);

				settings.selectAlgorithm(actionCommand);
				((MemorySettings) settings).selectPolicy(policy);
				((MemorySettings) settings).setMemSize(memSize);
				((MemorySettings) settings).setSOSize(soSize);
				((MemorySettings) settings).setPageSize(pageSize);

				context.setMemorySizeParams(memSize, soSize);
				context.setPolicy(policy);
				actionSpecific(actionCommand); // Updates management. Creates OS block

				break;
			case 1:   // Memory blocks
				for (int i=0; i<data.size(); i++) { 
					Vector<Vector<String>> block = data.get(i);
					Vector<Object> blockData = new Vector<Object>();  

					blockData.add(Integer.parseInt(block.get(0).get(1))); // start. Value at position 1
					blockData.add(Integer.parseInt(block.get(1).get(1))); // size. Value at position 1
					context.addMemPartition(blockData); 
				}
				break;
			case 2:  // Programs
				for (int i=0; i<data.size(); i++) { 
					Vector<Vector<String>> program = data.get(i);
					Vector<Object> programData = new Vector<Object>();  

					programData.add(program.get(0).get(1)); 			// pid. Value at position 1
					programData.add(program.get(1).get(1));			 	// name. Value at position 1
					programData.add(Integer.parseInt(program.get(2).get(1))); // size. Value at position 1
					programData.add(Integer.parseInt(program.get(3).get(1))); // duration. Value at position 1
					programData.add(new Color(Integer.parseInt(program.get(4).get(1)))); // color. Value at position 1 (RGB value)

					if (program.size() > 5) { // Components
						Vector<Vector> components = new Vector<Vector>();

						int num = (program.size() - 5) / 3; // bid, size, load?
						if ((program.size() - 5) % 3 != 0) throw new SoSimException("all_04");
						int k = 5;
						for (int j=0; j<num; j++) {
							Vector<Object> component = new Vector<Object>();
							component.add(Integer.parseInt(program.get(k+j*3).get(1)));  	// bid
							component.add(Integer.parseInt(program.get(k+j*3+1).get(1)));	// size
							component.add(Integer.parseInt(program.get(k+j*3+2).get(1)));	// load?
							components.add(component);
						}
						context.addProgram(programData, components);
					} else context.addProgram(programData, null);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SoSimException("all_04");
		}
	}
}
