package edu.upc.fib.ossim.memory.view;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.table.DefaultTableColumnModel;

import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.AppTableModel;


/**
 * Process creation and update form in pagination memory management (non contiguous). 
 * A table paints block specific information representing process pages: page number, and initial load state into memory.<br/>
 * When updating a process, its pages are initialized with process values   
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 * @see FormProcess
 */
public class FormProcessPag extends FormProcess {
	private static final long serialVersionUID = 1L;
	private static final int TABLE_WIDTH = 100;
	private static final int TABLE_HEIGHT = 100;
	
	private JLabel lblocks;
	private AppTableModel tablemodel;
	private String blockTitle;
	
	/**
	 * Constructs a form process (pagination)  
	 * 
	 * @param presenter	event manager
	 * @param title		form title
	 * @param help		help icon
	 * @param values	creating a new process: pid, updating an existing process: pid, name, size, duration and color    
	 * @param blockTitle pages information title	
	 * 
	 */
	public FormProcessPag(Presenter presenter, String title, JLabel help, Vector<Object> values, String blockTitle) {
		super(presenter, title, help, values);
		this.blockTitle = blockTitle;
	}
	
	/**
	 * Creates and initialize pages table
	 * 
	 * @param values	value with index 5 contains pages information data <code>Vector<Vector<Object>></code>
	 */
	@SuppressWarnings("unchecked")
	public void initBlocks(Vector<Object> values) {
		size.addChangeListener(presenter);	// Update page table	
		
		lblocks = new JLabel(blockTitle);
        lblocks.setAlignmentX(JLabel.LEFT);
        pn.add(lblocks);

        Vector<Object> header = ((MemoryPresenter) presenter).getFormTableHeader();
        Vector<Vector<Object>> data = null;

        if (values.size() > 1) data = (Vector<Vector<Object>>) values.get(5);
        else data = ((MemoryPresenter) presenter).getFormTableInitData();

        tablemodel = new AppTableModel(data, header, true);
        JTable table = new JTable(tablemodel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Hide column size
		DefaultTableColumnModel modelColumna = (DefaultTableColumnModel) table.getColumnModel();
		modelColumna.removeColumn(table.getColumnModel().getColumn(1));
        
        JScrollPane scroll = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scroll.setLayout(new ScrollPaneLayout());
        scroll.setPreferredSize(new Dimension(TABLE_WIDTH,TABLE_HEIGHT));

        pn.add(scroll);
	}

	/**
	 * Updates pages table rows number.    
	 * 
	 * @param pages	total pages
	 */
	public void updatePageTable(int pages) {
		if (pages != tablemodel.getRowCount()) {
			if (pages > tablemodel.getRowCount()) {
				Vector<Object> page;
				for (int i = tablemodel.getRowCount(); i < pages; i++) {
					page = new Vector<Object>();
					page.add(i);
					page.add("");
					page.add(true);
					tablemodel.addRow(page);		
				}
			} else {
				for (int i = tablemodel.getRowCount() - 1; i >= pages; i--) {
					tablemodel.removeRow(i);	
				}
			}
		}
	}
	
	/**
	 * No concrete block validation is needed 
	 * 
	 * @return true
	 */
	public boolean validateFieldsBlock() {
		return true;
	}

	/**
	 * Returns a table containing pages data
	 *
	 * @return form pages data
	 */	
	@SuppressWarnings("rawtypes")
	public Vector<Vector> getComponentsData() {
		// Program blocks data.
		return tablemodel.getDataVector();
	}
}
