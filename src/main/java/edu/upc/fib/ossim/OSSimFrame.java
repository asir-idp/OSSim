package edu.upc.fib.ossim;

import java.awt.Component;

import javax.swing.*; 

/**
 * Frame based application. Desktop environments 
 * 
 * @author Alex
 */
public class OSSimFrame extends JFrame implements OSSim { 

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs OSSimFrame, initialize home panel and menu
	 *  
	 */
	public OSSimFrame() { 
		super(); 
		initialize(); 
	} 

	private void initialize() { 
		this.setTitle("OS Sim"); 
		this.setSize(800,600); //Mida 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		this.setResizable(false);
		AppSession.getInstance().setApp(this);
		Menu menu = new Menu();
		this.setJMenuBar(menu);
		this.setContentPane(new Home(menu));
		this.setVisible(true); // Propietat de visibilitat 
	} 

	/**
	 * Removes previous views and loads new one into container.  
	 * 
	 * @param view	view to load
	 */
	public void loadView(JPanel view) {
		this.getContentPane().removeAll();
		this.setContentPane(view);
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
	 * Application (JFrame) input. 
	 * 
	 * @param args	unused
	 */
	public static void main (String args[]) { 
		@SuppressWarnings("unused")
		OSSimFrame mainFrame = new OSSimFrame(); 
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
	 * Frame allow opening and saving simulations
	 * 
	 * @return true
	 */
	public boolean allowOpenSave() {
		return true;
	}
} 
