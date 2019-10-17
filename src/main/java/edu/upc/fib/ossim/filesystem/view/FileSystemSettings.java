package edu.upc.fib.ossim.filesystem.view;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.SettingsTemplate;
import edu.upc.fib.ossim.utils.Translation;

/**
 * File System settings panel. Allows setting memory and block size controls, besides 
 * two file System management algorithm are available: 
 * <ul>
 * <li>linked allocation with file-allocation table (FAT)</li>
 * <li>indexed allocation with triple indirection (UNIX)</li>
 * <ul> 
 * @author Alex Macia
 */
public class FileSystemSettings extends SettingsTemplate { 
	private static final long serialVersionUID = 1L;
	private static final Integer[] BLOCK_VALUES = {1, 2, 4};
	private static final Integer[] MEM_VALUES = {4, 6, 8};

	private TitledBorder titleD;
	private JComboBox blockSize;
	private JComboBox devSize;
	private JLabel lblock;
	private JLabel lmem;

	private TitledBorder titleA;
	private JRadioButton linked;
	private JRadioButton indexed;
	
	/**
	 * Constructs a FileSystemSettings panel
	 * 
	 * @param presenter	event manager
	 * @param keyHelp	reference to panel's help 	
	 */
	public FileSystemSettings(Presenter presenter, String keyHelp)  { 
		super(presenter, keyHelp);
	}
	
	/**
	 * Adds components to panel, memory size combo, block size combo and algorithm selection button group: 
	 * linked allocation (FAT) and indexed allocation (UNIX).
	 */
	public void initSpecific() {
		blockSize = new JComboBox(BLOCK_VALUES);
		blockSize.setSelectedIndex(0);
		blockSize.setActionCommand("SIZE");
		blockSize.addActionListener(presenter);

		devSize = new JComboBox(MEM_VALUES);
		devSize.setSelectedIndex(0);
		devSize.setActionCommand("SIZE");
		devSize.addActionListener(presenter);
		
		linked = new JRadioButton(Translation.getInstance().getLabel("fs_53"));
		linked.setActionCommand("DOS");
		linked.addActionListener(presenter);
		linked.setSelected(true);
		indexed = new JRadioButton(Translation.getInstance().getLabel("fs_54"));
		indexed.setActionCommand("LINUX");
		indexed.addActionListener(presenter);

		addAlgorithm(linked);
		addAlgorithm(indexed);

		JPanel devSet = new JPanel();
		devSet.setLayout(new BoxLayout(devSet, BoxLayout.PAGE_AXIS));
		titleD = BorderFactory.createTitledBorder(Translation.getInstance().getLabel("fs_51"));
		devSet.setBorder(titleD);
	
		JPanel pblock = new JPanel(new FlowLayout(FlowLayout.LEFT));
		lblock = new JLabel(Translation.getInstance().getLabel("fs_50"));
		pblock.add(lblock);
		pblock.add(blockSize);
		devSet.add(pblock);

		JPanel pmem = new JPanel(new FlowLayout(FlowLayout.LEFT));
		lmem = new JLabel(Translation.getInstance().getLabel("fs_49"));
		pmem.add(lmem);
		pmem.add(devSize);
		devSet.add(pmem);

		JPanel allocationMethod = new JPanel();
		allocationMethod.setLayout(new BoxLayout(allocationMethod, BoxLayout.PAGE_AXIS));
		
		titleA = BorderFactory.createTitledBorder(Translation.getInstance().getLabel("fs_52"));
		allocationMethod.setBorder(titleA);

		JPanel link = new JPanel(new FlowLayout(FlowLayout.LEFT));
		link.add(linked);
		allocationMethod.add(link);
		JPanel index = new JPanel(new FlowLayout(FlowLayout.LEFT));
		index.add(indexed);
		allocationMethod.add(index);
		
		pane.add(devSet);
		
		pane.add(allocationMethod);
	}
	
	/**
	 * Translates labels and help reference to current session language and then resizes panel 
	 */
	public void updateLabels() {
		this.setTitle(Translation.getInstance().getLabel("all_10"));
		titleD.setTitle(Translation.getInstance().getLabel("fs_51"));
		lblock.setText(Translation.getInstance().getLabel("fs_50"));
		lmem.setText(Translation.getInstance().getLabel("fs_49"));
		titleA.setTitle(Translation.getInstance().getLabel("fs_52"));
		linked.setText(Translation.getInstance().getLabel("fs_53"));
		indexed.setText(Translation.getInstance().getLabel("fs_54"));
		help = presenter.createHelp("fs_01");
		this.pack();
	}

	/**
	 * Returns block size value
	 * 
	 * @return	block size value
	 */
	public int getBlockSize() {
		return (Integer) blockSize.getSelectedItem();
	}

	/**
	 * Sets  block size value
	 * 
	 * @param size	block size
	 */
	public void setBlockSize(int size) {
		blockSize.removeActionListener(presenter); // Avoid triggering action event
		blockSize.setSelectedItem(new Integer(size));
		blockSize.addActionListener(presenter);
	}

	/**
	 * Returns memory size value
	 * 
	 * @return	memory size value
	 */
	public int getDevSize() {
		return (Integer) devSize.getSelectedItem() * 1024;
	}

	/**
	 * Sets  memory size value
	 * 
	 * @param size	memory size
	 */
	public void setDevSize(int size) {
		devSize.removeActionListener(presenter); // Avoid triggering action event
		devSize.setSelectedItem(new Integer(size/1024));
		devSize.addActionListener(presenter);
	}

	/**
	 * Selects an algorithm 
	 * 
	 * @param actionCommand	algorithm's action command value to select 
	 */
	public void setAlgorithm(String actionCommand) {
		if (linked.getActionCommand().equals(actionCommand)) linked.setSelected(true);
		if (indexed.getActionCommand().equals(actionCommand)) indexed.setSelected(true);
	}
} 
