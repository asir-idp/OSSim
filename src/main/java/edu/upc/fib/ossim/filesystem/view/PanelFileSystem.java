package edu.upc.fib.ossim.filesystem.view;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Main file system management panel. Tool bar allows, settings managing and 
 * information view, apart from common actions such as: loading and saving simulations. <br/>
 * This panel contains 3 elements (painters) a secondary storage device,
 * a system's file tree and a folder content's table.   
 *  
 * @author Alex Macia
 * 
 * @see PanelTemplate
 * @see DevicePainter
 * @see FilesTreePainter
 * @see FolderPainter
 */
public class PanelFileSystem extends PanelTemplate  { 
	private static final long serialVersionUID = 1L;
	private JLabel ltitol;
	
	/**
	 * Constructs a PanelFileSystem 
	 * 
	 * @param presenter	event manager
	 */
	public PanelFileSystem(Presenter presenter) { 
		super(presenter, null, "filesystem", false, true);
	}

	/**
	 * Adds components to panel, secondary storage device, file tree and folder content's table
	 */
	public void initSpecificLayout() {
		ltitol = new JLabel(Translation.getInstance().getLabel("fs_10"));
		layout.putConstraint(SpringLayout.WEST, ltitol, 0, SpringLayout.WEST, header);
		layout.putConstraint(SpringLayout.NORTH, ltitol, 10, SpringLayout.SOUTH, header);
		pane.add(ltitol);
		
		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(FileSystemPresenter.FILESTREE_PAINTER), 0, SpringLayout.WEST, ltitol);
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(FileSystemPresenter.FILESTREE_PAINTER), 10, SpringLayout.SOUTH, ltitol);
		pane.add(presenter.getPainter(FileSystemPresenter.FILESTREE_PAINTER));
		
		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(FileSystemPresenter.FOLDER_PAINTER), 0, SpringLayout.WEST, presenter.getPainter(FileSystemPresenter.FILESTREE_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(FileSystemPresenter.FOLDER_PAINTER), 0, SpringLayout.SOUTH, presenter.getPainter(FileSystemPresenter.FILESTREE_PAINTER));
		pane.add(presenter.getPainter(FileSystemPresenter.FOLDER_PAINTER));

		DevicePainter disk = (DevicePainter) presenter.getPainter(FileSystemPresenter.DEVICE_PAINTER);
		JScrollPane scroll1 = new JScrollPane(disk);
		scroll1.setPreferredSize(new Dimension(FileSystemPresenter.DEVICE_WIDTH+10, FileSystemPresenter.DEVICE_HEIGHT+10));
		
		layout.putConstraint(SpringLayout.WEST, scroll1, 10, SpringLayout.EAST, presenter.getPainter(FileSystemPresenter.FILESTREE_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, scroll1, 0, SpringLayout.NORTH, header);
		pane.add(scroll1);
	}
	
	/**
	 * Translates labels and tool tips to current session language 
	 */
	public void updateLabels() {
		// overridden method
		super.updateLabels();
		ltitol.setText(Translation.getInstance().getLabel("fs_10"));
	}
} 
