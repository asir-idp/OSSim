package edu.upc.fib.ossim.process.view;

import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.TablePainterTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Shows incoming processes table, ordered by arriving time (initial process time), 
 * table columns are: process pid (column's background color is process color), 
 * process name and time to arrive<br/>
 * A pop up menu allows process update and delete  
 * 
 * @author Alex Macia
 */

public class ArrivingPainter extends TablePainterTemplate { 
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a ArrivingPainter  and initialize table
	 * 
	 * @param presenter	event manager
	 * @param title		table title
	 * @param keyHelp	reference to table's help 	
	 * @param header	table header
	 * @param menuItems	pop up menu items
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public ArrivingPainter(Presenter presenter, String title, String keyHelp, Vector<Object> header, Vector<String[]> menuItems, int width, int height) { 
		super(presenter, title, keyHelp, header, menuItems, width, height);
	}
	
	public JPanel createTitle(String title, String keyHelp) {
		JPanel ptitle = new JPanel(); 
		ptitle.setLayout(new BoxLayout(ptitle, BoxLayout.LINE_AXIS));
		ltitle = new JLabel(Translation.getInstance().getLabel(title));
		ptitle.add(ltitle);
		return ptitle;
	}
}
