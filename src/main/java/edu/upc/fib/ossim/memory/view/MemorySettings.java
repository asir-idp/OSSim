package edu.upc.fib.ossim.memory.view;

import java.awt.FlowLayout;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
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
 * Memory management settings panel. Allows setting main memory and operating system size, besides 
 * multiple memory management algorithm are available: 
 * <ul>
 * <li>Fixed-size partitions (Contiguous)</li>
 * <li>Variable size partitions (contiguous)</li>
 * <li>Pagination (non contiguous)</li>
 * <li>Segmentation (non contiguous)</li>
 * </ul>
 * Contiguous memory management algorithms may select allocation policy among: first fit, best fit and worst fit.   
 * Fixed-size partitions algorithm includes a button for partitions creation, pagination algorithm includes a page size selector.  
 * 
 * @author Alex Macia
 */
public class MemorySettings extends SettingsTemplate { 
	private static final long serialVersionUID = 1L;
	public static final int MIN_MEMSIZE = 64;
	public static final int MAX_MEMSIZE = 256;
	public static final Integer[] SO_VALUES = {1, 2, 4};
	private static final Integer[] PAGE_SIZE = {1, 2, 4};

	private TitledBorder titleG;
	private JSpinner memSize;	// Memory Size
	private JLabel lmemSize;	
	private JComboBox soSize;	// SO Size
	private JLabel lsoSize;	
	private TitledBorder titleM;
	private TitledBorder titleP;
	private JRadioButton fixed;
	private JRadioButton variable;
	private JRadioButton pagination;
	private JComboBox pageSize;
	private JLabel lpage;
	private JRadioButton segmentation;

	private JRadioButton firstFit;
	private JRadioButton bestFit;
	private JRadioButton worstFit;
	private ButtonGroup bgpolicy;
	
	/**
	 * Constructs a MemorySettings panel
	 * 
	 * @param presenter	event manager
	 * @param keyHelp	reference to panel's help 	
	 */
	public MemorySettings(Presenter presenter, String keyHelp)  { 
		super(presenter, keyHelp);
	}
	

	/**
	 * Adds components to panel, memory size combo, operating system size combo, algorithm selection button group, 
	 * partitions creation button (Fixed-size), compaction button (Variable size and segmentation),
	 * page size selector (pagination) and allocation button group (contiguous memory management algorithms)
	 */
	public void initSpecific() {
		lmemSize = new JLabel(Translation.getInstance().getLabel("me_19"));
		SpinnerModel spmodel = new SpinnerNumberModel(MIN_MEMSIZE, MIN_MEMSIZE, MAX_MEMSIZE, 4); // 4kb Multiple
		memSize = new JSpinner(spmodel);
		memSize.setName("memSize");
		memSize.addChangeListener(presenter);

		lsoSize = new JLabel(Translation.getInstance().getLabel("me_18"));
		soSize = new JComboBox(SO_VALUES);
		soSize.setSelectedIndex(SO_VALUES.length-1);
		soSize.setActionCommand("SOSIZE");
		soSize.addActionListener(presenter);

		fixed = new JRadioButton(Translation.getInstance().getLabel("me_51"));
		fixed.setSelected(true);
		fixed.setActionCommand("FIX");
		fixed.addActionListener(presenter);

		variable = new JRadioButton(Translation.getInstance().getLabel("me_52"));
		variable.setActionCommand("VAR");
		variable.addActionListener(presenter);
		
		pagination = new JRadioButton(Translation.getInstance().getLabel("me_58"));
		pagination.setActionCommand("PAG");
		pagination.addActionListener(presenter);
		
		pageSize = new JComboBox(PAGE_SIZE);
		pageSize.setActionCommand("PSIZE");
		pageSize.addActionListener(presenter);
		pageSize.setVisible(false);

		segmentation = new JRadioButton(Translation.getInstance().getLabel("me_59"));
		segmentation.setActionCommand("SEG");
		segmentation.addActionListener(presenter);

		addAlgorithm(fixed);
		addAlgorithm(variable);
		addAlgorithm(pagination);
		addAlgorithm(segmentation);

		firstFit = new JRadioButton(Translation.getInstance().getLabel("me_55"));
		firstFit.setActionCommand("FF");
		firstFit.addActionListener(presenter);
		firstFit.setSelected(true);
		bestFit = new JRadioButton(Translation.getInstance().getLabel("me_56"));
		bestFit.setActionCommand("BF");
		bestFit.addActionListener(presenter);
		worstFit = new JRadioButton(Translation.getInstance().getLabel("me_57"));
		worstFit.setActionCommand("WF");
		worstFit.addActionListener(presenter);

		bgpolicy = new ButtonGroup();
		bgpolicy.add(firstFit);
		bgpolicy.add(bestFit);
		bgpolicy.add(worstFit);
		
		JPanel memGen = new JPanel();
		memGen.setLayout(new BoxLayout(memGen, BoxLayout.PAGE_AXIS));
		titleG = BorderFactory.createTitledBorder(Translation.getInstance().getLabel("me_17"));
		memGen.setBorder(titleG);

		JPanel pgenmem = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pgenmem.add(lmemSize);
		pgenmem.add(memSize);
		memGen.add(pgenmem);
		JPanel pgenso = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pgenso.add(lsoSize);
		pgenso.add(soSize);
		memGen.add(pgenso);
	
		JPanel memMng = new JPanel();
		memMng.setLayout(new BoxLayout(memMng, BoxLayout.PAGE_AXIS));
		titleM = BorderFactory.createTitledBorder(Translation.getInstance().getLabel("me_20"));
		memMng.setBorder(titleM);

		JPanel pfix = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pfix.add(fixed);
		memMng.add(pfix);
		
		JPanel pvar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pvar.add(variable);
		memMng.add(pvar);
		
		JPanel ppag = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ppag.add(pagination);
		lpage = new JLabel(Translation.getInstance().getLabel("me_62"));
		lpage.setVisible(false);
		ppag.add(lpage);
		ppag.add(pageSize);
		memMng.add(ppag);

		JPanel pseg = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pseg.add(segmentation);
		memMng.add(pseg);
		
		JPanel assPolicy = new JPanel();
		assPolicy.setLayout(new BoxLayout(assPolicy, BoxLayout.LINE_AXIS));
		titleP = BorderFactory.createTitledBorder(Translation.getInstance().getLabel("me_21"));
		assPolicy.setBorder(titleP);
		
		JPanel ass = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ass.add(firstFit);
		ass.add(bestFit);
		ass.add(worstFit);
		assPolicy.add(ass);
		
		pane.add(memGen);
		
		pane.add(memMng);
		
		pane.add(assPolicy);
	}
	
	/**
	 * Translates labels and help reference to current session language and then resizes panel 
	 */
	public void updateLabels() {
		this.setTitle(Translation.getInstance().getLabel("all_10"));
		titleG.setTitle(Translation.getInstance().getLabel("me_17"));
		titleM.setTitle(Translation.getInstance().getLabel("me_20"));
		titleP.setTitle(Translation.getInstance().getLabel("me_21"));
		lmemSize.setText(Translation.getInstance().getLabel("me_19"));
		lsoSize.setText(Translation.getInstance().getLabel("me_18"));
		fixed.setText(Translation.getInstance().getLabel("me_51"));
		variable.setText(Translation.getInstance().getLabel("me_52"));
		pagination.setText(Translation.getInstance().getLabel("me_58"));
		segmentation.setText(Translation.getInstance().getLabel("me_59"));
		lpage.setText(Translation.getInstance().getLabel("me_62"));
		firstFit.setText(Translation.getInstance().getLabel("me_55"));
		bestFit.setText(Translation.getInstance().getLabel("me_56"));
		worstFit.setText(Translation.getInstance().getLabel("me_57"));
		help = presenter.createHelp("me_01");
		this.pack();
	}

	/**
	 * Returns memory size value
	 * 
	 * @return	memory size value
	 */
	public int getMemSize() {
		return (Integer) memSize.getValue();
	}
	
	/**
	 * Sets  memory size value
	 * 
	 * @param size	memory size
	 */
	public void setMemSize(int size) {
		memSize.setValue(new Integer(size));
	}

	/**
	 * Returns operating system size 
	 * 
	 * @return operating system size
	 */
	public int getSOSize() {
		return (Integer) soSize.getSelectedItem();
	}
	
	/**
	 * Sets operating system size 
	 * 
	 * @param size	operating system size
	 */
	public void setSOSize(int size) {
		soSize.removeActionListener(presenter);
		soSize.setSelectedItem(new Integer(size));
		soSize.addActionListener(presenter);
	}

	/**
	 * Sets page size control visibility 
	 * 
	 * @param b control visibility 
	 */
	public void paginationSetVisible(boolean b) {
		lpage.setVisible(b);
		pageSize.setVisible(b);
	}

	/**
	 * Gets allocation policy value 
	 * 
	 * @return allocation policy value
	 */
	public String getPolicy() {
		return bgpolicy.getSelection().getActionCommand();
	}

	/**
	 * Selects a policy button (whose action command is parameter actionCommand) within policy button group 
	 * 
	 * @param actionCommand	button action command to select
	 */
	public void selectPolicy(String actionCommand) {
		Enumeration<AbstractButton> policies = bgpolicy.getElements();
		while (policies.hasMoreElements()) {
			AbstractButton policy = policies.nextElement();
			if (actionCommand.equals(policy.getActionCommand())) policy.setSelected(true);
		}
	}
	
	/**
	 * Returns page size value
	 * 
	 * @return page size value
	 */
	public int getPageSize() {
		return (Integer) pageSize.getSelectedItem();
	}

	/**
	 * Sets page size value
	 * 
	 * @param pagesize page size 	
	 */
	public void setPageSize(int pagesize) {
		pageSize.removeActionListener(presenter);
		pageSize.setSelectedItem(new Integer(pagesize));
		pageSize.addActionListener(presenter);
	}
	
	/**
	 * Selects an algorithm 
	 * 
	 * @param actionCommand	algorithm's action command value to select 
	 */
	public void setAlgorithm(String actionCommand) {
		if (fixed.getActionCommand().equals(actionCommand)) fixed.setSelected(true);
		if (variable.getActionCommand().equals(actionCommand)) variable.setSelected(true);
		if (pagination.getActionCommand().equals(actionCommand)) pagination.setSelected(true);
		if (segmentation.getActionCommand().equals(actionCommand)) segmentation.setSelected(true);
	}

	/**
	 * Sets allocation policy controls availability 
	 * 
	 * @param b allocation policy controls availability
	 */
	public void policyEnable(boolean b) {
		firstFit.setEnabled(b);
		bestFit.setEnabled(b);
		worstFit.setEnabled(b);
	}
} 
