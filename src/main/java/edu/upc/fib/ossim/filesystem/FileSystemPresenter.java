package edu.upc.fib.ossim.filesystem;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.DefaultTreeModel;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.filesystem.model.ContextFileSystem;
import edu.upc.fib.ossim.filesystem.model.FileSystemStrategyFAT;
import edu.upc.fib.ossim.filesystem.model.FileSystemStrategyUNIX;
import edu.upc.fib.ossim.filesystem.view.DevicePainter;
import edu.upc.fib.ossim.filesystem.view.FileSystemSettings;
import edu.upc.fib.ossim.filesystem.view.FilesTreePainter;
import edu.upc.fib.ossim.filesystem.view.FolderPainter;
import edu.upc.fib.ossim.filesystem.view.FormFile;
import edu.upc.fib.ossim.filesystem.view.FormFolder;
import edu.upc.fib.ossim.filesystem.view.FormLink;
import edu.upc.fib.ossim.filesystem.view.PanelFileSystem;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.HelpDialog;
import edu.upc.fib.ossim.utils.InfoDialog;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;


/**
 * File System presenter manage concrete file system management behaviors, 
 * it builds main panel adding concrete components,
 * manage specific events and implements xml processing  
 * 
 * @author Alex Macia
 * 
 * @see Presenter
 */
public class FileSystemPresenter extends Presenter {
	private static final int INFO_WIDTH = 300;
	private static final int INFO_HEIGTH = 400;
	private static final int FATSUM_WIDTH = 220;
	private static final int FATSUM_HEIGTH = 100;
	private static final int INODE_WIDTH = 250;
	private static final int INODE_HEIGTH = 300;
	public static final int DEVICE_HEIGHT = 480; 
	public static final int DEVICE_WIDTH = 560; 
	public static final int TREE_WIDTH = 200;
	public static final int TREE_HEIGHT = 230;
	public static final int FOLDER_WIDTH = 200;
	public static final int FOLDER_HEIGHT = 180;
	public static final String DEVICE_PAINTER = "device";
	public static final String FILESTREE_PAINTER = "filestree";
	public static final String FOLDER_PAINTER = "folder";
	private Vector<String[]> menuItemsFolder;
	private Vector<String[]> menuItemsItem;
	private Stack<InfoDialog> detailsTableStack; // Keeps modal windows opening's 
	private String mgnActionCommand; // Keeps current file system 
	private int blockSize;
	private int devSize;
	public ContextFileSystem context;
	/**************************************************************************************************/
	/*************************************   Class  management  ***************************************/
	/**************************************************************************************************/
	
	/**
	 * Constructs a FileSystemPresenter, initialize a details windows (instances of InfoDialog) stack  
	 * 
	 * @see Presenter
	 * @see InfoDialog
	 */
	public FileSystemPresenter(boolean openSettings) {
		super(openSettings);
		detailsTableStack = new Stack<InfoDialog>();
	}
	
	/**
	 * Constructs main panel adding all components and its pop up menus. 
	 * Three painters builds this panel, the secondary storage device, 
	 * the system's file tree, and the table displaying a folder' content,
	 * besides a settings dialog and an information dialog are initialized    
	 *  
	 * @see DevicePainter
	 * @see FilesTreePainter
	 * @see FolderPainter
	 * @see FileSystemSettings
	 * @see InfoDialog  
	 */
	public PanelTemplate createPanelComponents() {
		Vector<String[]> menuItemsTree = new Vector<String[]>();
		String[] item1 = {"FILE", "fs_03", "update.png"};
		String[] item2 = {"FOLDER", "fs_04", "update.png"};
		String[] item3 = {"LINK", "fs_05", "update.png"};
		String[] item4 = {"UPD", "fs_06", "update.png"};
		String[] item5 = {"DEL", "fs_07", "trash.png"};
		menuItemsTree.add(item1);
		menuItemsTree.add(item2);
		menuItemsTree.add(item3);
		menuItemsTree.add(item4);
		menuItemsTree.add(item5);
		menuItemsFolder = new Vector<String[]>();
		String[] item6 = {"FATSUM", "fs_11", "info.png"};
		String[] item7 = {"INODE", "fs_12", "info.png"};
		menuItemsFolder.add(item6);
		menuItemsFolder.add(item7);
		menuItemsItem = new Vector<String[]>();
		String[] item8 = {"INDIRECT", "fs_65", "info.png"};
		menuItemsItem.add(item8);
		
		super.addPainter(new DevicePainter(this, Translation.getInstance().getLabel("fs_01")), DEVICE_PAINTER);
		super.addPainter(new FilesTreePainter(this, menuItemsTree, TREE_WIDTH, TREE_HEIGHT), FILESTREE_PAINTER);
		super.addPainter(new FolderPainter(this, menuItemsFolder, context.getName(context.getRootId()), context.getSelectedFolderHeader(), FOLDER_WIDTH, FOLDER_HEIGHT), FOLDER_PAINTER);
		getPainter(FOLDER_PAINTER).clearMenu();
		getPainter(FOLDER_PAINTER).addMenuItem(menuItemsFolder.get(0));
		((FolderPainter) getPainter(FOLDER_PAINTER)).updateFolderData(context.getFolderSelected(), context.getSelectedFolderHeader(), context.getSelectedFolderData());
		settings = new FileSystemSettings(this, "fs_set");
		info = new InfoDialog(this, "fs_45", "fs_info", false, INFO_WIDTH, INFO_HEIGTH, null, context.getTableHeaderInfo(), context.getTableInfoData());
		return new PanelFileSystem(this);
	}

	/**
	 * Maps MemoryPresenter concrete actions.
	 * 
	 * For instance<br/> <code>actions.put(action command, number);</code><br/>
	 * <ul>
	 * action command from component that generate the event<br/> 
	 * number between 90 and 110 
	 * </ul>
	 */
	public void mapActionsSpecific() {
		actions.put("FILE",90);
		actions.put("FOLDER",91);
		actions.put("LINK",92);
		actions.put("UPD",93);
		actions.put("DEL",94);
		actions.put("SIZE",96);
		actions.put("DOS",97);
		actions.put("LINUX",98);
		actions.put("FATSUM",99);
		actions.put("INODE",100);
		actions.put("INDIRECT",101);
	}

	/**
	 * Creates file system management model. Model implements Strategy Pattern, it can be accessed 
	 * through <b>context</b>, different algorithms are implemented in concrete <b>strategies</b>. Initial
	 * context strategy is DOS (FAT table), block size is 1 kb, and memory 4096 kb  
	 */
	public void createContext() {
		mgnActionCommand = "DOS";
		blockSize = 1;
		devSize = 4096;
		context = new ContextFileSystem(new FileSystemStrategyFAT(blockSize, devSize));
	}

	/**************************************************************************************************/
	/*************************************   Events management  ***************************************/
	/**************************************************************************************************/

	/**
	 * Do nothing, file system doesn't generate component change state events
	 * 
	 */
	public void stateChangedSpecific(ChangeEvent e) {
		// Do nothing
	}
	
	/**
	 * Manage list events generated by folder table and block tables, 
	 * both show pop ups depending on selected row<br/>
	 *
	 * @param e	list selection event
	 *
	 * @see InfoDialog#showPopupMenu(int, int)
	 * @see FolderPainter#showPopupMenu(int, int)
	 */
	public void valueChanged(ListSelectionEvent e) {
		// Table event
		Integer row = null;
		if (!e.getValueIsAdjusting()) {
			if (detailsTableStack.size() > 0 && detailsTableStack.peek().getEventSource().equals(e.getSource())) { 
				// Item details table
				row = detailsTableStack.peek().detectMouseOver(); 
				if (row != null) {
					try {
						Integer block = new Integer((String) detailsTableStack.peek().getValueAt(row, 1));
						context.setselectedBlock(block);
						// Only shows popup for indirect blocks
						if (context.isSelectedIndirect()) detailsTableStack.peek().showPopupMenu();
					} catch (NumberFormatException e1) {
						// Row not points any block
					}
				}
			} else {
				// Folder painter table
				row = this.getPainter(FOLDER_PAINTER).detectMouseOver(0, 0);
				if (row != null) {
					Integer id = new Integer((String) ((FolderPainter) this.getPainter(FOLDER_PAINTER)).getValueAt(row, 1));
					setselectedPhysicalObject(id);
					this.getPainter(FOLDER_PAINTER).showPopupMenu();
				}
			}
		}
	}
	
	/**
	 * Implements management concrete concrete action events (Template Pattern).
	 * <ul>
	 * <li>Add file. Opens FormFile and updates model with user input</li>
	 * <li>Add folder. Opens FormFolder and updates model with user input</li>
	 * <li>Add link. Opens FormLink and updates model with user input</li>
	 * <li>Update a file system object. Opens appropriate form and updates model with user input</li>
	 * <li>Delete a file system object</li>
	 * <li>Change block size</li> 
	 * <li>Selects algorithm. Opens a confirmation dialog, and initialize file system</li>
	 * <li>Show item details, FAT item's summary, i-node information or indirect block information</li>
	 * </ul>    
	 * 
	 * @see Presenter#actionPerformed(ActionEvent e)
	 */ 
	public void actionSpecific(String actionCommand) throws SoSimException {
		Vector<Object> d = null;
		Vector<Object> values;
		Vector<String> labels;
		int id, pid;
		int ids[];
		
		int action = actions.get(actionCommand).intValue();
		
		switch (action) {
		case 90:	// Add file
			// Control max elements creation
			if (context.getElementCount() >= ContextFileSystem.MAX_ELEMENTS) throw new SoSimException("fs_11", "(max. : " + ContextFileSystem.MAX_ELEMENTS + ")");
			
			labels = new Vector<String>();
			labels.add("fs_44"); // Into folder
			labels.add("fs_31"); // File name
			
			values = new Vector<Object>();
			values.add(context.getFolderSelected());
			d = openForm(new FormFile(this, Translation.getInstance().getLabel("fs_41"), createHelp("fsnew_file"), values, labels));
			if (d != null) {
				id = context.addFile(-1, d); 
				pid = context.getParentId(id);
				((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(pid, id);
			}
			break;
		case 91:	// Add folder
			// Control max elements creation
			if (context.getElementCount() >= ContextFileSystem.MAX_ELEMENTS) throw new SoSimException("fs_11", "(max. : " + ContextFileSystem.MAX_ELEMENTS + ")");

			labels = new Vector<String>();
			labels.add("fs_44"); // Into folder
			labels.add("fs_30"); // Folder name

			values = new Vector<Object>();
			values.add(context.getFolderSelected());
			d = openForm(new FormFolder(this, Translation.getInstance().getLabel("fs_42"), createHelp("fsnew_folder"), values, labels));
			if (d != null) {
				ids = context.addFolder(-1, d);
				pid = context.getParentId(ids[0]);
				((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(pid, ids[0]); // Folder
				((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(ids[0], ids[1]); // "."
				((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(ids[0], ids[2]); // ".."
			}
			break;
		case 92:	// Add link
			// Control max elements creation
			if (context.getElementCount() >= ContextFileSystem.MAX_ELEMENTS) throw new SoSimException("fs_11", "(max. : " + ContextFileSystem.MAX_ELEMENTS + ")");

			labels = new Vector<String>();
			labels.add("fs_44"); // Into folder
			labels.add("fs_29"); // Link name

			values = new Vector<Object>();
			values.add(context.getFolderSelected());
			d = openForm(new FormLink(this, Translation.getInstance().getLabel("fs_43"), createHelp("fsnew_link"), values, labels));
			if (d != null) {
				id = context.addLink(-1, d); 
				pid = context.getParentId(id);
				((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(pid, id);
			}
			break;
		case 93:	// Update any item
			values = new Vector<Object>();
			labels = new Vector<String>();

			if (context.isSelectedAFile()) { 
				labels.add("fs_44"); // Into folder
				labels.add("fs_31"); // File name
				
				values.add(context.getFolderSelected());
				values.addAll(context.getSelectedData());
				d = openForm(new FormFile(this, Translation.getInstance().getLabel("fs_41"), createHelp("fsnew_file"), values, labels));
			}
			if (context.isSelectedAFolder()) { 
				labels.add("fs_44"); // Into folder
				labels.add("fs_30"); // Folder name
				
				values.add(context.getFolderSelected());
				values.addAll(context.getSelectedData());
				d = openForm(new FormFolder(this, Translation.getInstance().getLabel("fs_42"), createHelp("fsnew_folder"), values, labels));
			}
			if (context.isSelectedALink()) {
				labels.add("fs_44"); // Into Folder
				labels.add("fs_29"); // Link name
				
				values.add(context.getFolderSelected());
				values.addAll(context.getSelectedData());
				
				String targetInfo = context.getLinkTargetInfo();
				if (targetInfo != null) JOptionPane.showMessageDialog(panel, targetInfo);
				
				d = openForm(new FormLink(this, Translation.getInstance().getLabel("fs_43"), createHelp("fsnew_link"), values, labels));
			}

			if (d != null) {
				id = context.updateFileSystemObject(d);
				((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).updNode(id); 
			}
			break;

		case 94:	// Delete
			Vector<Integer> delIds = context.removeFileSystemObject();
	    	Iterator<Integer> it = delIds.iterator();
	    	while (it.hasNext()) {
	    		((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).delNode(it.next());	
	    	}
			
			break;
		
		case 96:	// Change block size
			
			if (blockSize == ((FileSystemSettings) settings).getBlockSize() &&
				devSize == ((FileSystemSettings) settings).getDevSize()) break;		
			if (!context.areAnyFile() || 
					(context.areAnyFile() && 
							JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(panel, Translation.getInstance().getError("fs_10"), "Warning", JOptionPane.OK_CANCEL_OPTION))) {

				blockSize = ((FileSystemSettings) settings).getBlockSize();		
				devSize = ((FileSystemSettings) settings).getDevSize();
				getPainter(FOLDER_PAINTER).clearMenu();
				if ("DOS".equals(mgnActionCommand)) {
					context.setAlgorithm(new FileSystemStrategyFAT(blockSize, devSize));
					getPainter(FOLDER_PAINTER).addMenuItem(menuItemsFolder.get(0));
					info.dispose();
					info = new InfoDialog(this, "fs_45", "fs_info", false, INFO_WIDTH, INFO_HEIGTH, null, context.getTableHeaderInfo(), context.getTableInfoData());
				}
				if ("LINUX".equals(mgnActionCommand)) {
					context.setAlgorithm(new FileSystemStrategyUNIX(blockSize, devSize));
					getPainter(FOLDER_PAINTER).addMenuItem(menuItemsFolder.get(1));
					info.dispose();
					info = new InfoDialog(this, "fs_46", "fs_info", false, INFO_WIDTH, INFO_HEIGTH, null, context.getTableHeaderInfo(), context.getTableInfoData());
				}
				panel.setLabel(getAlgorithmInfo());		
				((FilesTreePainter) getPainter(FILESTREE_PAINTER)).initTree();
			} else {
				((FileSystemSettings) settings).setBlockSize(blockSize); // Restore selection
				((FileSystemSettings) settings).setDevSize(devSize); // Restore selection
			}
			
			break;

			// Algorithm selection
		case 97:	// DOS
			if (mgnActionCommand.equals(actionCommand)) break;	
			if (!context.areAnyFile() || 
					(context.areAnyFile() && 
							JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(panel, Translation.getInstance().getError("fs_10"), "Warning", JOptionPane.OK_CANCEL_OPTION))) {
				context.setAlgorithm(new FileSystemStrategyFAT(blockSize, devSize));
				panel.setLabel(getAlgorithmInfo());	
				getPainter(FOLDER_PAINTER).clearMenu();
				getPainter(FOLDER_PAINTER).addMenuItem(menuItemsFolder.get(0));
				info.dispose();
				info = new InfoDialog(this, "fs_45", "fs_07", false, INFO_WIDTH, INFO_HEIGTH, null, context.getTableHeaderInfo(), context.getTableInfoData());
				mgnActionCommand = "DOS";
				((FilesTreePainter) getPainter(FILESTREE_PAINTER)).initTree();
			} else {
				((FileSystemSettings) settings).setAlgorithm("LINUX"); // Restore selection
			}
			break;
		case 98:	// Linux
			if (mgnActionCommand.equals(actionCommand)) break;
			if (!context.areAnyFile() ||
					(context.areAnyFile() && 
							JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(panel, Translation.getInstance().getError("fs_10"), "Warning", JOptionPane.OK_CANCEL_OPTION))) {
				context.setAlgorithm(new FileSystemStrategyUNIX(blockSize, devSize));
				panel.setLabel(getAlgorithmInfo());	
				getPainter(FOLDER_PAINTER).clearMenu();
				getPainter(FOLDER_PAINTER).addMenuItem(menuItemsFolder.get(1));
				info.dispose();
				info = new InfoDialog(this, "fs_46", "fs_08", false, INFO_WIDTH, INFO_HEIGTH, null, context.getTableHeaderInfo(), context.getTableInfoData());
				mgnActionCommand = "LINUX";
				((FilesTreePainter) getPainter(FILESTREE_PAINTER)).initTree();
			} else {
				((FileSystemSettings) settings).setAlgorithm("DOS"); // Restore selection
			}
			break;

		case 99:	// Show item details. FAT item's summary
			detailsTableStack.push(new InfoDialog(this, "fs_11", "fsdetail_info", true, FATSUM_WIDTH, FATSUM_HEIGTH, null, context.getDetailInfoHeader(), context.getDetailInfoData()));
			detailsTableStack.peek().setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
			detailsTableStack.peek().setVisible(true);
			detailsTableStack.pop(); // Removes window after closing
			break;
		case 100:	// Show item details. i-node information
			detailsTableStack.push(new InfoDialog(this, menuItemsItem, "fs_12", "fsdetail_info", true, INODE_WIDTH, INODE_HEIGTH, null, context.getDetailInfoHeader(), context.getDetailInfoData())); 
			detailsTableStack.peek().setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
			detailsTableStack.peek().setVisible(true);
			detailsTableStack.pop(); // Removes window after closing
			break;
		case 101:	// Show indirect item details. indirection information
			detailsTableStack.push(new InfoDialog(this, menuItemsItem, "fs_65", "fsdetail_info", true, INODE_WIDTH, INODE_HEIGTH, null, context.getInnerDetailInfoHeader(), context.getInnerDetailInfoData())); 
			detailsTableStack.peek().setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
			Point p = detailsTableStack.peek().getLocation();
			int stackSize = detailsTableStack.size();
			detailsTableStack.peek().setLocation((int) p.getX() + 40 * stackSize,(int) p.getY() + 40 * stackSize);
			detailsTableStack.peek().setVisible(true);
			detailsTableStack.pop(); // Removes window after closing
			break;
		}

		((FolderPainter) getPainter(FOLDER_PAINTER)).updateFolderData(context.getFolderSelected(), context.getSelectedFolderHeader(), context.getSelectedFolderData());
	}
	
	/**
	 * Manage mouse pressed events generated by views<br/>
	 * Over a painter, mouse entered, selects painter's object under mouse icon and opens a pop up menu showing possible actions allowed for concrete painter.
	 * Otherwise, if mouse is over a color change label opens a color chooser dialog, if it is over a help icon it opens corresponding help panel.
	 *
	 * @param e	mouse event
	 */
	public void mousePressed(MouseEvent e) {
		try { // Help labels or color
			JLabel help = (JLabel) e.getSource(); 

			String value = helps.get(help); 

			if (value != null) {
				HelpDialog helpPanel = new HelpDialog(this.getPanel(), "all_61", Functions.getInstance().getPropertyString("help_file"), value, HELP_WIDTH, HELP_HEIGHT);
				helpPanel.scrollToKey(value);
			} else {
				// Color
				form.changeColor();
			}
			
		} catch (Exception e1) { 
			//JTree tree = (JTree) e.getSource();
			Integer id = this.getPainter(FILESTREE_PAINTER).detectMouseOver(e.getX(), e.getY());

			if (id != null) {
				selectElement(id, this.getPainter(FILESTREE_PAINTER));

				((FolderPainter) getPainter(FOLDER_PAINTER)).updateFolderData(context.getFolderSelected(), context.getSelectedFolderHeader(), context.getSelectedFolderData());
				
				if (e.isPopupTrigger()) {
					this.getPainter(FILESTREE_PAINTER).showPopupMenu();
				}
				//repaint();
			}
		}
	}
	
	/**
	 * @see ContextFileSystem#setSelectedLogicalObject(int)
	 * 
	 * @return object can be selected
	 */
	public boolean selectElement(Integer id, PainterTemplate pt) {
		return context.setSelectedLogicalObject(id.intValue());
	}
	
	/**
	 * @see ContextFileSystem#setselectedPhysicalObject(int)
	 */
	public void setselectedPhysicalObject(Integer id) {
		context.setselectedPhysicalObject(id.intValue());
	}
	
	/**
	 * @see  FilesTreePainter#getTreeModel()
	 */
	public DefaultTreeModel getTreeModel() {
		return ((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).getTreeModel();
	}
	
	/**
	 * @see  FilesTreePainter#getMap()
	 */
	public Hashtable<Object, Integer> getMap() {
		return ((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).getMap();
	}
	
	/**
	 * Returns true if current algorithm is UNIX 
	 * 
	 * @return true if current algorithm is UNIX
	 */
	public boolean showSoftField() {
		return "LINUX".equals(mgnActionCommand);
	}
	
	/**************************************************************************************************/
	/*************************************   Request management ***************************************/
	/**************************************************************************************************/

	/**
	 * Updates information panel data according to current file system    
	 */
	public void updateInfo() {
		// Puts data 
		info.initData(context.getTableInfoData());
	}

	/**
	 * Translates information panel
	 * 
	 */
	public void updateLabels() {
		// Update info dialog
		info.updateLabels(context.getTableHeaderInfo());	
		((FolderPainter) getPainter(FOLDER_PAINTER)).updateFolderData(context.getFolderSelected(), context.getSelectedFolderHeader(), context.getSelectedFolderData());
	}
	
	/**
	 * Returns null. No iterator is needed
	 */
	public Iterator<Integer> iterator(int i) {
		return null;
	}
	
	/**
	 * @see ContextFileSystem#getAlgorithmInfo()
	 */
	public String getAlgorithmInfo() {
		return context.getAlgorithmInfo();
	}
	
	/**
	 * @see ContextFileSystem#getDiskSize()
	 */
	public int getDiskSize() {
		return context.getDiskSize();
	}

	/**
	 * @see ContextFileSystem#getRootId()
	 */
	public int getRootId() {
		return context.getRootId();
	}
	
	/**
	 * @see ContextFileSystem#getRootIds()
	 */
	public int[] getRootIds() {
		return context.getRootIds();
	}
	
	/**
	 * @see FileSystemSettings#getBlockSize()
	 */
	public int getBlockSize() {
		return ((FileSystemSettings) settings).getBlockSize();
	}

	/**
	 * Returns null. No information is needed. 
	 */
	public Vector<String> getInfo(int pid) {
		return null;
	}
	
	/**
	 * @see ContextFileSystem#getName(int)
	 */
	public String getName(int block) {
		return context.getName(block);
	}
	
	/**
	 * @see ContextFileSystem#getPath(List)
	 */
	public String getPath(List<String> path) {
		return context.getPath(path);
	}

	/**
	 * @see ContextFileSystem#getColor(int)
	 */
	public Color getColor(int id) {
		return context.getColor(id);
	}

	/**
	 * @see ContextFileSystem#getBlockColor(int)
	 */
	public Color getBlockColor(int block) {
		return context.getBlockColor(block);
	}

	/**
	 * @see ContextFileSystem#getBlockString(int)
	 */
	public String getBlockString(int block) {
		return context.getBlockString(block);
	}
	
	/**
	 * Returns null. No information header is needed. 
	 */
	public Vector<Object> getHeaderInfo() {
		return null;
	}
	
	/**************************************************************************************************/
	/*************************************   XML management ***************************************/
	/**************************************************************************************************/

	/**
	 * Returns XML root element for file system management simulation files
	 * 
	 */
	public String getXMLRoot() {
		// Returns XML root element 
		return Functions.getInstance().getPropertyString("xml_root_fs");
	}
	
	/**
	 * Returns XML direct root children for memory management simulation files: 
	 * "params" and "fsobjects".
	 * Every child groups simulation persistent object    
	 * 
	 */
	public Vector<String> getXMLChilds() {
		Vector<String> childs = new Vector<String>();
		childs.add("params");
		childs.add("fsobjects");
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
			attribute.add("fileSystem");
			attribute.add(mgnActionCommand);
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("blockSize");
			attribute.add(Integer.toString(blockSize));
			param.add(attribute);
			attribute = new Vector<String>();		
			attribute.add("devSize");
			attribute.add(Integer.toString(devSize));
			param.add(attribute);
			data.add(param);
			break;
		case 1: 	// File system items: Folders, Files, Links
			data = context.getXMLDataFileSystemItems(); // Except Root folder. Creates at init
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
				String sBlockSize = data.get(0).get(1).get(1);
				String sdevSize = data.get(0).get(2).get(1);
				settings.selectAlgorithm(actionCommand);
				blockSize = new Integer(sBlockSize).intValue();
				((FileSystemSettings) settings).setBlockSize(blockSize); // Restore selection
				devSize = new Integer(sdevSize).intValue();
				((FileSystemSettings) settings).setDevSize(devSize); // Restore selection
				mgnActionCommand = "";
				actionSpecific(actionCommand); // Inits filesystem. Creates Root folder
				break;
			case 1:   // File system items: Folders, Files, Links
				for (int i=0; i<data.size(); i++) { 
					Vector<Vector<String>> item = data.get(i);
					Vector<Object> itemData = new Vector<Object>();  
					
					int id = new Integer(item.get(0).get(1)).intValue(); // id. Value at position 1
					itemData.add(item.get(1).get(1)); // name. Value at position 1
					int parentid = new Integer(item.get(2).get(1)).intValue(); // parent id. Value at position 1
					context.setSelectedLogicalObject(parentid);
					
					String type = item.get(3).get(1);
					if ("folder".equals(type)) {
						int[] ids = context.addFolder(id, itemData);
						((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(parentid, ids[0]); // Folder
						((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(ids[0], ids[1]); // "."
						((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(ids[0], ids[2]); // ".."
					}
					if ("link".equals(type)) {
						itemData.add(new Integer(item.get(4).get(1))); // link id. Value at position 1
						itemData.add(new Boolean(item.get(5).get(1))); // Soft. Value at position 1
						if (item.size() > 6) {
							// Removed hard link target
							itemData.add(new Integer(item.get(6).get(1))); // target size. Value at position 1
							itemData.add(new Color(new Integer(item.get(7).get(1)))); // target color. Value at position 1
						}
						context.addLink(id, itemData);
						((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(parentid, id);
					}
					if ("file".equals(type)) {	
						itemData.add(new Integer(item.get(4).get(1))); // size. Value at position 1
						itemData.add(new Color(new Integer(item.get(5).get(1)))); // color. Value at position 1
						context.addFile(id, itemData);
						((FilesTreePainter) this.getPainter(FILESTREE_PAINTER)).addNode(parentid, id);
					} 
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SoSimException("all_04");
		}
	}
}
