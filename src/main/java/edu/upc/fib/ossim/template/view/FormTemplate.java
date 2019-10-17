package edu.upc.fib.ossim.template.view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.utils.EscapeDialog;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Application form template (Template Pattern). 
 * Form template contains title, modal property, form values and labels. 
 * Optionally a ColorChooser and a submit button may be included into implementations.
 * All form events are managed through a Presenter's instance (Model-View-Presenter Pattern). <br/> 
 * A help reference is associated with the template, this references points to an anchor into a help file and can be opened through a help icon. 
 * Form fields are layered out as a grid 
 *  
 * @author Alex Macia
 * 
 * @see Presenter
 */
public abstract class FormTemplate extends EscapeDialog { 
	private static final long serialVersionUID = 1L;

	private JLabel scolor;
	protected Color color;
	protected JPanel pn;
	protected JPanel grid;
	protected Vector<Object> values;
	protected Vector<String> labels;
	protected boolean ok;
	protected Presenter presenter;

	/**
	 * Constructs a FormTemplate titled, adding help icon and initializing field values
	 * 
	 * @param presenter	event manager
	 * @param title		dialog title
	 * @param help		help icon  
	 * @param values	initial fields values
	 * 
	 * @see #init(Vector)
	 */
	public FormTemplate(Presenter presenter, String title, JLabel help, Vector<Object> values)  { 
		formTemplate(presenter, title, help, values);
		init(values);
		this.setContentPane(pn);
		this.pack();
	}

	/**
	 * Constructs a FormTemplate titled, adding help icon, initializing field values and labels
	 * 
	 * @param presenter	event manager
	 * @param title		dialog title
	 * @param help		help icon  
	 * @param values	initial fields values
	 * @param labels	field's labels	
	 * 
	 * @see #init(Vector)
	 */
	public FormTemplate(Presenter presenter, String title, JLabel help, Vector<Object> values, Vector<String> labels)  { 
		formTemplate(presenter, title, help, values);
		this.labels = labels;
		init(values);
		this.setContentPane(pn);
		this.pack();
	}

	private void formTemplate(Presenter presenter, String title, JLabel help, Vector<Object> values)  { 
		this.presenter = presenter;
		ok = false;
		this.values = values;
		this.setModal(true);	// Forms are always modal
		this.setTitle(title);
		pn = new JPanel(); 	
		pn.setLayout(new BoxLayout(pn,  BoxLayout.PAGE_AXIS));

		JPanel phelp = new JPanel(); 
		phelp.setLayout(new BoxLayout(phelp, BoxLayout.LINE_AXIS));
		phelp.add(Box.createHorizontalGlue());
		phelp.add(help);
		pn.add(phelp);

		grid = new JPanel(new SpringLayout());
	}

	/**
	 * Abstract method to implement specific form content
	 * 
	 * @param values field values
	 */
	public abstract void init(Vector<Object> values);

	/** 
	 * Adds a color chooser into form 
	 * 
	 * @param color
	 */
	public void initColor(Color color) {
		grid.add(new JLabel(Translation.getInstance().getLabel("all_01")));
		scolor = new JLabel();
		scolor.setName("color");
		scolor.setPreferredSize(new Dimension(20,20));
		scolor.setOpaque(true);
		scolor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.color = color;        
		scolor.setBackground(color);
		scolor.addMouseListener(presenter);
		grid.add(scolor);
	}

	/**
	 * Returns random rgb Color.<br/>
	 * rgb values range [250 - 150], soft colors  
	 * 
	 * @return rgb color
	 */
	public Color RandColor() {
		Random rand = new Random();
		int r = rand.nextInt(100);
		int g = rand.nextInt(100);
		int b = rand.nextInt(100);
		return new Color(250-r,250-g,250-b);
	}

	/**
	 * Shows color chooser dialog and updates color field with user selection
	 */
	public void changeColor() {
		color = JColorChooser.showDialog(this, Translation.getInstance().getLabel("all_03"), color);
		if (color != null) scolor.setBackground(color);
	}

	/** 
	 * Adds a submit button into form 
	 */
	public void addOKButton() {
		// Button
		JPanel pbuttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton bok = new JButton(Translation.getInstance().getLabel("all_02"));
		bok.setActionCommand("OK");
		bok.addActionListener(presenter);
		this.getRootPane().setDefaultButton(bok);
		
		pbuttons.add(bok);
		pn.add(pbuttons);
	}

	/**
	 * Sets submission state (true-ok, false-cancel)
	 * 
	 * @param b	submission state
	 */
	public void setOK(boolean b) {
		this.ok = b;
	}

	/**
	 * Returns data from form on submission
	 * 
	 * @return vector containing form fields data or null on user cancel  
	 */
	public Vector<Object> getData() {
		try {
			if (ok) return getSpecificData();
			else return null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Abstract method to implement specific return form fields values
	 * 
	 * @return vector containing form fields data
	 */
	public abstract  Vector<Object> getSpecificData();

	/**
	 * Validates form fields values on submission and returns success or not.
	 * Should be overwrite into template implementation    
	 * 
	 * @return 	default true 
	 */
	public boolean validateFields() {
		return true;
	}
} 
