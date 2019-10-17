package edu.upc.fib.ossim.filesystem.view;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Link form. Adds or updates a link, besides common file system objects fields such as: 
 * parent folder, name and a submit button, this form includes system's file tree to select link source. 
 * 
 * @author Alex Macia
 * 
 * @see FormFileSystemItem
 */
public class FormLink extends FormFileSystemItem { 

	private JCheckBox soft;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private Hashtable<Object, Integer> map;
	
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a FormFile
	 * 
	 * @see FormFileSystemItem#FormFileSystemItem(Presenter, String, JLabel, Vector, Vector)
	 */
	public FormLink(Presenter presenter, String title, JLabel help, Vector<Object> values, Vector<String> labels) {
		super(presenter, title, help, values, labels);
	}

	/**
	 * Creates and initialize concrete form fields, system's file tree. 
	 * Fields and its labels are laid out as a compact grid.
	 *  
	 * @see FormFileSystemItem#initSpecific(Vector)
	 * @see Functions#makeCompactGrid(java.awt.Container, int, int, int, int, int, int)
	 */
	public void initSpecific(Vector<Object> values) {
		soft = new JCheckBox();
		soft.setSelected(true); // Default soft link. On Windows it is not shown
		soft.setEnabled(false); // No editable
		if (((FileSystemPresenter) presenter).showSoftField()) {
			
			if (values.size() > 1) {
				boolean softState = (Boolean) values.get(3);
				soft.setSelected(softState);
			} else {
				soft.setSelected(false);
				soft.setEnabled(true); // editable
			}
		} 
		grid.add(new JLabel(Translation.getInstance().getLabel("fs_27"))); // Soft link
		grid.add(soft);
		
		grid.add(new JLabel(Translation.getInstance().getLabel("fs_28"))); // Target link
		
		map = ((FileSystemPresenter) presenter).getMap();
		treeModel = ((FileSystemPresenter) presenter).getTreeModel();
		tree = new JTree(treeModel);

		if (values.size() > 1) {
			Integer linkId = (Integer) values.get(2);
			if (linkId >= 0) {
				DefaultMutableTreeNode node = getNode(linkId);
				tree.setSelectionPath(new TreePath(node.getPath())); // Selects node
			}
			tree.setEnabled(false);
		} else {
			tree.setSelectionRow(0); // Selects root
			tree.setEditable(true);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
	    
	    tree.setShowsRootHandles(true);
	    
		JScrollPane scroll = new JScrollPane(tree);
		scroll.setLayout(new ScrollPaneLayout());
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(FileSystemPresenter.TREE_WIDTH, FileSystemPresenter.TREE_HEIGHT-20));
		grid.add(scroll);
				
		Functions.getInstance().makeCompactGrid(grid, 3, 2, 6, 6, 6, 6);
		JPanel pgrid = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pgrid.add(grid);
		pn.add(pgrid);
    }
	
	private DefaultMutableTreeNode getNode(int id) {
		Set<Object> keys = map.keySet();
	    Iterator<Object> it = keys.iterator();
	    while (it.hasNext()) {
	    	DefaultMutableTreeNode aux = (DefaultMutableTreeNode) it.next();
	    	if (map.get(aux).intValue() == id) return aux;	
	    }
	    return null;
	}
	
	/**
	 * Returns a vector containing form values
	 *
	 * @return form data
	 */
	public Vector<Object> getSpecificData() {
		Vector<Object> data = super.getSpecificData();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node != null) data.add(map.get(node)); // Link id
		else data.add(-1); // Link id
		data.add(soft.isSelected()); // Soft link
		return data;
	}
} 
