package edu.upc.fib.ossim.process.view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableColumn;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.AppTableModel;
import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.ColorRenderer;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Process creation and update form. Process fields are: process identifier (pid), 
 * name, priority, submission time, duration, color and CPU or I/O process bursts table.<br/>
 * When duration -1 (infinite), periodic CPU I/O cycle must be defined.<br/>
 * A process name is always required and duration should not be 0,
 * when updating a process, form fields can be initialized with process values   
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 */
public class FormProcess extends FormTemplate{
	private static final long serialVersionUID = 1L;
	private static final int TABLE_WIDTH = 50;
	private static final int TABLE_HEIGHT = 100;

	private String spid;
	private JTextField name;
	private JSpinner prio;	// Priority
	private JSpinner submission;	// submission time
	private JSpinner bursts;
	private JCheckBox periodicCycle;
	
	private AppTableModel tablemodel;
	private JTable burstsTable;
	
	/**
	 * Constructs a form process
	 * 
	 * @param presenter	event manager
	 * @param title		form title
	 * @param help		help icon
	 * @param values	creating a new process: time and max pid, updating an existing process: time, pid, name, priority, initial time, duration and color    
	 */
	public FormProcess(Presenter presenter, String title, JLabel help, Vector<Object> values) { 
		super(presenter, title, help, values);
	}

	/**
	 * Creates and initialize form fields 
 	 * Fields and its labels are laid out as a compact grid.
	 * 
	 * @see Functions#makeCompactGrid(java.awt.Container, int, int, int, int, int, int)
	 */
	@SuppressWarnings("unchecked")
	public void init(Vector<Object> values) {
		// Actual time (required), pìd (required), name, priority, init time, duration, color  
		if (values.size() > 2) spid = values.get(1).toString();
		else spid = values.get(1).toString();
		grid.add(new JLabel(Translation.getInstance().getLabel("pr_30") + " " + spid));
		grid.add(new JLabel(""));
		
		grid.add(new JLabel(Translation.getInstance().getLabel("pr_31")));
		if (values.size() > 2) name = new JTextField(values.get(2).toString(), 10);
		else name = new JTextField(10);
		name.addFocusListener(presenter);
        grid.add(name);
		
		grid.add(new JLabel(Translation.getInstance().getLabel("pr_32")));
		SpinnerModel spmodelPrio;
		if (values.size() > 2) spmodelPrio = new SpinnerNumberModel(new Integer(values.get(3).toString()).intValue(), 1, 10, 1);
		else spmodelPrio = new SpinnerNumberModel(1, 1, 10, 1);
        prio = new JSpinner(spmodelPrio);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) prio.getEditor();
		editor.getTextField().addFocusListener(presenter);
        grid.add(prio);
        
		grid.add(new JLabel(Translation.getInstance().getLabel("pr_37")));
		SpinnerModel spmodelInit;
		if (values.size() > 2) spmodelInit = new SpinnerNumberModel(new Integer(values.get(4).toString()).intValue(), new Integer(values.get(0).toString()).intValue(), 100, 1);
		else spmodelInit = new SpinnerNumberModel(new Integer(values.get(0).toString()).intValue(), new Integer(values.get(0).toString()).intValue(), 100, 1);
		submission = new JSpinner(spmodelInit);
		editor = (JSpinner.DefaultEditor) submission.getEditor();
		editor.getTextField().addFocusListener(presenter);
		grid.add(submission);
		
		if (values.size() > 2) initColor((Color) values.get(7));
        else initColor(RandColor());

		// Bursts. CPU or IO. 
		Vector<Integer> burstsCycle;
		boolean periodic = false;
		if (values.size() > 2) {
			periodic = (Boolean) values.get(5);
			burstsCycle = (Vector<Integer>) values.get(6);
		} else {
			burstsCycle = new Vector<Integer>();
			burstsCycle.add(0); // Initially, 1 CPU burst 
		}
		
		grid.add(new JLabel(Translation.getInstance().getLabel("pr_74")));
		periodicCycle = new JCheckBox();
		if (periodic) periodicCycle.setSelected(true);
		grid.add(periodicCycle);
		
        Functions.getInstance().makeCompactGrid(grid, 6, 2, 6, 6, 6, 6);
		pn.add(grid);
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> row = new Vector<Object>(); 
		
		for (int i = 0; i < burstsCycle.size(); i++) {
			row = new Vector<Object>();
			row.add(new ColorCell(new Integer(i).toString(), Color.WHITE));
			
			if (burstsCycle.get(i) == 0) { // CPU burst 
				row.add(new ColorCell("", Color.LIGHT_GRAY));
				row.add(new ColorCell("", Color.WHITE));
			} else { // I/O burst
				row.add(new ColorCell("", Color.WHITE));
				row.add(new ColorCell("", Color.LIGHT_GRAY));
			}
			data.add(row);
		}
		
		Vector<Object> header = presenter.getFormTableHeader();
		
		tablemodel = new AppTableModel(data, header, false);
		burstsTable = new JTable(tablemodel);
		burstsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		burstsTable.setCellSelectionEnabled(true);
		burstsTable.getSelectionModel().addListSelectionListener(presenter);
		burstsTable.getColumnModel().getSelectionModel().addListSelectionListener(presenter);

		for (int i = 0; i< burstsTable.getColumnCount(); i++) {
			TableColumn columna = burstsTable.getColumnModel().getColumn(i);
			columna.setCellRenderer(new ColorRenderer());
		}

		JScrollPane scroll = new JScrollPane(burstsTable);
		burstsTable.setFillsViewportHeight(true);
		scroll.setLayout(new ScrollPaneLayout());
		scroll.setPreferredSize(new Dimension(TABLE_WIDTH,TABLE_HEIGHT));
		
		pn.add(scroll);
		
		JPanel bp = new JPanel(new FlowLayout()); 
		bp.add(new JLabel(Translation.getInstance().getLabel("pr_73")));
		SpinnerModel spmodelBursts = new SpinnerNumberModel(burstsCycle.size(), 1, 10, 1);
		bursts = new JSpinner(spmodelBursts);
		editor = (JSpinner.DefaultEditor) bursts.getEditor();
		editor.getTextField().addFocusListener(presenter);
		bursts.setName("bursts");
		bursts.addChangeListener(presenter);	// Update bursts table	
		bp.add(bursts);
		pn.add(bp);

		addOKButton();
	}

	/**
	 * Gets Bursts Table
	 * 
	 * @return Bursts Table
	 */
	public JTable getTable() {
		return burstsTable;
	}
	
	/**
	 * Is periodic cycle check selected
	 * 
	 * @return periodic cycle check is selected
	 */
	public boolean isPeriodic() {
		return this.periodicCycle.isSelected();
	}
	
	/**
	 * Updates bursts table rows number    
	 * 
	 * @param rows	process bursts table new row count
	 */
	public void updateBurstsTable(int rows) {
		bursts.setValue(rows);
		if (rows != tablemodel.getRowCount()) {
			if (rows > tablemodel.getRowCount()) {
				Vector<Object> row;
				for (int i = tablemodel.getRowCount(); i < rows; i++) {
					row = new Vector<Object>();
					row.add(new ColorCell(new Integer(i).toString(), Color.WHITE));
					row.add(new ColorCell("", Color.LIGHT_GRAY));
					row.add(new ColorCell("", Color.WHITE));
					tablemodel.addRow(row);
				}
			} else {
				for (int i = tablemodel.getRowCount() - 1; i >= rows; i--) {
					tablemodel.removeRow(i);	
				}
			}
		}
	}
	
	
	/**
	 * Validates process name field
	 * 
	 * @return	validation result 
	 */
	public boolean validateFields() {
		if ("".equals(name.getText()) || name.getText() == null) {
			JOptionPane.showMessageDialog(this.getParent(),Translation.getInstance().getError("all_01"),"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		} 
		
		ColorCell firstBurstIO = (ColorCell) burstsTable.getValueAt(0, 2);
		ColorCell lastBurstIO = (ColorCell) burstsTable.getValueAt(burstsTable.getRowCount()-1, 2);
		
		if (Color.LIGHT_GRAY.equals(firstBurstIO.getColor())) {
			// Strt with cpu burst, not io
			JOptionPane.showMessageDialog(this.getParent(),Translation.getInstance().getError("pr_02"),"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if (!periodicCycle.isSelected() && Color.LIGHT_GRAY.equals(lastBurstIO.getColor())) {
			// Non periodic ends with cpu burst, not io
			JOptionPane.showMessageDialog(this.getParent(),Translation.getInstance().getError("pr_03"),"Error",JOptionPane.ERROR_MESSAGE);
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
		data.add(spid);
		data.add(name.getText());
		data.add(prio.getValue());
		data.add(submission.getValue());
		data.add(periodicCycle.isSelected());
		data.add(color);
		Vector<Integer> bursts= new Vector<Integer>();
		for(int r = 0; r < tablemodel.getRowCount(); r++) {
			ColorCell cell = (ColorCell) tablemodel.getValueAt(r, 1); 
			if (cell.getColor().equals(Color.LIGHT_GRAY)) bursts.add(0); // CPU burst
			else bursts.add(1); // I/O burst
		}
		data.add(bursts);
		
		return data;
	}
} 
