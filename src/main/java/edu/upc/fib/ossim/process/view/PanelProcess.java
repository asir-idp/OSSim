package edu.upc.fib.ossim.process.view;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Functions;


/**
 * Main process scheduling panel. Tool bar allows process creation, scheduler settings managing, 
 * information view and time control apart from common actions such as: loading and saving simulations. <br/>
 * This panel contains 3 elements (painters) a main process queue (ready queue),
 * a secondary one with incoming processes and the cpu.   
 *  
 * @author Alex Macia
 * 
 * @see PanelTemplate
 * @see QueuePainter
 * @see ProcessorPainter
 */
public class PanelProcess extends PanelTemplate { 
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a PanelProcess 
	 * 
	 * @param presenter	event manager
	 * @param keyLabelAdd	reference to add label string into bundle file	
	 */
	public PanelProcess(Presenter presenter, String keyLabelAdd) { 
		super(presenter, keyLabelAdd, "scheduling", true, true);
	}

	/**
	 * Adds components to panel, ready queue, incoming queue and cpu 
	 */
	public void initSpecificLayout() {
		QueuePainter procs = (QueuePainter) presenter.getPainter(ProcessPresenter.PROCS_PAINTER);
		JScrollPane scroll1 = new JScrollPane(procs);
		scroll1.setPreferredSize(new Dimension(procs.getViewPortwidth()+10, procs.getViewPortheight()+10));
		layout.putConstraint(SpringLayout.WEST, scroll1, 0, SpringLayout.WEST, header);
		layout.putConstraint(SpringLayout.NORTH, scroll1, 10, SpringLayout.SOUTH, header);
		pane.add(scroll1);
		
		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER), 0, SpringLayout.WEST, scroll1);
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER), 40, SpringLayout.SOUTH, scroll1);
		pane.add(presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER));

		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(ProcessPresenter.IO_PAINTER), 10, SpringLayout.EAST, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.IO_PAINTER), 0, SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER));
		pane.add(presenter.getPainter(ProcessPresenter.IO_PAINTER));

		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER), 10, SpringLayout.EAST, presenter.getPainter(ProcessPresenter.IO_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER), 0, SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.IO_PAINTER));
		pane.add(presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER));
		
		JLabel up = new JLabel(Functions.getInstance().createImageIcon("up.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up, 0, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, up, 5, SpringLayout.SOUTH, scroll1);
		pane.add(up);

		JLabel up_io = new JLabel(Functions.getInstance().createImageIcon("up.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up_io, 0, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.IO_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, up_io, 0, SpringLayout.NORTH, up);
		pane.add(up_io);
		
		JLabel left = new JLabel(Functions.getInstance().createImageIcon("left.png"));
		layout.putConstraint(SpringLayout.WEST, left, -35, SpringLayout.WEST, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, left, 15, SpringLayout.SOUTH, scroll1);
		pane.add(left);

		JLabel down = new JLabel(Functions.getInstance().createImageIcon("down.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, down, 10, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, down, 0, SpringLayout.NORTH, up);
		pane.add(down);
		
		JLabel up_cpu = new JLabel(Functions.getInstance().createImageIcon("up.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up_cpu, -10, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, up_cpu, 0, SpringLayout.NORTH, up);
		pane.add(up_cpu);
	}
} 

