package edu.upc.fib.ossim.memory.view;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Main memory block creation and update form. Memory block fields are: start address and size, 
 * required both.<br/>
 * When updating a block, form fields can be initialized with its values   
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 */
public class FormBlock extends FormTemplate { 
	private static final long serialVersionUID = 1L;

	private JSpinner start;
	private JSpinner size;	// Size
	
	/**
	 * Constructs a form memory block
	 * 
	 * @param presenter	event manager
	 * @param title		form title
	 * @param help		help icon
	 * @param values	creating a new block: max memory size, updating an existing process: block start address and size    
	 */
	public FormBlock(Presenter presenter, String title, JLabel help, Vector<Object> values) { 
		super(presenter, title, help, values);
	}

	/**
	 * Creates and initialize form fields.
	 * Fields and its labels are laid out as a compact grid.
	 * 
	 * @see Functions#makeCompactGrid(java.awt.Container, int, int, int, int, int, int)
	 */
	public void init(Vector<Object> values) {
		grid.add(new JLabel(Translation.getInstance().getLabel("me_80")));
		SpinnerModel spmodela;
		if (values.size() > 1) spmodela = new SpinnerNumberModel(new Integer(values.get(1).toString()).intValue(), 0, new Integer(values.get(0).toString()).intValue(), 1);
		else spmodela = new SpinnerNumberModel(0, 0, new Integer(values.get(0).toString()).intValue(), 1);
		start = new JSpinner(spmodela);
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) start.getEditor();
		editor.getTextField().addFocusListener(presenter);
		grid.add(start);

		grid.add(new JLabel(Translation.getInstance().getLabel("me_81")));
		SpinnerModel spmodeli;
		if (values.size() > 1) spmodeli = new SpinnerNumberModel(new Integer(values.get(2).toString()).intValue(), 1, new Integer(values.get(0).toString()).intValue(), 1);
		else spmodeli = new SpinnerNumberModel(1, 1, new Integer(values.get(0).toString()).intValue(), 1);
		size = new JSpinner(spmodeli);
		editor = (JSpinner.DefaultEditor) size.getEditor();
		editor.getTextField().addFocusListener(presenter);
		grid.add(size);
		
		Functions.getInstance().makeCompactGrid(grid, 2, 2, 6, 6, 6, 6);
		pn.add(grid);
		
		addOKButton();
	}

	/**
	 * Returns true, no validation is needed        
	 * 
	 */
	public boolean validateFields() {
		return true;
	}
	
	/**
	 * Returns a vector containing form values
	 *
	 * @return form data
	 */
	public Vector<Object> getSpecificData() {
		Vector<Object> data = new Vector<Object>();
		data.add(start.getValue());
		data.add(size.getValue());
		return data;
	}
} 
