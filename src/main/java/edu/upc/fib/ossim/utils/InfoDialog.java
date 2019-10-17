package edu.upc.fib.ossim.utils;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.MenuElement;
import javax.swing.ScrollPaneLayout;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;

import edu.upc.fib.ossim.template.Presenter;


/**
 * Shows a dialog with information painted as:
 * 		<ul>
 *		<li>(optional) labels, couples of keys and values</li>
 * 		<li>Information table</li>
 * 		</ul>
 * 
 * Labels keys reference to strings and depends on current session language. A help reference is associated with the dialog, this references points to an anchor into a help file and can be opened through a help icon.     
 * Optionally a pop up menu is also associated with the dialog 
 */
public class InfoDialog extends EscapeDialog { 
	private static final long serialVersionUID = 1L;

	private JPopupMenu popup;
	private Vector<JMenuItem> items;
	private Vector<String[]> menuItems;
	private String keyTitle;
	private String keyHelp;
	private Hashtable<String, JLabel> mapKeyStats; // JLabel keys mapped
	private Hashtable<Integer, JLabel> mapStats; // JLabel values mapped
	private JLabel help;
	private AppTableModel tablemodel;
	private JTable table;
	private JScrollPane scroll;
	private Presenter presenter;

	/**
	 * Constructs a dialog that shows information: text and a table, an icon that opens help reference, 
	 * and a pop up associated with mouse event     
	 * 
	 * @param presenter		event manager 
	 * @param menuItems		pop up menu items
	 * @param keyTitle		reference to dialog title
	 * @param keyHelp		help reference
	 * @param modal			modal property 
	 * @param width			dialog width
	 * @param height		dialog height 
	 * @param stats			information labels (matrix)
	 * @param header		table information header
	 * @param data			table information data
	 */
	public InfoDialog(Presenter presenter, Vector<String[]> menuItems, String keyTitle, String keyHelp, boolean modal, int width, int height, Vector<Vector<String>> stats, Vector<Object> header, Vector<Vector<Object>> data) { 
		super();
		this.menuItems = menuItems;
		tablemodel = new AppTableModel(data, header, false); // Not editable
		createPopupMenu(presenter);
		infoDialog(presenter, keyTitle, keyHelp, modal, width, height, stats, header);
	}

	/**
	 * Constructs a dialog that shows information: text and a table, an icon that opens help reference, 
	 * 
	 * @param presenter		event manager 
	 * @param keyTitle		dialog title
	 * @param keyHelp		help reference
	 * @param modal			modal property 
	 * @param width			dialog width
	 * @param height		dialog height 
	 * @param stats			information labels (matrix)
	 * @param header		table information header
	 * @param data			table information data
	 */
	public InfoDialog(Presenter presenter, String keyTitle, String keyHelp, boolean modal, int width, int height, Vector<Vector<String>> stats, Vector<Object> header, Vector<Vector<Object>> data) { 
		super();
		tablemodel = new AppTableModel(data, header, false); // Not editable
		infoDialog(presenter, keyTitle, keyHelp, modal, width, height, stats, header);
	}

	/**
	 * Constructs a dialog that shows information: text and a table (empty), an icon that opens help reference, 
	 * and a pop up associated with mouse event     
	 * 
	 * @param presenter		event manager 
	 * @param menuItems		pop up menu items
	 * @param keyTitle		dialog title
	 * @param keyHelp		help reference
	 * @param modal			modal property 
	 * @param width			dialog width
	 * @param height		dialog height 
	 * @param stats			information labels (matrix)
	 * @param header		table information header
	 */
	public InfoDialog(Presenter presenter, Vector<String[]> menuItems, String keyTitle, String keyHelp, boolean modal, int width, int height, Vector<Vector<String>> stats, Vector<Object> header) { 
		super();
		this.menuItems = menuItems;
		tablemodel = new AppTableModel(header, false); // Not editable
		createPopupMenu(presenter);
		infoDialog(presenter, keyTitle, keyHelp, modal, width, height, stats, header);
	}

	/**
	 * Constructs a dialog that shows information: text and a table (empty), an icon that opens help reference. 
	 * 
	 * @param presenter		event manager 
	 * @param keyTitle		dialog title
	 * @param keyHelp		help reference
	 * @param modal			modal property 
	 * @param width			dialog width
	 * @param height		dialog height 
	 * @param stats			information labels (matrix)
	 * @param header		table information header
	 */
	public InfoDialog(Presenter presenter, String keyTitle, String keyHelp, boolean modal, int width, int height, Vector<Vector<String>> stats, Vector<Object> header) { 
		super();
		tablemodel = new AppTableModel(header, false); // Not editable
		infoDialog(presenter, keyTitle, keyHelp, modal, width, height, stats, header);
	}

	private void infoDialog(Presenter presenter, String keyTitle, String keyHelp, boolean modal, int width, int height, Vector<Vector<String>> stats, Vector<Object> header) {
		this.keyTitle = keyTitle;
		this.keyHelp = keyHelp;
		this.presenter = presenter;
		this.mapKeyStats = new Hashtable<String, JLabel>(); 
		this.mapStats = new Hashtable<Integer, JLabel>();
		this.setTitle(Translation.getInstance().getLabel(keyTitle));
		this.setModal(modal);
		JPanel pn = init(width, height, stats);
		this.setContentPane(pn);
		this.pack();
	}

	/**
	 * Adds a data row to table's model 
	 * 
	 * @param data	new row's data vector 
	 */
	public void addRow(Vector<Object> data) {
		tablemodel.addRow(data);
	}

	/**
	 * Removes a row from table's model	
	 * 
	 * @param pid	row identifier
	 */
	public void removeRow(int pid) {
		int row = findRowById(pid);
		if (row >= 0) tablemodel.removeRow(row);
	}

	/**
	 * Updates a row data from table's model  
	 * 	 
	 * @param pid	row identifier
	 * @param data	new row's data vector 
	 */
	public void updateRow(int pid, Vector<Object> data) {
		int row = findRowById(pid);
		if (row >= 0) {
			for(int i=0;i<data.size();i++) tablemodel.setValueAt(data.get(i), row, i);
		}
	}

	private int findRowById(int pid) {
		for(int i=0;i<tablemodel.getRowCount();i++) {
			ColorCell cell = (ColorCell) tablemodel.getValueAt(i, 0);
			Integer PID = new Integer((String) cell.getValue());
			if (PID.intValue() == pid) return i;
		}
		return -1;
	}

	/**
	 * Initialize table's model data 
	 * 
	 * @param data	new table's model data
	 */
	public void initData(Vector<Vector<Object>> data) {
		while (tablemodel.getRowCount() > 0) tablemodel.removeRow(0);

		if (data != null) {
			for (int i = 0; i< data.size(); i++) {
				tablemodel.addRow(data.get(i));
			}
		}
	}

	private JPanel init(int width, int height, Vector<Vector<String>> stats) {
		JPanel pn = new JPanel(); 
		pn.setLayout(new BoxLayout(pn, BoxLayout.PAGE_AXIS));

		// Create Help
		JPanel phelp = new JPanel(); 
		phelp.setLayout(new BoxLayout(phelp, BoxLayout.LINE_AXIS));
		phelp.add(Box.createHorizontalGlue());
		help = presenter.createHelp(keyHelp);
		phelp.add(help);
		pn.add(phelp);

		// Create statistics
		if (stats != null) {
			JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel pstats = new JPanel(new SpringLayout());
			JLabel key, value;
			for(int i=0; i<stats.size();i++) {
				Vector<String> row = stats.get(i);

				key = new JLabel(Translation.getInstance().getLabel(row.get(0))); 
				value = new JLabel(row.get(1));

				pstats.add(key); // Description
				pstats.add(value); // Value

				mapKeyStats.put(row.get(0), key); 
				mapStats.put(i, value);
			}

			Functions.getInstance().makeCompactGrid(pstats, stats.size(), 2, 0, 0, 20, 20);
			pane.add(pstats);
			pn.add(pane);
		}

		// Create table
		table = new JTable(tablemodel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(presenter);

		for (int i = 0; i< table.getColumnCount(); i++) {
			TableColumn columna = table.getColumnModel().getColumn(i);
			columna.setCellRenderer(new ColorRenderer());
		}

		table.setAutoCreateRowSorter(true);
		scroll = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		scroll.setLayout(new ScrollPaneLayout());
		scroll.setPreferredSize(new Dimension(width,height));
		pn.add(scroll);

		return pn;
	}

	
	/**
	 * Updates labels names references by labels and column names
	 * to session language
	 * 
	 */
	public void updateHeader(Vector<Object> header) {
		if (header.size() > table.getColumnCount()) table.addColumn(new TableColumn());
		if (header.size() < table.getColumnCount()) table.removeColumn(table.getColumnModel().getColumn(table.getColumnCount()-1));
		
		for (int i = 0; i< table.getColumnCount(); i++) {
			TableColumn columna = table.getColumnModel().getColumn(i);
			columna.setHeaderValue(header.get(i));
		}
	}
	
	/**
	 * Updates labels names references by labels and column names
	 * to session language
	 * 
	 */
	public void updateLabels(Vector<Object> header) {
		for (int i = 0; i< table.getColumnCount(); i++) {
			TableColumn columna = table.getColumnModel().getColumn(i);
			columna.setHeaderValue(header.get(i));
		}

		this.setTitle(Translation.getInstance().getLabel(keyTitle));
		help = presenter.createHelp(keyHelp);

		Set<String> keys = mapKeyStats.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			mapKeyStats.get(key).setText(Translation.getInstance().getLabel(key));
		}
		if (this.isVisible()) {
			this.setVisible(false);
			table.validate();
			this.setVisible(true);
		}
	}

	/**
	 * Updates labels values with a new ones 
	 * 
	 * @param values	new information values 
	 */
	public void updateValues(Vector<String> values) {
		for(int i=0; i<values.size();i++) {
			mapStats.get(i).setText(values.get(i));
		}
	}

	/**
	 * Creates a pop up menu containing a set of items  
	 * 
	 * @param presenter		menu event manager
	 */
	private void createPopupMenu(Presenter presenter) {
		//Create the popup menu.
		// Each element items has 3 String's : command, label, icon
		popup = new JPopupMenu();
		items = new Vector<JMenuItem>();
		for (int i = 0; i < menuItems.size(); i++) {
			JMenuItem item = new JMenuItem(menuItems.get(i)[1],Functions.getInstance().createImageIcon(menuItems.get(i)[2]));
			item.setActionCommand(menuItems.get(i)[0]);
			item.addActionListener(presenter);
			popup.add(item);
			items.add(item);
		}
	}

	/**
	 * Shows pop up menu at mouse position
	 * 
	 */
	public void showPopupMenu() {
		Point mouse = this.getMousePosition();
		if (mouse != null) {
			MenuElement[] elements = popup.getSubElements();
			for (int i=0; i<elements.length;i++) {
				String actionCommand = 	((JMenuItem) elements[i]).getActionCommand();	
				String key = "";
				for (int j=0; j<menuItems.size();j++) { 
					if (actionCommand.equals(menuItems.get(j)[0]))	key = menuItems.get(j)[1];	
				}
				
				if (!("".equals(key))) ((JMenuItem) elements[i]).setText(Translation.getInstance().getLabel(key));  
			}
			popup.show(this, this.getMousePosition().x, this.getMousePosition().y);
		}
	}

	/**
	 * Returns row number if selected or null if any is selected  
	 * 
	 * @return	selected row number or null
	 */
	public Integer detectMouseOver() {
		int row = table.getSelectedRow();
		if (row >= 0) return row;
		return null;
	}

	/**
	 * Returns cell value
	 * 
	 * @param row	
	 * @param col
	 * @return	cell value
	 */
	public Object getValueAt(int row, int col) {
		ColorCell cc = (ColorCell) tablemodel.getValueAt(row, col); 
		return cc.getValue();
	}

	/**
	 * Returns model that generates table events
	 * 
	 * @return table model
	 */
	public Object getEventSource() {
		return table.getSelectionModel();
	}
} 
