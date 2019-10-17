package edu.upc.fib.ossim.template.view;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Application Painter template (Template Pattern). 
 * Painter template manage common application canvas behavior. 
 * Maps objects that may generate events and detects mouse events over them. 
 * Optionally a pop up menu is also associated with each painter displaying actions associated to that mouse events 
 *  
 * @author Alex Macia
 */
public abstract class PainterTemplate extends JPanel {
		
	private static final long serialVersionUID = 1L;

	private JPopupMenu popup;
	private Vector<JMenuItem> items;
	private Vector<String[]> menuItems;
	protected Presenter presenter;
	protected Graphics2D g2;
	protected Hashtable<Object, Integer> map;
	
	
	/**
	 * Constructs a PainterTemplate and its pop up menu
	 * 
	 * @param presenter	event manager
	 * @param menuItems	pop up menu items
	 * @param width		
	 * @param height
	 */
	public PainterTemplate(Presenter presenter, Vector<String[]> menuItems, int width, int height) {
		super();
		this.presenter = presenter;
		this.menuItems = menuItems;
		this.addMouseListener(presenter);
		this.addMouseMotionListener(presenter);
		this.setName("painter");
		createPopupMenu();
		this.map = new Hashtable<Object, Integer>(); // map processes to shapes (requests)		
		this.setPreferredSize(new Dimension(width, height));
	}

	/**
	 * Constructs a PainterTemplate 
	 * 
	 * @param presenter	event manager
	 * @param width		
	 * @param height
	 */
	public PainterTemplate(Presenter presenter, int width, int height) {
		super();
		this.presenter = presenter;
		this.map = new Hashtable<Object, Integer>(); // map processes to shapes (requests)		
		this.setPreferredSize(new Dimension(width, height));
	}
	
	/**
	 * Gets map containing canvas objects  
	 * 
	 * @return	map containing canvas objects
	 */
	public Hashtable<Object, Integer> getMap() {
		return map;
	}

	/**
	 * Change cursor icon over the canvas
	 * 
	 * @param c	cursor icon
	 */
	public void changeCursor(Cursor c) {
		this.setCursor(c);
	}
	
	/**
	 * Returns object identifier detected by a mouse event over the canvas or null 
	 * if any object is mapped under mouse position   
	 * 
	 * @param x	mouse x position
	 * @param y	mouse y position
	 * 
	 * @return	object identifier or null
	 * 
	 * @see  #contains(Object, int, int)
	 */
	public Integer detectMouseOver(int x, int y) {
		Set<Object> rectangles = map.keySet();
		
		Iterator<Object> it = rectangles.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if(contains(o, x, y)) return map.get(o);
		}
		return null;
	}
	
	/**
	 * Abstract method to implement specific contains operation. 
	 * Returns true when graphic object contains position (x,y) and false otherwise    
	 * 
	 * @param o	graphic object
	 * @param x	x position
	 * @param y	y position
	 * 
	 * @return	graphic object o contains (x,y) 
	 */
	public abstract boolean contains(Object o, int x, int y);
	
	/**
	 * Creates a pop up menu, every items has 3 String's : command, label, icon file name
	 * 
	 * @param menuItems	pop up menu items
	 */
	private void createPopupMenu() {
		//Create the popup menu.
		// Each element items has 3 String's : command, label, icon
        popup = new JPopupMenu();
        popup.addPopupMenuListener(presenter);
        items = new Vector<JMenuItem>();
       	for (int i = 0; i < menuItems.size(); i++) {
       		JMenuItem item = new JMenuItem(menuItems.get(i)[1],Functions.getInstance().createImageIcon(menuItems.get(i)[2]));
       		item.setActionCommand(menuItems.get(i)[0]);
       		item.addActionListener(presenter);
       		popup.add(item);
       		items.add(item);
       	}
    }
	
	/**
	 * Adds a menu item to pop up menu
	 * 
	 * @param menuItem	menu item vector: command, label and icon file name
	 */
	public void addMenuItem (String[] menuItem) {
		JMenuItem item = new JMenuItem(menuItem[1],Functions.getInstance().createImageIcon(menuItem[2]));
    	item.setActionCommand(menuItem[0]);
    	item.addActionListener(presenter);
    	menuItems.add(menuItem);
        popup.add(item);
        items.add(item);
	}

	/**
	 * Removes all menu items from pop up
	 * 
	 */
	public void clearMenu () {
		popup = new JPopupMenu();
		popup.addPopupMenuListener(presenter);
	    items = new Vector<JMenuItem>();
	}

	/**
	 * Shows pop up menu at mouse position
	 * 
	 */
	public void showPopupMenu() {
		Point mouse = this.getMousePosition();
		if (mouse != null) {
			MenuElement[] elements = popup.getSubElements();
			for (int i=0; i<elements.length;i++) {
				String actionCommand = 	((JMenuItem) elements[i]).getActionCommand();	
				String key = "";
				for (int j=0; j<menuItems.size();j++) { 
					if (actionCommand.equals(menuItems.get(j)[0]))	key = menuItems.get(j)[1];	
				}
				
				if (!("".equals(key))) ((JMenuItem) elements[i]).setText(Translation.getInstance().getLabel(key));  
			}
			popup.show(this, this.getMousePosition().x, this.getMousePosition().y);
		}
	}
	
	/**
	 * By default alias is component name 
	 * 
	 * @return component's name
	 */
	public String getAlias() {
		return this.getName();
	}
}
