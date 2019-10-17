package edu.upc.fib.ossim.filesystem.view;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Generic file system object form (Template pattern). Contains common file system objects fields such as: 
 * parent folder, name and a submit button. 
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 */
public abstract class FormFileSystemItem extends FormTemplate { 
	private static final long serialVersionUID = 1L;

	protected JLabel folder;
	protected JTextField name;
	
	/**
	 * Constructor
	 * 
	 * @param presenter	event manager
	 * @param title		form title
	 * @param help		help icon
	 * @param values	(optional) field initial values : folder, name, size and color
	 * @param labels	field labels
	 */
	public FormFileSystemItem(Presenter presenter, String title, JLabel help, Vector<Object> values, Vector<String> labels) { 
		super(presenter, title, help, values, labels);
	}
	
	/**
	 * Creates and initialize common form fields: parent folder, name and submit button. 
	 * 
	 *  @see #initSpecific(Vector)
	 */
	public void init(Vector<Object> values) {
		JPanel pfolder = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		// Max folder length 60 characters
		String sfolder = Translation.getInstance().getLabel(labels.get(0)) + "\"" +  values.get(0) + "\"";
		if (sfolder.length() > 60) sfolder = sfolder.substring(0, 57) + "...\"";
		
		folder = new JLabel(sfolder);
		pfolder.add(folder);
		pn.add(pfolder);
		
		grid.add(new JLabel(Translation.getInstance().getLabel(labels.get(1))));
		if (values.size() > 1) name = new JTextField(values.get(1).toString(), 10);
		else name = new JTextField(10);
		name.addFocusListener(presenter);
		grid.add(name);

		initSpecific(values);
		
		addOKButton();
    }
	
	/**
	 * Concrete implementations may implement this method to add its own fields
	 * 
	 * @param values
	 */
	public abstract void initSpecific(Vector<Object> values); 
	
	/**
	 * Validates file system object name that is required         
	 * 
	 * @return	validation result
	 * 
	 */
	public boolean validateFields() {
		if ("".equals(name.getText()) || name.getText() == null) {
			JOptionPane.showMessageDialog(this.getParent(),Translation.getInstance().getError("all_01"),"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Returns a vector containing form values
	 *
	 * @return form data
	 */	
	public Vector<Object> getSpecificData() {
		Vector<Object> data = new Vector<Object>();
		data.add(name.getText());

		return data;
	}
} 
