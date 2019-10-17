package edu.upc.fib.ossim.filesystem.view;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;


/**
 * Paints system's file tree, file System objects are: files, folders and links. 
 * A pop up menu allows add, update, remove objects from the tree and create links.  
 * 
 * 
 * @author Alex Macia
 */
public class FilesTreePainter extends PainterTemplate { 
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private DefaultTreeModel treeModel;

	/**
	 * Constructs a FilesTreePainter, creates the pop up menu and and initialize system's file tree 
	 * (root and ".", ".." links).  
	 * 
	 * @param presenter	event manager
	 * @param menuItems	pop up menu items
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public FilesTreePainter(Presenter presenter, Vector<String[]> menuItems, int width, int height) { 
		super(presenter, menuItems, width, height);
		init();
	}

	/**
	 * Returns system's file tree model 
	 * 
	 * @return	system's file tree model
	 */
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	private void init() {
		map.clear();
		
		int[] rootBlocks = ((FileSystemPresenter) presenter).getRootIds();
		String sroot = ((FileSystemPresenter) presenter).getName(rootBlocks[0]);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(sroot);
		treeModel = new DefaultTreeModel(root);
	    tree = new JTree(treeModel);
	    
	    map.put(root, rootBlocks[0]);
		
	    addNode(rootBlocks[0], rootBlocks[1]); // "."
	    addNode(rootBlocks[0], rootBlocks[2]); // ".."

	    tree.setEditable(true);
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    tree.setShowsRootHandles(true);
	    this.removeMouseListener(presenter);
	    tree.addMouseListener(presenter);
	    
		JScrollPane scroll = new JScrollPane(tree);
		scroll.setLayout(new ScrollPaneLayout());
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(FileSystemPresenter.TREE_WIDTH, FileSystemPresenter.TREE_HEIGHT-20));
		add(scroll);
	}
		
	/**
	 * Initialize system's file tree, restoring initial state: root and ".", ".." links.  
	 * 
	 */
	public void initTree() {
		int[] rootBlocks = ((FileSystemPresenter) presenter).getRootIds();

		DefaultMutableTreeNode root = getNode(rootBlocks[0]);
				
		int childs = root.getChildCount();
		for(int i=0; i < childs; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(0); // Removing change child index, so ever remove 0 index
			treeModel.removeNodeFromParent(child);
		}
		map.clear();
		
		map.put(root, rootBlocks[0]);
		updNode(rootBlocks[0]); // Update root
	    addNode(rootBlocks[0], rootBlocks[1]); // "."
	    addNode(rootBlocks[0], rootBlocks[2]); // ".."
	}
	
	/**
	 * Add's a new system's file object whose parent is another system's file object 
	 * 
	 * @param idparent	parent identifier
	 * @param id		new system's file object identifier
	 */
	public void addNode(int idparent, int id) {
		// Find parent
		DefaultMutableTreeNode parent = getNode(idparent);
	    
	    if (parent != null) {
		    // Create child
		    String schild = ((FileSystemPresenter) presenter).getName(id);
		    DefaultMutableTreeNode child = new DefaultMutableTreeNode(schild);
	
		    map.put(child, id);
		    
		    treeModel.insertNodeInto(child, parent, parent.getChildCount());
	
		    tree.scrollPathToVisible(new TreePath(child.getPath()));
	    }
	}

	/**
	 * Updates a system's file object
	 * 
	 * @param id  system's file object identifier	
	 */
	public void updNode(int id) {
		// Find node
		DefaultMutableTreeNode node = getNode(id);
	    
	    if (node != null) {
	    	String snode = ((FileSystemPresenter) presenter).getName(id);
	    	//node.setUserObject(snode);
	    	
	    	TreeNode[] nodesToRoot = treeModel.getPathToRoot(node); 
	        TreePath pathToRoot = new TreePath(nodesToRoot); 
	        treeModel.valueForPathChanged(pathToRoot, snode ); 
	    }
	}
	
	/**
	 * Remove a system's file object from the tree
	 * 
	 * @param id  system's file object identifier	
	 */
	public void delNode(int id) {
		// Find node
		DefaultMutableTreeNode node = getNode(id);
	    
	    if (node != null) {
			treeModel.removeNodeFromParent(node);
	    }
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
	 * Returns system's file object identifier at mouse position or null if any
	 * 
	 * @param y 	
	 * @param x	
	 * 
	 * @return system's file object identifier at mouse position or null if any
	 */
	public Integer detectMouseOver(int x, int y) {
		// overridden method
		int row = tree.getRowForLocation(x, y);
        if(row == -1) return null;              
        tree.setSelectionRow(row);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        return map.get(node);
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
		return false;
	}
}
