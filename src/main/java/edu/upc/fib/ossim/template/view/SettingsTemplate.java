package edu.upc.fib.ossim.template.view;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.utils.EscapeDialog;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Application Settings Panel template (Template Pattern). 
 * Settings template manage common simulations settings panel's behavior. 
 * An algorithm button group keeps all possible algorithms implemented.   
 * A help reference is associated with the template, this references points to an anchor into a help file and can be opened through a help icon.
 * Panels implementing that template add fields to manage simulation settings 
 * 
 * @author Alex Macia
 */
public abstract class SettingsTemplate extends EscapeDialog { 
	private static final long serialVersionUID = 1L;

	protected SpringLayout layout;
	protected JPanel pane;
	protected JLabel help;
	protected ButtonGroup algorithmGroup;
	protected String keyHelp;

	protected Presenter presenter;
	
	/**
	 * Constructs SettingsTemplate, adds help icon and concrete settings components
	 * 
	 * @param presenter	event manager
	 * @param keyHelp	reference to help 
	 * 
	 * @see #initSpecific()
	 */
	public SettingsTemplate(Presenter presenter, String keyHelp)  { 
		super();
		this.setModal(true); // Settings panels are always modal
		this.setTitle(Translation.getInstance().getLabel("all_10"));
		this.keyHelp = keyHelp;
		this.presenter = presenter;
		this.setContentPane(init());
		this.pack();
	}

	private JPanel init() {
		pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		
		algorithmGroup = new ButtonGroup();
		help = presenter.createHelp(keyHelp);
		
		JPanel phelp = new JPanel(); 
		phelp.setLayout(new BoxLayout(phelp, BoxLayout.LINE_AXIS));
		phelp.add(Box.createHorizontalGlue());
		phelp.add(help);
		pane.add(phelp);
		
		initSpecific();
		return pane;
	}
	
	/**
	 * Adds an algorithm 
	 * 
	 * @param algorithm	button associated with that algorithm
	 */
	public void addAlgorithm(AbstractButton algorithm) {
		algorithmGroup.add(algorithm);
	}
	
	/**
	 * Selects an algorithm button (whose action command is parameter actionCommand) within algorithm button group 
	 * 
	 * @param actionCommand	button action command to select
	 */
	public void selectAlgorithm(String actionCommand) {
		Enumeration<AbstractButton> algorithms = algorithmGroup.getElements();
		while (algorithms.hasMoreElements()) {
			AbstractButton algorithm = algorithms.nextElement();
			if (actionCommand.equals(algorithm.getActionCommand())) algorithm.setSelected(true);
		}
	}

	/**
	 * Abstract method to implement specific settings components 
	 */
	public abstract void initSpecific();
	
	/**
	 * Gets selected algorithm action command
	 *
	 * @return	selected algorithm action command
	 */
	public String getAlgorithm() {
		return algorithmGroup.getSelection().getActionCommand();
	}
	
	/**
	 * Abstract method to implement labels translation 
	 */
	public abstract void updateLabels();
	
} 
