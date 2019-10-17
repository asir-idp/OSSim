package edu.upc.fib.ossim.disk.view;

import javax.swing.SpringLayout;

import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PanelTemplate;

/**
 * Disk scheduling panel. Tool bar allows request creation, scheduling settings managing, 
 * and time control apart from common actions such as: loading and saving simulations. <br/>
 * This panel contains 3 elements (painters), a physical disk platter, 
 * a graph showing served request and a table with all requests and scheduling information 
 *  
 * @author Alex Macia
 * 
 * @see PanelTemplate
 * @see DiskPainter
 * @see GraphPainter
 * @see InfoPainter
 */
public class PanelDisk extends PanelTemplate { 
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a PanelDisk 
	 * 
	 * @param presenter	event manager
	 * @param keyLabelAdd	reference to add label string into bundle file	
	 */
	public PanelDisk(Presenter presenter, String keyLabelAdd) { 
		super(presenter, keyLabelAdd, "disk", true, false);
	}
	
	/**
	 * Adds components to panel, platter, graph and request's table 
	 */
	public void initSpecificLayout() {
		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(DiskPresenter.DISK_PAINTER), 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(DiskPresenter.DISK_PAINTER), 10, SpringLayout.SOUTH, header);
		pane.add(presenter.getPainter(DiskPresenter.DISK_PAINTER));
		
		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(DiskPresenter.GRAPH_PAINTER), 20, SpringLayout.EAST, presenter.getPainter(DiskPresenter.DISK_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(DiskPresenter.GRAPH_PAINTER), 0, SpringLayout.NORTH, presenter.getPainter(DiskPresenter.DISK_PAINTER));
		pane.add(presenter.getPainter(DiskPresenter.GRAPH_PAINTER));
		
		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(DiskPresenter.REQUEST_PAINTER), 0, SpringLayout.WEST, presenter.getPainter(DiskPresenter.GRAPH_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(DiskPresenter.REQUEST_PAINTER), 10, SpringLayout.SOUTH, presenter.getPainter(DiskPresenter.GRAPH_PAINTER));
		pane.add(presenter.getPainter(DiskPresenter.REQUEST_PAINTER));
	}
} 
