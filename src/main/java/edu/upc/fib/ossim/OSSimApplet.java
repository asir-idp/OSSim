package edu.upc.fib.ossim;

import java.awt.Component;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * Applet based application. Web environment
 * 
 * @author Alex
 */
public class OSSimApplet extends JApplet implements OSSim {
	private static final long serialVersionUID = 1L;
	StringBuffer buffer;

	/**
	 * Application (JApplet) input, initialize home panel, menu and language observable.
	 * 
	 * @see JApplet#init()
	 */
	public void init() {
		//Execute a job on the event-dispatching thread; creating this applet's GUI.
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createGUI();
				}
			});
		} catch (Exception e) { 
			System.out.println("createGUI didn't complete successfully");
		}
	}

	private void createGUI() {
		AppSession.getInstance().setApp(this);
		Menu menu = new Menu();
		this.setJMenuBar(menu);
		this.setContentPane(new Home(menu));
	}        

	/**
	 * Does nothing at all 
	 * 
	 * @see JApplet#start()
	 */
	public void start() {
	}

	/**
	 * Does nothing at all 
	 * 
	 * @see JApplet#stop()
	 */
	public void stop() {
	}

	/**
	 * Does nothing at all 
	 * 
	 * @see JApplet#destroy()
	 */
	public void destroy() {
	}

	/**
	 * Removes previous views, updates interface and loads new one into container.  
	 * 
	 * @param view	view to load
	 * 
	 * @see JPanel#updateUI()
	 */
	public void loadView(JPanel view) {
		this.getContentPane().removeAll();
		this.setContentPane(view);
		view.updateUI();
		this.repaint();
		this.setVisible(true);
	}
	
	/**
	 * Shows a message into a message dialog 	 
	 * 
	 * @param msg	message content
	 */
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}
	
	/**
	 * Gets itself
	 * 
	 * @return this
	 */
	public Component getComponent() {
		return this;
	}
	
	/**
	 * Applet does not allow opening and saving simulations. Java security issues
	 * 
	 * @return false
	 */
	public boolean allowOpenSave() {
		return false;
	}
}
