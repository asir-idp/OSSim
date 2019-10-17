package edu.upc.fib.ossim.disk.view;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.TablePainterTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Shows disk scheduling table information, served requests ordered by time served, then 
 * pending request and at last incoming requests. for each served requests is shown 
 * head movement from previous request and accumulate movement<br/>
 * A pop up menu allows request update and delete  
 * 
 * @author Alex Macia
 */

public class InfoPainter extends TablePainterTemplate { 
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a InfoPainter  and initialize table
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
	public InfoPainter(Presenter presenter, String title, String keyHelp, Vector<Object> header, Vector<String[]> menuItems, int width, int height) { 
		super(presenter, title, keyHelp, header, menuItems, width, height);
	}

	public JPanel createTitle(String title, String keyHelp) {
		//"pr_02"
		JPanel phelp = new JPanel(); 
		phelp.setLayout(new BoxLayout(phelp, BoxLayout.LINE_AXIS));
		ltitle = new JLabel(Translation.getInstance().getLabel(title));
		phelp.add(ltitle);
		phelp.add(Box.createHorizontalGlue());
		help = presenter.createHelp(keyHelp);
		phelp.add(help);
		return phelp;
	}
}
