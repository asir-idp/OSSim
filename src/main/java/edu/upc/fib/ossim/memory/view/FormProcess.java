package edu.upc.fib.ossim.memory.view;
import java.awt.Color;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Process creation and update form in contiguous memory management. 
 * Process fields are: process identifier (pid), name, size, duration and color.<br/>
 * A process name is always required and duration should not be 0, 
 * when updating a process, form fields can be initialized with process values   
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 */
public class FormProcess extends FormTemplate { 
	private static final long serialVersionUID = 1L;
	public static final int MAX_PROGSIZE = 64;

	private String spid;
	private JTextField name;
	protected JSpinner size;	// Size
	private JSpinner duration;	
	
	/**
	 * Constructs a form process
	 * 
	 * @param presenter	event manager
	 * @param title		form title
	 * @param help		help icon
	 * @param values	creating a new process: pid, updating an existing process: pid, name, size, duration and color    
	 */
	public FormProcess(Presenter presenter, String title, JLabel help, Vector<Object> values) { 
		super(presenter, title, help, values);
	}

	/**
	 * Creates and initialize form fields.
 	 * Fields and its labels are laid out as a compact grid.
	 * 
	 * @see Functions#makeCompactGrid(java.awt.Container, int, int, int, int, int, int)
	 */
	public void init(Vector<Object> values) {
		/* PID, Name, size, duration, color */
		spid = values.get(0).toString();
		grid.add(new JLabel(Translation.getInstance().getLabel("me_30") + spid));
		grid.add(new JLabel(""));
		
		grid.add(new JLabel(Translation.getInstance().getLabel("me_31")));
		if (values.size() > 1) name = new JTextField(values.get(1).toString(), 10);
		else name = new JTextField(10);
		name.addFocusListener(presenter);
		grid.add(name);
		
		grid.add(new JLabel(Translation.getInstance().getLabel("me_32")));
		SpinnerModel spmodel;
		if (values.size() > 1) spmodel = new SpinnerNumberModel(Integer.parseInt(values.get(2).toString()), 1, MAX_PROGSIZE, 1);
		else spmodel = new SpinnerNumberModel(1, 1, MAX_PROGSIZE, 1);
		size = new JSpinner(spmodel);
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) size.getEditor();
		editor.getTextField().addFocusListener(presenter);
		size.setName("size");
		grid.add(size);
        
		grid.add(new JLabel(Translation.getInstance().getLabel("me_33")));
		SpinnerModel spmodelDuration;
		if (values.size() > 1) spmodelDuration = new SpinnerNumberModel(Integer.parseInt(values.get(3).toString()), -1, 100, 1);
		else spmodelDuration = new SpinnerNumberModel(-1, -1, 100, 1);
		duration = new JSpinner(spmodelDuration);
		editor = (JSpinner.DefaultEditor) duration.getEditor();
		editor.getTextField().addFocusListener(presenter);
		grid.add(duration);
       
        if (values.size() > 1) initColor((Color) values.get(4));
        else initColor(RandColor());
		
		Functions.getInstance().makeCompactGrid(grid, 5, 2, 6, 6, 6, 6);
		pn.add(grid);
		
        initBlocks(values); 
        
        addOKButton();
	}
	
	/**
	 * Contiguous memory management has no blocks, pagination and segmentation process forms 
	 * (non contiguous management) inherit this form and implements this method 
	 * 
	 * @param values	null
	 * 
	 * @see FormProcessPag
	 * @see FormProcessSeg
	 */
	public void initBlocks(Vector<Object> values) {
		// Do nothing
	}

	/**
	 * Validates process name field and duration <> 0        
	 * 
	 * @return	validation result
	 * 
	 */
	public boolean validateFields() {
		if ("".equals(name.getText()) || name.getText() == null) {
			JOptionPane.showMessageDialog(this.getParent(),Translation.getInstance().getError("all_01"),"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (((Integer) duration.getValue()).intValue() == 0) {
			JOptionPane.showMessageDialog(this.getParent(),Translation.getInstance().getError("all_02"),"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
				
		return validateFieldsBlock();
	}

	/**
	 * Always true
	 * 
	 * @return	validation result
	 * 
	 * @see FormProcessPag
	 * @see FormProcessSeg
	 */
	public boolean validateFieldsBlock() {
		return true;
	}
	
	/**
	 * Returns a vector containing form values
	 *
	 * @return form data
	 */
	public Vector<Object> getSpecificData() {
		Vector<Object> data = new Vector<Object>();
		data.add(spid);
		data.add(name.getText());
		data.add(size.getValue());
		data.add(duration.getValue());
		data.add(color);

		return data;
	}
	
	/**
	 * Returns null
	 * 
	 * @return	nothing
	 * 
	 * @see FormProcessPag
	 * @see FormProcessSeg
	 */
	@SuppressWarnings("rawtypes")
	public Vector<Vector> getComponentsData() {
		return null;
	}
} 
