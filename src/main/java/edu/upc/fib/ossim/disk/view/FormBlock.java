package edu.upc.fib.ossim.disk.view;
import java.awt.Color;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Block requests creation and update form. Requests fields are: block number and  
 * request time, form shows block's cylinder, due to this is what algorithms use to serve requests.<br/>
 * When updating a request, form fields can be initialized with request values   
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 */
public class FormBlock extends FormTemplate { 
	private static final long serialVersionUID = 1L;

	private JSpinner bid;
	private JSpinner init;	// Initial time
	private JLabel requestCylinder;	// Request cylinder
	
	/**
	 * Constructs a form block
	 * 
	 * @param presenter	event manager
	 * @param title		form title
	 * @param help		help icon
	 * @param values	creating a new block request: max block number, 
	 * updating an existing request: max block number, block requested and time    
	 */
	public FormBlock(Presenter presenter, String title, JLabel help, Vector<Object> values) { 
		super(presenter, title, help, values);
	}

	public void init(Vector<Object> values) {
		// values: Max blocks, bid, init, color 
		grid.add(new JLabel(Translation.getInstance().getLabel("dk_30")));
		SpinnerModel spmodelBid;
		if (values.size() > 1) spmodelBid = new SpinnerNumberModel(new Integer(values.get(1).toString()).intValue(), 0, new Integer(values.get(0).toString()).intValue() - 1, 1);
		else spmodelBid = new SpinnerNumberModel(0, 0, new Integer(values.get(0).toString()).intValue() - 1, 1);
		bid = new JSpinner(spmodelBid);
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) bid.getEditor();
		editor.getTextField().addFocusListener(presenter);
		bid.setName("bid");
		bid.addChangeListener(presenter);
		grid.add(bid);
		
		grid.add(new JLabel(Translation.getInstance().getLabel("dk_31")));
		SpinnerModel spmodelInit;
		if (values.size() > 1) spmodelInit = new SpinnerNumberModel(new Integer(values.get(2).toString()).intValue(), 0, 100, 1);
		else spmodelInit = new SpinnerNumberModel(0, 0, 100, 1);
		init = new JSpinner(spmodelInit);
		editor = (JSpinner.DefaultEditor) init.getEditor();
		editor.getTextField().addFocusListener(presenter);
		grid.add(init);
		
		grid.add(new JLabel(Translation.getInstance().getLabel("dk_34"))); // Sector's Cylinder 
		int rqCyl = 0;
		if (values.size() > 1) rqCyl = new Integer(values.get(2).toString()).intValue()/((DiskPresenter) presenter).getSectors();
		requestCylinder = new JLabel(new Integer(rqCyl).toString()); // Sector's Cylinder 
		setRequestCylinder();
		grid.add(requestCylinder); 
		
		if (values.size() > 1) initColor((Color) values.get(3));
        else initColor(RandColor());
		
		Functions.getInstance().makeCompactGrid(grid, 4, 2, 6, 6, 6, 6);
		pn.add(grid);
		
		addOKButton();
	}

	/**
	 * Sets current requested block cylinder 
	 * 
	 */
	public void setRequestCylinder() {
		int rqCyl = (Integer) bid.getValue()/((DiskPresenter) presenter).getSectors();
		requestCylinder.setText(new Integer(rqCyl).toString()); // Sector's Cylinder
	}
	
	/**
	 * Returns true, no validation is needed
	 * 
	 * @return true
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
		data.add(bid.getValue());
		data.add(init.getValue());
		data.add(color);
		return data;
	}
} 
