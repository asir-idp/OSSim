package edu.upc.fib.ossim.template;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.template.view.SettingsTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.HelpDialog;
import edu.upc.fib.ossim.utils.InfoDialog;
import edu.upc.fib.ossim.utils.OpenSaveDialog;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.TimerPanel;
import edu.upc.fib.ossim.utils.Translation;
import edu.upc.fib.ossim.utils.XMLParserJDOM;


/**
 * Application Presenter template (Template Pattern && Model-View-Presenter Pattern).<br/><br/>
 * 
 * Presenter template manage common application presenter behavior:<br/>
 * <ul>
 * <li>Common actions: time controls, open/save simulations, open settings, open information</li>
 * <li>Receive all events (actions) from views</li>
 * <li>Manage language translations through Observer Pattern</li> 
 * <li>Maps help icons to access appropriate help text</li>
 * </ul>
 * In addition presenter centralize all the simulation's panels, dialogs and forms:<br/>
 * <ul>
 * <li>A main panel (PanelTemplate implementation)</li>
 * <li>A set of canvas (PainterTemplate implementations)</li>
 * <li>A settings dialog (SettingsTemplate implementation)</li>
 * <li>A information dialog (InfoDialog instance)</li>
 * <li>All simulation forms (FormTemplate implementation)</li>	
 * <li>And optionally a time panel (TimerPanel implementation)</li>
 * </ul>
 * Access to model (context from Strategy Pattern) are implemented at concrete simulations presenters    
 * 
 * @author Alex Macia
 */
public abstract class Presenter implements Observer, ChangeListener, ActionListener, MouseListener, MouseMotionListener, ListSelectionListener, PopupMenuListener, FocusListener   {
	// 1. Receive events/actions from views  
	// 2. Access model
	// 3. Updates view
	// 4. Language observer 
	protected static final int TIMER_VELOCITY = 1000;
	protected static final int HELP_WIDTH = 600;
	protected static final int HELP_HEIGHT = 350;
	
	protected Hashtable<String, PainterTemplate> painters;
	protected PanelTemplate panel;
	protected TimerPanel timecontrols;
	protected SettingsTemplate settings;
	protected InfoDialog info;
	protected FormTemplate form;

	protected Hashtable<JLabel, String> helps;
	protected Hashtable<String, Integer> actions;
	
	protected boolean wasrunning; // Pause simulation while popup is shown  
	protected boolean simulationComplete;  // Simulation complete  
	protected boolean started; // Simulation started
	

	/**************************************************************************************************/
	/*************************************   Class  management  ***************************************/
	/**************************************************************************************************/
	
	/**
	 * Constructs a Presenter, a parent (top level container) is needed to load panels into, 
	 * also register itself as an observer (language selections notifications)  
	 *
	 *  @param openSettings		open settings at simulation start?
	 *  
	 *  @see #createPanelComponents()
	 *  @see #mapActionsSpecific()
	 *  @see #createContext()
	 */
	public Presenter(boolean openSettings) {
		AppSession.getInstance().getLangNotifier().addObserver(this);
		Translation.getInstance().setLanguage();
		helps = new Hashtable<JLabel, String>();
		painters = new Hashtable<String, PainterTemplate>(); // map processes to shapes (requests)
		actions = new Hashtable<String, Integer>(); // map actions from events	
		mapActions();
		createContext();
		panel = createPanelComponents();
		AppSession.getInstance().getApp().loadView(panel);
		if (openSettings) {
			settings.setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
			settings.setVisible(true);
		}
		simulationComplete = false;
		started = false;
		wasrunning = false;
	}

	/**
	 * Abstract method, implementations may add simulation components to panel 
	 * 
	 * @return a panel filled with all simulation components
	 * 
	 * @see PanelTemplate
	 */
	public abstract PanelTemplate createPanelComponents();
	
	private void mapActions() {
		actions.put("play", 1);
		actions.put("step", 2);
		actions.put("stop", 3);
		actions.put("pause", 4);
		actions.put("timer", 5);
		actions.put("subtimer", 6);
		actions.put("OK", 7);
		actions.put("panel_st", 10);
		actions.put("panel_vd", 11);
		actions.put("open", 12);
		actions.put("save", 13);
		
		mapActionsSpecific();
	}

	/**
	 * Abstract method, implementations may add their specific actions received from views<br/>   
	 * 
	 * For instance<br/> <code>actions.put(action command, number);</code><br/>
	 * <ul>
	 * action command from component that generate the event<br/> 
	 * number >= 20
	 * </ul>
	 */
	public abstract void mapActionsSpecific();

	
	/**
	 * Abstract method, implementations creates their own model objects. 
	 */
	public abstract void createContext();
	
	/** 
	 * Getter of simulation's main panel
	 * 
	 * @return simulations main panel
	 * 
	 * @see PanelTemplate
	 */
	public PanelTemplate getPanel() {
		return panel;
	}

	/** 
	 * Gets the simulation's painter identified by key
	 * 
	 * @param key 	painter identifier
	 * @return 	simulation's painter
	 * 
	 * @see PainterTemplate
	 */
	public PainterTemplate getPainter(String key) {
		return painters.get(key);
	}

	/** 
	 * Getter of simulation's time panel
	 * 
	 * @return simulations time panel
	 * 
	 * @see TimerPanel
	 */
	public TimerPanel getTimecontrols() {
		return timecontrols;
	}

	/** 
	 * Getter of simulation's settings
	 * 
	 * @return simulations settings
	 * 
	 * @see SettingsTemplate
	 */
	public SettingsTemplate getSettings() {
		return settings;
	}

	/** 
	 * Adds a painter to simulation's set of painters
	 *
	 * @param painter	painter instance
	 * @param key		painter identifier
	 * 
	 * @see PainterTemplate
	 */
	public void addPainter(PainterTemplate painter, String key) {
		this.painters.put(key, painter);
	}

	/**
	 * Setter of simulation's time panel
	 * 
	 * @param timecontrols	time panel instance
	 * 
	 * @see TimerPanel
	 */
	public void setTimecontrols(TimerPanel timecontrols) {
		this.timecontrols = timecontrols;
	}

	/**
	 * Setter of simulation's settings panel
	 * 
	 * @param settings	settings panel instance
	 * 
	 * @see SettingsTemplate
	 */
	public void setSettings(SettingsTemplate settings) {
		this.settings = settings;
	}

	/**
	 * Setter of simulation's information panel
	 * 
	 * @param info	information panel instance
	 * 
	 * @see InfoDialog
	 */
	public void setInfo(InfoDialog info) {
		this.info = info;
	}

	/**
	 * Opens an instance of a form and center it relative to simulations main panel
	 * Returns user data from form 
	 * 
	 * @param form	form instance
	 * @return vector containing user data from form 
	 * 
	 * @see FormTemplate
	 */
	public Vector<Object> openForm(FormTemplate form) {
		this.form = form;
		form.setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
		form.setVisible(true);
		return form.getData();
	}

	/**
	 * Gets the latest opened form 
	 * 
	 * @return	form 
	 */
	public FormTemplate getForm() {
		return form;
	}

	/**
	* Returns form's table header
	* 
	* @return	form's table header
	*/
	public Vector<Object> getFormTableHeader() {
		// Do nothing by default. Should be overwrite 
		return null;
	}
	
	
	/**************************************************************************************************/
	/*************************************   Events management  ***************************************/
	/**************************************************************************************************/
	
	/**
	 * Manage ChangeEvents generated by views<br/>
	 * For instance slider that controls time velocity, other concrete events are managed in stateChangedSpecific method 
	 *
	 * @param e	change event
	 * 
     * @see #stateChangedSpecific(ChangeEvent e)
	 */
	public void stateChanged(ChangeEvent e) {
		try {
			JSlider slider = (JSlider) e.getSource();
			if (!slider.getValueIsAdjusting()) { // interested only in the final result of the user's action.
				if (slider.isEnabled()) timecontrols.setDelay(slider.getValue());
			}
		} catch (ClassCastException ex) {
			stateChangedSpecific(e);
		}
	}
	
	/**
	 * Abstract method to implement concrete change events
	 *    
	 * @param e	change event
	 */
	public abstract void stateChangedSpecific(ChangeEvent e);

	
	/**
	 * Manage list events generated by views<br/>
	 * Do nothing by default. Should be overwrite 
	 *
	 * @param e	list selection event
	 * 
	 */
	public void valueChanged(ListSelectionEvent e) {
		// Do nothing by default. Should be overwrite
	}

	/**
	 * ActionListener method<br/>
 	 * 
	 * @param e	action event
	 *
	 * @see #actionPerformed(String)
	 */
	public void actionPerformed(ActionEvent e) {
		actionPerformed(e.getActionCommand());
	}
	
	/**
	 * Manage button events generated by views<br/>
	 * Implements common actions as time controls, open/save simulations, open settings, open information and open add form.
	 * Additional time functionalities and concrete implementations events are managed at actionPlay, actionStop, actionTimer, 
	 * actionDecimal, actionSpecific methods (Template Pattern).
	 * Time stops before actions and continues after action is done.
	 * Finally updates painter's data and information dialog due to possible changes 
 	 * 
	 * @param actionCommand	command string associated with this action event's action 
	 *
	 * @see #actionSpecific(String actionCommand)
	 * @see #actionPlay()
	 * @see #actionStop()
	 * @see #actionTimer() 
	 * @see #actionDecimal()
	 * 
	 */
	public void actionPerformed(String actionCommand) {
		int action = actions.get(actionCommand).intValue();
		OpenSaveDialog open;
		File returnFile;
		boolean end;
				
		try {
			if (action <= 6) { // Time event
				switch (action) {
				case 1:	// Start
					panel.disableRunning(true);
					timecontrols.play();
					if (!started) {
						started = true;
						end = actionPlay();
						if (end) {
							timecontrols.stop(); 
							if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(panel, Translation.getInstance().getError("all_07"), "Warning", JOptionPane.OK_CANCEL_OPTION)) {
								simulationComplete  = true;
								timecontrols.play();	
							} else {
								panel.disableRunning(false);
								actionStop();
							}
						}
					}
					break;
				case 2:	// Step
					if (!started) {
						started = true;
						actionPlay();
						panel.disableRunning(true);   
					}
					
					for (int i=0; i< timecontrols.getTimesfaster(); i++) {
						actionDecimal();
					}
					timecontrols.timer();
					actionTimer();
					
					break;
				case 3:  // Stop
					simulationComplete = false;
					if (started) {
						started = false;
						timecontrols.stop(); 
						panel.disableRunning(false);
						actionStop();
					}
					break;
				case 4:  // pause
					timecontrols.pause(); 
					break;
				case 5:  // timer
					// Increments 1 time unit
					timecontrols.timer();
					end = actionTimer();
					if (!simulationComplete && end) {
						if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(panel, Translation.getInstance().getError("all_07"), "Warning", JOptionPane.OK_CANCEL_OPTION)) 
							timecontrols.pause();
						simulationComplete  = true;	
					}
					break;
				case 6:  // Subtimer
					actionDecimal();
					break;
				}
			} else { 
				wasrunning = false;
				if (timecontrols != null && timecontrols.isRunning() && action != 11) { // Don't stop when opening information panel
					wasrunning = true;
					timecontrols.pause();  // Stop forwarding time
				}

				switch (action) {
				// Form actions 
				case 7:
					boolean ok = form.validateFields();
					if (ok) form.dispose();
					form.setOK(ok);
					break;
				case 10:	// Open Panel settings 
					settings.setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
					settings.setVisible(true);
					break;
				case 11:	// Open Panel information 
					info.setLocationRelativeTo(AppSession.getInstance().getApp().getComponent());
					info.setVisible(true);
					break;
				case 12:	// Open simulation file to load. Only current simulation type 
					// The first confirm accion
					if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(AppSession.getInstance().getApp().getComponent(), Translation.getInstance().getError("all_08"), "Warning", JOptionPane.OK_CANCEL_OPTION)) {
						open = new OpenSaveDialog(panel);
						returnFile = open.showOpenFileChooser();
						if (returnFile != null) {
							//loadXML(returnFile.toURI().toURL());
							Functions.getInstance().openSimulation(returnFile.toURI().toURL());
						}
					}
					break;
				case 13:	// Save actual simulation file  
					open = new OpenSaveDialog(panel);
					returnFile = open.showSaveFileChooser();
					if (returnFile != null) saveXML(returnFile.toURI().toURL());
					break;
				}
				actionSpecific(actionCommand);
				if (wasrunning) timecontrols.play(); // Start forwarding time again
			}
		} catch (SoSimException ex) {
			panel.disableRunning(false);
			if (timecontrols != null) timecontrols.stop();
			wasrunning = false;
			started = false;
			actionStop();
			JOptionPane.showMessageDialog(AppSession.getInstance().getApp().getComponent(),ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			panel.disableRunning(false);
			if (timecontrols != null) timecontrols.stop();
			wasrunning = false;
			started = false;
			actionStop();
			JOptionPane.showMessageDialog(AppSession.getInstance().getApp().getComponent(),ex.toString(),"Error",JOptionPane.ERROR_MESSAGE);
		}
		if (action != 11) updateInfo(); // Update table info, only if not opening info action
		repaintPainters(); // Repaint painters
	}
	
	/**
	 * Additional play actions. Returns true when simulation ends <br/>
	 * Returns false by default. Should be overridden 
	 * 
	 * @return false
	 * 
	 * @throws SoSimException
	 * 
	 * @see #actionPerformed(ActionEvent)
	 */
	public boolean actionPlay() throws SoSimException {
		return false;
	}
	
	/**
	 * Additional stop actions. <br/>
	 * Do nothing by default. Should be overridden 
	 * 
	 * @throws SoSimException
	 * 
 	 * @see #actionPerformed(ActionEvent)
	 */
	public void actionStop() { }
	
	/**
	 * Additional main timer actions. Returns true when simulation ends <br/>
	 * Returns false by default. Should be overridden 
	 *
	 * @return false
	 * 
	 * @throws SoSimException
	 * 
	 * @see #actionPerformed(ActionEvent)
	 */
	public boolean actionTimer() throws SoSimException { 
		return false;
	}
	
	/**
	 * Additional main subtiming actions (this timer is faster than main timer). <br/>
	 * Should be overridden 
	 * 
	 * @see #actionPerformed(ActionEvent) 
	 */
	public void actionDecimal() { }
	
	/**
	 * Abstract method to implement concrete action events
	 *    
	 * @param actionCommand	action command from source
	 * 
	 * @see #actionPerformed(ActionEvent) 
	 */
	public abstract void actionSpecific(String actionCommand) throws SoSimException;
	
	/**
	 * Manage mouse click events generated by views<br/>
	 * Do nothing by default. Should be overridden 
	 *
	 * @param e	mouse event
	 * 
	 */
	public void mouseClicked(MouseEvent e) {
		// Do nothing by default. Should be overwrite
	}
	
	/**
	 * Manage mouse entered events generated by views<br/>
	 * Changes cursor icon over help icons 
	 *
	 * @param e	mouse event
	 *
	 * @see #mouseExited(MouseEvent e) 
	 */
	public void mouseEntered(MouseEvent e) {
		if ("help".equals(e.getComponent().getName())) {
			e.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	protected PainterTemplate getPainter(Component c) {
		Enumeration<PainterTemplate> paintersEnum = painters.elements();
		while (paintersEnum.hasMoreElements()) {
			PainterTemplate pt = paintersEnum.nextElement(); 
			if (c.equals(pt)) return pt;
		}
		return null;
	}

	/**
	 * Manage mouse exited events generated by views<br/>
	 * Restores cursor icon over help icons
	 *
	 * @param e	mouse event
	 *
	 * @see #mouseEntered(MouseEvent e) 
	 */
	public void mouseExited(MouseEvent e) {
		if ("help".equals(e.getComponent().getName())) {
			e.getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Manage mouse pressed events generated by views (UNIX platforms)<br/>
	 * Over a painter, mouse entered, selects painter's object under mouse icon and opens a pop up menu showing possible actions allowed for concrete painter.
	 * Otherwise, if mouse is over a color change label opens a color chooser dialog, if it is over a help icon it opens corresponding help panel.
	 * In any case it stops simulations until user returns    
	 *
	 * @param e	mouse event
	 */
	public void mousePressed(MouseEvent e) {
		wasrunning = false;
		if (timecontrols.isRunning()) {
			wasrunning = true;
			timecontrols.pause();  // Stop forwarding time
		}
	
		painterMousePressed(e);
		
		if ("help".equals(e.getComponent().getName())) { // Help labels
			JLabel help = (JLabel) e.getSource();
			HelpDialog helpPanel = new HelpDialog(this.getPanel(), "all_61", Functions.getInstance().getPropertyString("help_file"), helps.get(help), HELP_WIDTH, HELP_HEIGHT);
			helpPanel.setVisible(false);
			helpPanel.scrollToKey(helps.get(help));
			if (wasrunning) timecontrols.play(); // Start forwarding time again
		}

		if ("color".equals(e.getComponent().getName())) { // color 
			form.changeColor();
			if (wasrunning) timecontrols.play(); // Start forwarding time again
		}
	}
	
	/**
	 * Manage mouse pressed events generated by views (Windows platforms)    
	 *
	 * @param e	mouse event
	 * 
	 * @see #mousePressed(MouseEvent)
	 */

	public void mouseReleased(MouseEvent e) { 
		mousePressed(e);
	}
	
	/**
	 * Manage mouse pressed events generated by painters<br/>
	 * Over a painter, mouse entered, selects painter's object under mouse icon and opens a pop up menu showing possible actions allowed for concrete painter.
	 * 
	 * @param e	mouse event
	 */
	protected void painterMousePressed(MouseEvent e) {
		boolean popup = false;
		if ("painter".equals(e.getComponent().getName())) {
			PainterTemplate pt = getPainter(e.getComponent());
			if (pt != null) {
				Integer id = pt.detectMouseOver(e.getX(), e.getY());

				if (id != null) {
					if (e.isPopupTrigger() && selectElement(id, pt)) {
						popup = true;
						pt.showPopupMenu();
						// Returns running at popup events
					}  
					repaintPainters();
				}  
			}   
			if (!popup && wasrunning) timecontrols.play();
		}  
		
	}
	
	/**
	 * Sets selected a model's object identified by id from a painter  
	 * 
	 * @param id	model's object identifier
	 * @param pt 	object's parent painter 
	 * 
	 * @return element can be selected
	 */
	protected abstract boolean selectElement(Integer id, PainterTemplate pt);
	
	/**
	 * Manage mouse moved events generated by views<br/>
	 * Over a painter's object change mouse cursor    
	 *
	 * @param e	mouse event
	 */
	public void mouseMoved(MouseEvent e) { 
		PainterTemplate pt = getPainter(e.getComponent());
		
		Integer id = pt.detectMouseOver(e.getX(), e.getY());

		if (id != null) {
			pt.changeCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			pt.changeCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Manage mouse drag events generated by views<br/>
	 * Do nothing by default. Should be overridden 
	 *
	 * @param e	mouse event
	 * 
	 */
	public void mouseDragged(MouseEvent e) { }

	/**
	 * Manage popup's cancel event (User selects no item)<br/>
	 * Starts simulation if it was paused when popup opened 
	 *
	 * @param e	popup menu event
	 * 
	 */
	public void popupMenuCanceled(PopupMenuEvent e) {
		// No Item selected --> After visible
		if (wasrunning) timecontrols.play(); // Start forwarding time again
	}

	/**
	 * Manage popup's become invisible event (User selects an item)<br/>
	 * Starts simulation if it was paused when popup opened 
	 *
	 * @param e	popup menu event
	 * 
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// Item selected --> After Visible
		// No Item selected --> After cancel
		if (wasrunning) timecontrols.play(); // Start forwarding time again
	}

	/**
	 * Manage popup's become visible event<br/>
	 * Do nothing by default. Should be overridden 
	 *
	 * @param e	popup menu event
	 * 
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// First pop up event
	}
	
	public void focusGained(FocusEvent e) {
		if (e.getComponent() instanceof JTextComponent) { // FocusListener only on JTextComponent, but...
			JTextComponent tc = (JTextComponent) e.getComponent();
			tc.setText(tc.getText()); // !??! java problem
			tc.selectAll();
		}
	}

	public void focusLost(FocusEvent e) { }
	
	/**
	 * Repaints all simulation painters to update information
	 * 
	 */
	public void repaintPainters() {
		// Repaint set of painters
		Enumeration<PainterTemplate> paintersEnum = painters.elements();
		while (paintersEnum.hasMoreElements()) paintersEnum.nextElement().repaint();
	}

	/**
	 * Close info panel
	 */
	public void closeInfo() {
		if (info != null) info.dispose();
	}
	
	/**************************************************************************************************/
	/*************************************  Observer Language *****************************************/
	/**************************************************************************************************/
	
	/**
	 * Translate all simulation components
	 * 
	 * @param o		observable
	 * @param arg	unused
	 */
	public void update(Observable o, Object arg) {
		// Update Panel labels
		panel.updateLabels();
		// Update painter
		repaintPainters();
		// Update settings dialog
		settings.updateLabels();
		// Update time controls tooltips
		if (timecontrols != null) timecontrols.updateLabels();
		// Concrete translations
		updateLabels();
	}
	
	/**
	 * Abstract method that translates concrete simulation components
	 * 
	 */
	public abstract void updateLabels(); 
	
	/**
	 * Returns a help icon that generate help events. This help component is hashed using value key that references an anchor into help file, it allow later component identification. 
	 * Help file depends on simulation instance   
	 * 
	 * @param key
	 * @return	icon that generate help events
	 * 
	 * @see #mousePressed(MouseEvent)
	 * @see #getHelpFile()
	 */
	public JLabel createHelp(String key) {
		JLabel help = new JLabel(Functions.getInstance().createImageIcon("help.gif"));
		help.addMouseListener(this);
		help.setName("help");
		helps.put(help, key);
		return help;
	}

	/**************************************************************************************************/
	/*************************************   Request management ***************************************/
	/**************************************************************************************************/

	/**
	 * Abstract method to updates information panel data, usually model give data  
	 */
	public abstract void updateInfo();
	
	/**
	 * Abstract method to return different model object's iterators, iterators contains only objects identifier
	 *  
	 * @param i		indicates iterator to return
	 * @return		a model object's iterator
	 */
	public abstract Iterator<Integer> iterator(int i);
	
	/**
	 * Abstract method that may returns main current algorithm information to display into main panel  
	 * 
	 * @return	current algorithm information 
	 */
	public abstract String getAlgorithmInfo();
	
	/**
	 * Abstract method that requests information about a model object identified by <code>id</code>
	 * 
	 * @param id	model object identifier
	 * 
	 * @return	model object information  
	 */
	public abstract Vector<String> getInfo(int id);
	
	/**
	 * Abstract method that requests color from a model object identified by <code>id</code>
	 * 
	 * @param id	model object identifier
	 * 
	 * @return 	model object color
	 */
	public abstract Color getColor(int id);
	
	/**************************************************************************************************/
	/*************************************   XML management ***************************************/
	/***************************************************************************************************/

	/**
	 * Initialize context and load's an xml document from a file
	 *
	 * @param file	xml file
	 * 
	 * @see XMLParserJDOM
	 * 
	 */
	public void loadXML(URL file) throws SoSimException {
		// Load's document root
		createContext();
		Vector<Vector<Vector<String>>> data;
		XMLParserJDOM parser = new XMLParserJDOM(file);

		// Load xml data 
		for (int i = 0; i< getXMLChilds().size(); i++) {
			data = new Vector<Vector<Vector<String>>>();
			String child = getXMLChilds().get(i);
			data.addAll(parser.getElements(child));
			putXMLData(i, data);
		}
		
		// Updates panel information
		panel.setLabel(getAlgorithmInfo());
	}

	/**
	 * Save a simulation into an xml file
	 *
	 * @param file	xml file
	 * 
	 * @throws SoSimException	exception thrown by parser
	 * 
	 * @see XMLParserJDOM
	 * 
	 */
	public void saveXML(URL file) throws SoSimException  {
		// Creates root
		XMLParserJDOM parser = new XMLParserJDOM(file, getXMLRoot());

		for (int i = 0; i< getXMLChilds().size(); i++) {
			String child = getXMLChilds().get(i);
			parser.addElement(getXMLRoot(), child, null);
			parser.addElements(child, getXMLData(i));	
		}

		parser.writeXmlFile();
	}
	
	/**
	 * Abstract method that may return root element value to manage simulation's xml operations 
	 * 
	 * @return	root element value
	 */
	public abstract String getXMLRoot();
	
	/**
	 * Abstract method that may return root direct child 
	 * 
	 * @return	root direct child
	 */
	public abstract Vector<String> getXMLChilds();
	
	/**
	 * Abstract method that may return all information from a concrete child identified by <code>child</code>  
	 * 
	 * @param child	child identifier
	 * 
	 * @return child's data
	 * 
	 * @see XMLParserJDOM#getElements
	 */
	public abstract Vector<Vector<Vector<String>>> getXMLData(int child);
	
	/**
	 * Abstract method that build all model information from a concrete child identified by <code>child</code>
	 * 
	 * @param child	child identifier
	 * @param data child's data
	 * @throws SoSimException	exception thrown by parser  
	 * 
	 * @see XMLParserJDOM#addElements
	 */
	public abstract void putXMLData(int child, Vector<Vector<Vector<String>>> data) throws SoSimException;

}
