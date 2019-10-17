package edu.upc.fib.ossim.filesystem.view;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * File form. Adds or updates a file, besides common file system objects fields such as: 
 * parent folder, name and a submit button, this form includes file size and color controls. 
 * 
 * @author Alex Macia
 * 
 * @see FormFileSystemItem
 */
public class FormFile extends FormFileSystemItem { 

	private static final long serialVersionUID = 1L;

	private JSpinner size;	// Size

	/**
	 * Construct a FormFile
	 * 
	 * @see FormFileSystemItem#FormFileSystemItem(Presenter, String, JLabel, Vector, Vector)
	 */
	public FormFile(Presenter presenter, String title, JLabel help, Vector<Object> values, Vector<String> labels) {
		super(presenter, title, help, values, labels);
	}

	/**
	 * Creates and initialize concrete form fields, file size and color. 
	 * Fields and its labels are laid out as a compact grid.
	 *  
	 * @see FormFileSystemItem#initSpecific(Vector)
	 * @see Functions#makeCompactGrid(java.awt.Container, int, int, int, int, int, int)
	 */
	public void initSpecific(Vector<Object> values) {
		grid.add(new JLabel(Translation.getInstance().getLabel("fs_32")));
		SpinnerModel spmodelSize;
		if (values.size() > 1) spmodelSize = new SpinnerNumberModel(new Integer(values.get(2).toString()).intValue(), 1, ((FileSystemPresenter) presenter).getDiskSize(), 1);
		else spmodelSize = new SpinnerNumberModel(1, 1, ((FileSystemPresenter) presenter).getDiskSize(), 1);
		size = new JSpinner(spmodelSize);
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) size.getEditor();
		editor.getTextField().addFocusListener(presenter);
		grid.add(size);
		
		if (values.size() > 1) initColor((Color) values.get(3));
        else initColor(RandColor());

		Functions.getInstance().makeCompactGrid(grid, 3, 2, 6, 6, 6, 6);
		
		JPanel pgrid = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pgrid.add(grid);
		pn.add(pgrid);
    }
	
	/**
	 * Returns a vector containing form values
	 *
	 * @return form data
	 */
	public Vector<Object> getSpecificData() {
		Vector<Object> data = super.getSpecificData();
		data.add(size.getValue());
		data.add(color);

		return data;
	}
} 
