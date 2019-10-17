package edu.upc.fib.ossim.memory.view;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;

import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.AppTableModel;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Process creation and update form in segmentation memory management (non contiguous). 
 * A table paints block specific information representing process segments: segment type, segment size and initial load state into memory.<br/>
 * When updating a process, its segments are initialized with process values   
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 * @see FormProcess
 */
public class FormProcessSeg extends FormProcess {
	private static final long serialVersionUID = 1L;
	private static final int TABLE_WIDTH = 100;
	private static final int TABLE_HEIGHT = 100;
	
	private JLabel lblocks;
	private AppTableModel tablemodel;
	private String blockTitle;
	
	/**
	 * Constructs a form process (segmentation)  
	 * 
	 * @param presenter	event manager
	 * @param title		form title
	 * @param help		help icon
	 * @param values	creating a new process: pid, updating an existing process: pid, name, size, duration and color    
	 * @param blockTitle segment information title	
	 * 
	 */
	public FormProcessSeg(Presenter presenter, String title, JLabel help, Vector<Object> values, String blockTitle) {
		super(presenter, title, help, values);
		this.blockTitle = blockTitle;
	}
	
	/**
	 * Creates and initialize segments table
	 * 
	 * @param values	value with index 5 contains segments information data <code>Vector<Vector<Object>></code>
	 */
	@SuppressWarnings("unchecked")
	public void initBlocks(Vector<Object> values) {
        lblocks = new JLabel(blockTitle);
        lblocks.setAlignmentX(JLabel.LEFT);
        pn.add(lblocks);
        if (values.size() <= 1) size.setValue(3);
        
        Vector<Object> header = ((MemoryPresenter) presenter).getFormTableHeader();
        Vector<Vector<Object>> data = null;

        if (values.size() > 1) data = (Vector<Vector<Object>>) values.get(5);
        else data = ((MemoryPresenter) presenter).getFormTableInitData();

        tablemodel = new AppTableModel(data, header, true);
        JTable table = new JTable(tablemodel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scroll.setLayout(new ScrollPaneLayout());
        scroll.setPreferredSize(new Dimension(TABLE_WIDTH,TABLE_HEIGHT));

        pn.add(scroll);
	}

	/**
	 * Validates that segments size sum is equal to process size    
	 * 
	 * @return validation result 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean validateFieldsBlock() {
		// Segments size numeric and positive
		// Total size equals segment size sum
		int totalSize = 0;
		int segSize = 0;
		Vector<Vector> data = tablemodel.getDataVector();
		for (int i = 0; i< data.size(); i++) {
			Vector<Object> segment = data.get(i);
			segSize = (Integer) segment.get(1);
			if (segSize < 1) {
				JOptionPane.showMessageDialog(this.getParent(),Translation.getInstance().getError("me_11"),"Error",JOptionPane.ERROR_MESSAGE);
				return false;
			} 
			totalSize += segSize;
		}

		if (totalSize != (Integer) size.getValue()) {
			JOptionPane.showMessageDialog(this.getParent(),Translation.getInstance().getError("me_12"),"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}

	/**
	 * Returns a table containing segments data
	 *
	 * @return form pages data
	 */	
	@SuppressWarnings("rawtypes")
	public Vector<Vector> getComponentsData() {
		// Program blocks data.
		return tablemodel.getDataVector();
	}
}
