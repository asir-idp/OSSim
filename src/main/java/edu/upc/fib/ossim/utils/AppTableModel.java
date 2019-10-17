package edu.upc.fib.ossim.utils;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * DefaultTableModel inheritance that manage table edition <br/>
 * and shows boolean columns as checkboxes 
 * 
 * @author Alex Macia
 */

public class AppTableModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private boolean editable; 

	/**
	 * Constructs a new AppTableModel initialized with column names, data and edition property  
	 * 
	 * @param data			Initial table data
	 * @param columnNames	Column names
	 * @param editable		Table edition property
	 */
	public AppTableModel(Vector<Vector<Object>> data, Vector<Object> columnNames, boolean editable) {
		super(data, columnNames);
		this.editable = editable;
	}

	/**
	 * Constructs a new AppTableModel, empty and initialized with column names and edition property  
	 * 
	 * @param columnNames	Column names
	 * @param editable		Table edition property
	 */
	public AppTableModel(Vector<Object> columnNames, boolean editable) {
		super(columnNames, 0);
		this.editable = editable;
	}

	/**
	 * Returns true regardless of parameter values.
	 * 
	 * @param row	the row whose value is to be queried
	 * @param col	the column whose value is to be queried 
	 * 
	 * @return true if cell [row, col] is editable
	 */
	public boolean isCellEditable(int row, int col) {
        // First Column not editable
		if (col == 0) return false;
		return this.editable;
    }
	
	/**
	 * Allows paint boolean columns as checkboxes   
	 * 
	 * @param c	the column whose class is to be queried
	 * 
	 * @return cell [0, c] class 
	 */
	public Class<?> getColumnClass(int c) {
	// Boolean columns as checkbox
		return getValueAt(0, c).getClass();
    }
}
