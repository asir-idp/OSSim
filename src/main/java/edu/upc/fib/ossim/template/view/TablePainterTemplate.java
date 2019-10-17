package edu.upc.fib.ossim.template.view;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.table.TableColumn;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.AppTableModel;
import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.ColorRenderer;
import edu.upc.fib.ossim.utils.Translation;



/**
 * Application Table Painter template (Template Pattern). 
 * Table Painter template extend Painter Template, it is a specialized painter 
 * to display information into a table. Optionally a help icon is associated to the painter 
 * 
 * @author Alex Macia
 */

public abstract class TablePainterTemplate extends PainterTemplate { 
	private static final long serialVersionUID = 1L;

	protected JLabel ltitle;
	protected JLabel help;
	private AppTableModel modeltaula;
	protected JTable table;

	/**
	 * Constructs a TablePainterTemplate and initialize table
	 * 
	 * @param presenter	event manager
	 * @param title		table title
	 * @param keyHelp	reference to table's help 	
	 * @param header	table header
	 * @param menuItems	pop up menu items
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public TablePainterTemplate(Presenter presenter, String title, String keyHelp, Vector<Object> header, Vector<String[]> menuItems, int width, int height) { 
		super(presenter, menuItems, width, height);
		init(header, title, keyHelp, width, height);
	}

	/**
	 * Constructs a TablePainterTemplate and initialize table
	 * 
	 * @param presenter	event manager
	 * @param title		table title
	 * @param keyHelp	reference to table's help 	
	 * @param header	table header
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public TablePainterTemplate(Presenter presenter, String title, String keyHelp, Vector<Object> header, int width, int height) { 
		super(presenter, width, height);
		init(header, title, keyHelp, width, height);
	}

	private void init(Vector<Object> header, String title, String keyHelp, int width, int height) {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		// Create title
		add(createTitle(title, keyHelp));
		
		modeltaula = new AppTableModel(null, header, false); // Not editable
		table = new JTable(modeltaula);
		table.setName("table");
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(presenter);
		this.removeMouseListener(presenter);

		for (int i = 0; i< table.getColumnCount(); i++) {
			TableColumn columna = table.getColumnModel().getColumn(i);
			columna.setCellRenderer(new ColorRenderer());
		}
		
		JScrollPane scroll = new JScrollPane(table);
		table.setFillsViewportHeight(false);
		scroll.setLayout(new ScrollPaneLayout());
		scroll.setPreferredSize(new Dimension(width,height - 10));
        add(scroll);
	}
	
	/**
	 * Abstract method to implement in concrete subclasses
	 * Shows above table its title and optionally a help icon    
	 * 
	 * @param title		table title
	 * @param keyHelp	reference to table's help 	
	 * 
	 * @return Panel containing table's title and optionally a help icon 
	 */
	public abstract JPanel createTitle(String title, String keyHelp);
	
	/**
	 * Loads table data, all incoming processes
	 * 
	 * @param data	table data
	 */
	public void initData(Vector<Vector<Object>> data) {
		while (modeltaula.getRowCount() > 0) modeltaula.removeRow(0);
		
		if (data != null) {
			for (int i = 0; i< data.size(); i++) {
				modeltaula.addRow(data.get(i));
			}
		}
	}

	/**
	 * Updates table title and column names to session language
	 * 
	 * @param header	table's columns names
	 * @param title		key to title 
	 * 
	 */
	public void updateLabels(Vector<Object> header, String title) {
		ltitle.setText(Translation.getInstance().getLabel(title));
		
		for (int i = 0; i< table.getColumnCount(); i++) {
			TableColumn columna = table.getColumnModel().getColumn(i);
			columna.setHeaderValue(header.get(i));
		}
		table.validate();
	}
	
	/**
	 * Returns table row (request) selected or null if any
	 * 
	 * @param y 	
	 * @param x	
	 * 
	 * @return request selected or null if any
	 */
	public Integer detectMouseOver(int x, int y) {
		// Overwrited method
		int row = table.getSelectedRow();
		if (row >= 0) return row;
		return null;
	}
	
	
	
	/**
	 * Sets value is adjusting property. Activate it before a change, when action is finished it is set to false  
	 * 
	 * @param b	property new state 	
	 * 
	 */
	public void setValueIsAdjusting(boolean b)  {
		table.getSelectionModel().setValueIsAdjusting(b);
	}
	
	/**
	 * Gets value from cell [row, col] 
	 * 
	 * @param row	
	 * @param col
	 * @return	value from cell [row, col]
	 */
	public Object getValueAt(int row, int col) {
		ColorCell cc = (ColorCell) modeltaula.getValueAt(row, col); 
		return cc.getValue();
	}
	
	/**
 	 * Returns false. Mouse event are managed <code>#detectMouseOver(int, int)</code>       
	 * 
	 * @param o 	unused
	 * @param x	x 	unused
	 * @param y	y 	unused
	 * 
	 * @return	false 
	 * 
	 * @see #detectMouseOver(int, int)
	 */
	public boolean contains(Object o, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}
}
