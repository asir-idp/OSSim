package edu.upc.fib.ossim.utils;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * FileChooser dialog to load / save <code>xml</code> simulations
 * 
 * @author Alex Macia
 */
public class OpenSaveDialog { 
	private static final long serialVersionUID = 1L;
	private JFileChooser file;
	private Component parent;

	/**
	 * Constructs dialog and apply <code>xml</code> file filters
	 * 
	 * @param parent	
	 */
	public OpenSaveDialog(Component parent) { 
		super();
		this.parent = parent;
		file = new JFileChooser();
		file.setFileSelectionMode(JFileChooser.FILES_ONLY); // Only select files
		file.setAcceptAllFileFilterUsed(false); // Anything else
		file.setFileFilter(new FileNameExtensionFilter(".xml", "xml")); // Only XML
	}

	/**
	 * Shows open dialog and returns file selected or null if any
	 * 
	 * @return	file to open or null
	 */
	public File showOpenFileChooser() {
		file.setDialogTitle(Translation.getInstance().getLabel("all_12"));
		int returnVal = file.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return file.getSelectedFile();
		} else {
			return null;
		}
	}

	/**
	 * Shows save dialog and returns file selected or null if any
	 * 
	 * @return	file to save or null
	 */
	public File showSaveFileChooser() {
		file.setDialogTitle(Translation.getInstance().getLabel("all_13"));
		int returnVal = file.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selected = file.getSelectedFile();
			File returned;
			if (!file.getFileFilter().accept(selected)) {
				// Add extension
				returned = new File(selected.getAbsolutePath() + ".xml");
				selected.renameTo(returned);
			} else returned = selected;
			return returned;
		} else {
			return null;
		}
	}
} 
