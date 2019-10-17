package edu.upc.fib.ossim.disk.view;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;

import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.SettingsTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Disk scheduling settings panel. Allows setting initial disk head sector and selects scheduling
 * algorithm among: 
 * <ul>
 * <li>first in first out (fifo)</li>
 * <li>last in first out (lifo)</li>
 * <li>shortest (seek) time first</li>  
 * <li>Scan</li>
 * <li>Circular scan (C-Scan)</li>
 * <li>Look</li>
 * <li>Circular look (C-Look)</li>
 * </ul> 
 * @author Alex Macia
 */
public class DiskSettings extends SettingsTemplate { 
	private static final long serialVersionUID = 1L;

	private Border title;
	private JLabel linitHead;
	private JRadioButton fifo;
	private JRadioButton lifo; 
	private JRadioButton stf;
	private JRadioButton scan;
	private JRadioButton cscan;
	private JRadioButton loop;
	private JRadioButton cloop;
	private JSpinner initHead;
	
	/**
	 * Constructs a DiskSettings panel
	 * 
	 * @param presenter	event manager
	 * @param keyHelp	reference to panel's help 	
	 */
	public DiskSettings(Presenter presenter, String keyHelp)  { 
		super(presenter, keyHelp);
	}
	
	/**
	 * Adds components to panel, head position spinner and algorithm selection button group: 
	 * fifo, lifo, stf, can, c-scan, look and c-look.
	 */
	public void initSpecific() {
		fifo = new JRadioButton(Translation.getInstance().getLabel("dk_51"));
		fifo.setSelected(true);
		fifo.setActionCommand("FIFO");
		fifo.addActionListener(presenter);
		lifo = new JRadioButton(Translation.getInstance().getLabel("dk_52"));
		lifo.setActionCommand("LIFO");
		lifo.addActionListener(presenter);
		stf = new JRadioButton(Translation.getInstance().getLabel("dk_53"));
		stf.setActionCommand("STF");
		stf.addActionListener(presenter);
		scan = new JRadioButton(Translation.getInstance().getLabel("dk_54"));
		scan.setActionCommand("SCAN");
		scan.addActionListener(presenter);
		loop = new JRadioButton(Translation.getInstance().getLabel("dk_55"));
		loop.setActionCommand("LOOP");
		loop.addActionListener(presenter);
		cloop = new JRadioButton(Translation.getInstance().getLabel("dk_57"));
		cloop.setActionCommand("CLOOP");
		cloop.addActionListener(presenter);
		cscan = new JRadioButton(Translation.getInstance().getLabel("dk_56"));
		cscan.setActionCommand("CSCAN");
		cscan.addActionListener(presenter);
		
		addAlgorithm(fifo);
		addAlgorithm(lifo);
		addAlgorithm(stf);
		addAlgorithm(scan);
		addAlgorithm(loop);
		addAlgorithm(cloop);
		addAlgorithm(cscan);
		
		SpinnerModel spmodel = new SpinnerNumberModel(0, //initial value
				0, //min
				((DiskPresenter) presenter).getNblocks() -1, //max
				1);                //step
		initHead = new JSpinner(spmodel);
		initHead.setName("initHead");
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) initHead.getEditor();
		editor.getTextField().addFocusListener(presenter);
		initHead.addChangeListener(presenter);
		
		linitHead = new JLabel(Translation.getInstance().getLabel("dk_22"));
		
		JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT));
		head.add(initHead);
		head.add(linitHead);
		pane.add(head);
		
		JPanel known = new JPanel();
		known.setLayout(new BoxLayout(known, BoxLayout.PAGE_AXIS));
		known.setAlignmentX(CENTER_ALIGNMENT);  // Left alignment ??

		title = BorderFactory.createTitledBorder("");
		known.setBorder(title);

		known.add(fifo);
		known.add(lifo);
		known.add(stf);
		known.add(scan);
		known.add(cscan);
		known.add(loop);
		known.add(cloop);
		
		pane.add(known);
	}
	
	/**
	 * Translates labels and help reference to current session language and then resizes panel 
	 */
	public void updateLabels() {
		this.setTitle(Translation.getInstance().getLabel("all_10"));
		linitHead.setText(Translation.getInstance().getLabel("dk_22"));
		fifo.setText(Translation.getInstance().getLabel("dk_51"));
		lifo.setText(Translation.getInstance().getLabel("dk_52"));
		stf.setText(Translation.getInstance().getLabel("dk_53"));
		scan.setText(Translation.getInstance().getLabel("dk_54"));
		loop.setText(Translation.getInstance().getLabel("dk_55"));
		cscan.setText(Translation.getInstance().getLabel("dk_56"));
		cloop.setText(Translation.getInstance().getLabel("dk_57"));
		help = presenter.createHelp("dk_01");
		this.pack();
	}
	
	/**
	 * Returns initial head's position 
	 * 
	 * @return	initial head's position
	 */
	public int getInitHead() {
		return (Integer) initHead.getValue();
	}
	
	/**
	 * Sets initial head's position 
	 * 
	 * @param position	initial head's position
	 */
	public void setInitHead(int position) {
		initHead.setValue(position);
	}

} 
