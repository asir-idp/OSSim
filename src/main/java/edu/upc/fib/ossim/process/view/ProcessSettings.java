package edu.upc.fib.ossim.process.view;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.SettingsTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Scheduler Settings panel. Multiple scheduler algorithm are available: 
 * <ul>
 * <li>FCFS</li>
 * <li>SJF</li>
 * <li>Priority</li>
 * <li>Round Robin</li>
 * </ul>
 * Additional control sets algorithm preemptiveness if possible and multiprogramming, besides Round Robin algorithm require 
 * quantum size selection.       
 * 
 * @author Alex Macia
 */
public class ProcessSettings extends SettingsTemplate { 
	private static final long serialVersionUID = 1L;

	private JCheckBox multiprogramming;
	private TitledBorder title;
	private JRadioButton shortScheduleFCFS;
	private JRadioButton shortScheduleSJF;
	private JRadioButton shortSchedulePrio;
	private JRadioButton shortScheduleRR;
	private JLabel lquantum;
	private JSpinner quantum;
	private JCheckBox preemptive;

	/**
	 * Constructs a ProcessSettings panel
	 * 
	 * @param presenter	event manager
	 * @param keyHelp	reference to panel's help 	
	 */
	public ProcessSettings(Presenter presenter, String keyHelp)  { 
		super(presenter, keyHelp);
	}

	/**
	 * Adds components to panel, algorithm selection button group, preemptive check control and 
	 * a quantum size selector used in Round Robin algorithm. FCFS is initially selected       
	 */
	public void initSpecific() {
		JPanel multi = new JPanel(new FlowLayout(FlowLayout.LEFT));
		multiprogramming = new JCheckBox(Translation.getInstance().getLabel("pr_26"));
		multiprogramming.setActionCommand("MUL");
		multiprogramming.addActionListener(presenter);
		multiprogramming.setSelected(true);
		multi.add(multiprogramming);
		pane.add(multi);
		
		JPanel algo = new JPanel();
		algo.setLayout(new BoxLayout(algo, BoxLayout.PAGE_AXIS));
		
		title = BorderFactory.createTitledBorder(Translation.getInstance().getLabel("pr_50"));
		algo.setBorder(title);

		shortScheduleFCFS = new JRadioButton(Translation.getInstance().getLabel("pr_51"));
		shortScheduleFCFS.setActionCommand("FCFS");
		shortScheduleFCFS.setSelected(true);
		shortScheduleFCFS.addActionListener(presenter);
		shortScheduleSJF = new JRadioButton(Translation.getInstance().getLabel("pr_52"));
		shortScheduleSJF.setActionCommand("SJF");
		shortScheduleSJF.addActionListener(presenter);
		shortSchedulePrio = new JRadioButton(Translation.getInstance().getLabel("pr_53"));
		shortSchedulePrio.setActionCommand("PRI");
		shortSchedulePrio.addActionListener(presenter);
		shortScheduleRR = new JRadioButton(Translation.getInstance().getLabel("pr_54"));
		shortScheduleRR.setActionCommand("RR");
		shortScheduleRR.addActionListener(presenter);

		addAlgorithm(shortScheduleFCFS);
		addAlgorithm(shortScheduleSJF);
		addAlgorithm(shortSchedulePrio);
		addAlgorithm(shortScheduleRR);

		SpinnerModel spmodel = new SpinnerNumberModel(1, //initial value
				1, //min
				10, //max
				1);                //step
		quantum = new JSpinner(spmodel);
		quantum.setName("quantum");
		quantum.addChangeListener(presenter);

		preemptive = new JCheckBox(Translation.getInstance().getLabel("pr_55"));
		preemptive.setEnabled(false);
		preemptive.setActionCommand("PRE");
		preemptive.addActionListener(presenter);
				
		JPanel fcfs = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fcfs.add(shortScheduleFCFS);
		algo.add(fcfs);
		
		JPanel sjf = new JPanel(new FlowLayout(FlowLayout.LEFT));
		sjf.add(shortScheduleSJF);
		algo.add(sjf);
		
		JPanel prio = new JPanel(new FlowLayout(FlowLayout.LEFT));
		prio.add(shortSchedulePrio);
		algo.add(prio);

		JPanel rr = new JPanel(new FlowLayout(FlowLayout.LEFT));
		rr.add(shortScheduleRR);
		rr.add(quantum);
		lquantum = new JLabel(Translation.getInstance().getLabel("pr_56"));
		rr.add(lquantum);
		lquantum.setVisible(false);
		quantum.setVisible(false);
		algo.add(rr);

		JPanel pre = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pre.add(preemptive);
		algo.add(pre);
		
		pane.add(algo);
	}
	
	/**
	 * Gets multiprogramming check state
	 * 
	 * @return	check state
	 */
	public boolean getMultiprogramming() {
		return multiprogramming.isSelected();
	}

	/**
	 * Sets multiprogramming check state
	 * 
	 * @param b	check state
	 */
	public void selectMultiprogramming(boolean b) {
		multiprogramming.setSelected(b);
	}
	
	/** 
	 * Enables multiprogramming check control
	 * 
	 * @param b	enable state
	 */
	public void enableMultiprogramming(boolean b) {
		multiprogramming.setEnabled(b);
	}
	
	/** 
	 * Sets preemptive check state 
	 * 
	 * @param b	check state
	 */
	public void selectPreemptive(boolean b) {
		preemptive.setSelected(b);
	}

	/** 
	 * Enables preemptive check control
	 * 
	 * @param b	enable state
	 */
	public void enablePreemptive(boolean b) {
		preemptive.setEnabled(b);
	}
	
	/**
	 * Gets preemptive check state
	 * 
	 * @return	check state
	 */
	public boolean getPreemptive() {
		return preemptive.isSelected();
	}

	/**
	 * Sets quantum size control visibility 
	 * 
	 * @param b control visibility 
	 */
	public void visibleQuantum(boolean b) {
		quantum.setVisible(b);
		lquantum.setVisible(b);
	}

	/**
	 * Gets quantum size
	 * 
	 * @return	quantum size
	 */
	public int getQuantumSize() {
		return (Integer) quantum.getValue();
	}

	/**
	 * Sets quantum size
	 * 
	 * @param size	quantum size
	 */
	public void setQuantumSize(int size) {
		quantum.setValue(size);
	}

	/**
	 * Translates labels and help reference to current session language and then resizes panel 
	 */
	public void updateLabels() {
		this.setTitle(Translation.getInstance().getLabel("all_10"));
		title.setTitle(Translation.getInstance().getLabel("pr_50"));
		multiprogramming.setText(Translation.getInstance().getLabel("pr_26"));
		shortScheduleSJF.setText(Translation.getInstance().getLabel("pr_52"));
		shortScheduleFCFS.setText(Translation.getInstance().getLabel("pr_51"));
		shortSchedulePrio.setText(Translation.getInstance().getLabel("pr_53"));
		shortScheduleRR.setText(Translation.getInstance().getLabel("pr_54"));
		preemptive.setText(Translation.getInstance().getLabel("pr_55"));
		help = presenter.createHelp("pr_01");
		lquantum.setText(Translation.getInstance().getLabel("pr_56"));
		this.pack();
	}
} 
