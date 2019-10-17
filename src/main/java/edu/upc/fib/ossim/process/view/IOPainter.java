package edu.upc.fib.ossim.process.view;

import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.TablePainterTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Shows a table containing processes doing I/O operations, ordered by I/O remaining time, 
 * table columns are: process pid (column's background color is process color), 
 * process name and time remaining
 * 
 * @author Alex Macia
 */

public class IOPainter extends TablePainterTemplate { 
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a IOPainter  and initialize table
	 * 
	 * @param presenter	event manager
	 * @param title		table title
	 * @param keyHelp	reference to table's help 	
	 * @param header	table header
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public IOPainter(Presenter presenter, String title, String keyHelp, Vector<Object> header, int width, int height) { 
		super(presenter, title, keyHelp, header, width, height);
		table.getSelectionModel().removeListSelectionListener(presenter); // No menu
	}
	
	public JPanel createTitle(String title, String keyHelp) {
		JPanel ptitle = new JPanel(); 
		ptitle.setLayout(new BoxLayout(ptitle, BoxLayout.LINE_AXIS));
		ltitle = new JLabel(Translation.getInstance().getLabel(title));
		ptitle.add(ltitle);
		return ptitle;
	}
}
