package edu.upc.fib.ossim.filesystem.view;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;

import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.AppTableModel;
import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.ColorRenderer;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Paints a folder content, therefore file system objects: files, folders and links, 
 * into a table, a pop up menu allows accessing objects information under that folder. 
 * 
 * @author Alex Macia
 * 
 * @see FilesTreePainter
 */
public class FolderPainter extends PainterTemplate { 
	private static final long serialVersionUID = 1L;

	private SpringLayout layout;
	private JLabel lfolder;
	private String folder;
	private AppTableModel modeltaula;
	private JTable table;

	/**
	 * Constructs a FolderPainter, creates the pop up menu and sets folder to display
	 * 
	 * @param presenter	event manager
	 * @param menuItems	pop up menu items
	 * @param folder	folder to display
	 * @param header	folder's file system objects table header: name and initial block	
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public FolderPainter(Presenter presenter, Vector<String[]> menuItems, String folder, Vector<Object> header, int width, int height) { 
		super(presenter, menuItems, width, height);
		this.folder = folder;
		init(header);
	}

	private void init(Vector<Object> header) {
		map.clear();
		layout = new SpringLayout();
		this.setLayout(layout);
		
		String sfolder = Translation.getInstance().getLabel("fs_08") + " " + folder;
		if (sfolder.length() > 27) sfolder = sfolder.substring(0, 24) + "...";
		lfolder = new JLabel(sfolder);
		layout.putConstraint(SpringLayout.WEST, lfolder, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, lfolder, 0, SpringLayout.NORTH, this);
		add(lfolder);
		
		modeltaula = new AppTableModel(null, header, false); // Not editable
		table = new JTable(modeltaula);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(presenter);
		this.removeMouseListener(presenter);

		//table.setAutoCreateRowSorter(true);  Error inserting data 
		JScrollPane scroll = new JScrollPane(table);
		table.setFillsViewportHeight(false);
		scroll.setLayout(new ScrollPaneLayout());
		scroll.setPreferredSize(new Dimension(FileSystemPresenter.FOLDER_WIDTH,FileSystemPresenter.FOLDER_HEIGHT-20));
		layout.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.WEST, lfolder);
		layout.putConstraint(SpringLayout.NORTH, scroll, 5, SpringLayout.SOUTH, lfolder);
        add(scroll);
	}
	
	/**
	 * Changes folder and updates table data with that folder's objects information.  
	 * 
	 * @param folder	folder's name
	 * @param header	folder's file system objects table header: name and initial block	
	 * @param data		data from file system objects under current folder
	 */
	public void updateFolderData(String folder, Vector<Object> header, Vector<Vector<Object>> data) {
		this.folder = folder;
		String sfolder = Translation.getInstance().getLabel("fs_08") + " " + folder;
		if (sfolder.length() > 27) sfolder = sfolder.substring(0, 24) + "...";
		lfolder.setText(sfolder);
		
		modeltaula = new AppTableModel(data, header, false); // Not editable
		table.setModel(modeltaula);
				
		for (int i = 0; i< table.getColumnCount(); i++) {
			TableColumn columna = table.getColumnModel().getColumn(i);
			columna.setCellRenderer(new ColorRenderer());
		}
		// Hide column
		//DefaultTableColumnModel modelColumna = (DefaultTableColumnModel) table.getColumnModel();
		//modelColumna.removeColumn(table.getColumnModel().getColumn(0));
		
	}
	
	/**
	 * Returns selected row or null if any
	 * 
	 * @param y 	unused
	 * @param x		unused
	 * 
	 * @return selected row or null if any
	 */
	public Integer detectMouseOver(int x, int y) {
		// Overwrited method
		int row = table.getSelectedRow();
		if (row >= 0) return row;
		return null;
	}
	
	/**
	 * Gets value from cell [row, col] 
	 * 
	 * @param row	
	 * @param col
	 * @return	value from cell [row, col]
	 */
	public Object getValueAt(int row, int col) {
		ColorCell cc = (ColorCell) modeltaula.getValueAt(row, col); 
		return cc.getValue();
	}
	
	/**
 	 * Returns false. Mouse event are managed <code>#detectMouseOver(int, int)</code>       
	 * 
	 * @param o 	unused
	 * @param x	x 	unused
	 * @param y	y 	unused
	 * 
	 * @return	false 
	 * 
	 * @see #detectMouseOver(int, int)
	 */
	public boolean contains(Object o, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}
}
