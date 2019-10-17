package edu.upc.fib.ossim.filesystem.view;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.utils.Functions;


/**
 * Folder form. Adds or updates a folder, only contains common file system objects fields: 
 * parent folder, name and a submit button. 
 * 
 * @author Alex Macia
 * 
 * @see FormFileSystemItem
 */
public class FormFolder extends FormFileSystemItem { 

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a FormFolder
	 * 
	 * @see FormFileSystemItem#FormFileSystemItem(Presenter, String, JLabel, Vector, Vector)
	 */
	public FormFolder(Presenter presenter, String title, JLabel help, Vector<Object> values, Vector<String> labels) {
		super(presenter, title, help, values, labels);
	}

	/**
	 * Fields and its labels are laid out as a compact grid.
	 *  
	 * @see FormFileSystemItem#initSpecific(Vector)
	 * @see Functions#makeCompactGrid(java.awt.Container, int, int, int, int, int, int)
	 */
	public void initSpecific(Vector<Object> values) {
		Functions.getInstance().makeCompactGrid(grid, 1, 2, 6, 6, 6, 6);
		JPanel pgrid = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pgrid.add(grid);
		pn.add(pgrid);
    }
} 
