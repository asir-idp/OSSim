package edu.upc.fib.ossim.memory.view;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Address translation form. Translates logical process addresses to main memory physical addresses, 
 * report also errors such us invalid addresses, page faults and segment faults. 
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 */
public class FormAddress extends FormTemplate implements ChangeListener { 
	private static final long serialVersionUID = 1L;

	private JSpinner address;	// 0 <= logical address < size 	
	private JLabel phyAddr;	// Physical Address	
	
	/**
	 * Construct a FormAddress
	 *  
	 * @param presenter	event manager
	 * @param title		form title
	 * @param help		help icon
	 * @param values	Process information    
	 */
	public FormAddress(Presenter presenter, String title, JLabel help, Vector<Object> values) { 
		super(presenter, title, help, values);
	}


	/**
	 * Creates and initialize form fields 
	 */
	public void init(Vector<Object> values) {
		/* program info  */
		grid.add(new JLabel(Translation.getInstance().getLabel("me_42")));
		grid.add(new JLabel(values.get(0).toString()));
		
		grid.add(new JLabel(Translation.getInstance().getLabel("me_83")));
		SpinnerModel spmodel = new SpinnerNumberModel(0, 0, MemorySettings.MAX_MEMSIZE, 1);
		address = new JSpinner(spmodel);
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) address.getEditor();
		editor.getTextField().addFocusListener(presenter);
		address.addChangeListener(this);
		grid.add(address);

		grid.add(new JLabel(Translation.getInstance().getLabel("me_84")));
		phyAddr = new JLabel(((MemoryPresenter) presenter).getAddTransPhysical(0));
		grid.add(phyAddr);
		
		Functions.getInstance().makeCompactGrid(grid, 3, 2, 10, 10, 10, 10);
		pn.add(grid);
	}

	/**
	 * Returns null, no data is generated   
	 * 
	 */
	public Vector<Object> getSpecificData() {
		return null;
	}

	/**
	 * Manage Change event from logical address spinner. Access presenter to translate 
	 * to physical address and updates information   
	 */
	public void stateChanged(ChangeEvent arg0) {
		phyAddr.setText(((MemoryPresenter) presenter).getAddTransPhysical((Integer) address.getValue()));
	}
} 
