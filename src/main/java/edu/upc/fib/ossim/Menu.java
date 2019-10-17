package edu.upc.fib.ossim;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.HelpDialog;
import edu.upc.fib.ossim.utils.OpenSaveDialog;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Application menu:
 * <ul>
 * <li>File: open and save simulations, exit application and home</li>
 * <li>Process: opens process scheduling simulation</li>
 * <li>Memory: opens memory management simulation</li>
 * <li>Files: opens file system management simulation</li>
 * <li>Disk: opens disk scheduling simulation</li>
 * <li>Help: opens main application help and about panel</li>   
 * </ul> 
 * <br/>
 * Menu also manages its own action events and observes language changes    
 * 
 * @author Àlex
 */
public class Menu extends JMenuBar implements ActionListener, Observer { 
	private static final long serialVersionUID = 1L;
	private static final int HELP_WIDTH = 600;
	private static final int HELP_HEIGHT = 350;

	private JMenu fileMenu = null; 
	private JMenu processMenu = null; 
	private JMenu memoryMenu = null;
	private JMenu diskMenu = null;
	private JMenu filesMenu = null;
	private JMenu helpMenu = null;

	private JLabel lang;
	private JButton ben;
	private JButton bca;
	private JButton bes;

	private JMenuItem exitMenuItem = null; 
	private JMenuItem openMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JMenuItem homeMenuItem = null;
	private JMenuItem simProcsMenuItem = null;
	private JMenuItem simMemMenuItem = null;
	private JMenuItem simFilesMenuItem = null;
	private JMenuItem simdiskMngMenuItem = null;
	private JMenuItem helpMenuItem = null;
	private JMenu samplesMenu = null;

	private JMenuItem samplesSch = null;
	private JMenuItem samplesMem = null;
	private JMenuItem samplesFs = null;
	private JMenuItem samplesDisk = null;

	private JMenu exercisesMenu = null;

	private JMenuItem exercisesSch = null;
	private JMenuItem exercisesMem = null;
	private JMenuItem exercisesFs = null;
	private JMenuItem exercisesDisk = null;


	private JMenuItem aboutMenuItem = null;



	private Hashtable<String, Integer> actions;

	/**
	 * Constructs menu, initialize menu components and maps menu actions.
	 */
	public Menu() { 
		AppSession.getInstance().getLangNotifier().addObserver(this);
		mapActions();
		initialize(); 
	} 

	private void mapActions() {
		actions = new Hashtable<String, Integer>(); // map actions from events
		actions.put("exit", 1);
		actions.put("open", 2);
		actions.put("save", 3);
		actions.put("home", 4);
		actions.put("about", 5);
		actions.put("help", 6);
		actions.put("en", 10);
		actions.put("ca", 11);
		actions.put("es", 12);
		actions.put("sch", 20);
		actions.put("mngc", 30);
		actions.put("mngd", 40);
		actions.put("fls", 50);
		actions.put("samplesSch", 60);
		actions.put("samplesMem", 61);
		actions.put("samplesFs", 62);
		actions.put("samplesDisk", 63);
		actions.put("exercisesSch", 70);
		actions.put("exercisesMem", 71);
		actions.put("exercisesFs", 72);
		actions.put("exercisesDisk", 73);
	}

	private void initialize() { 
		fileMenu = new JMenu();
		fileMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		fileMenu.setText(Translation.getInstance().getLabel("all_50")); 
		fileMenu.setMnemonic(Translation.getInstance().getLabel("all_20").charAt(0));
		exitMenuItem = new JMenuItem();
		exitMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		exitMenuItem.setText(Translation.getInstance().getLabel("all_51")); 
		exitMenuItem.setActionCommand("exit");
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		exitMenuItem.addActionListener(this); 
		fileMenu.add(exitMenuItem); 

		openMenuItem = new JMenuItem(); 
		openMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		openMenuItem.setText(Translation.getInstance().getLabel("all_12")); // Open simulation 
		openMenuItem.setActionCommand("open");
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		if (!AppSession.getInstance().getApp().allowOpenSave()) openMenuItem.setEnabled(false);
		openMenuItem.addActionListener(this); 
		fileMenu.add(openMenuItem); 

		saveMenuItem = new JMenuItem(); 
		saveMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		saveMenuItem.setText(Translation.getInstance().getLabel("all_13")); // Save simulation 
		saveMenuItem.setActionCommand("save");
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.addActionListener(this);
		saveMenuItem.setEnabled(false); // Disabled until opening any simulation
		fileMenu.add(saveMenuItem); 

		homeMenuItem = new JMenuItem(); 
		homeMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		homeMenuItem.setText(Translation.getInstance().getLabel("all_14")); 
		homeMenuItem.setActionCommand("home");
		homeMenuItem.addActionListener(this);
		fileMenu.add(homeMenuItem); 

		this.add(fileMenu); 

		processMenu = new JMenu(); 
		processMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		processMenu.setText(Translation.getInstance().getLabel("all_52")); 
		processMenu.setMnemonic(Translation.getInstance().getLabel("all_24").charAt(0));
		simProcsMenuItem = new JMenuItem();
		simProcsMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		simProcsMenuItem.setText(Translation.getInstance().getLabel("all_53"));
		simProcsMenuItem.setActionCommand("sch");
		simProcsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		simProcsMenuItem.addActionListener(this); 
		processMenu.add(simProcsMenuItem); 
		this.add(processMenu);

		memoryMenu = new JMenu(); 
		memoryMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		memoryMenu.setText(Translation.getInstance().getLabel("all_54")); 
		memoryMenu.setMnemonic(Translation.getInstance().getLabel("all_26").charAt(0));
		simMemMenuItem = new JMenuItem(); 
		simMemMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		simMemMenuItem.setText(Translation.getInstance().getLabel("all_55"));
		simMemMenuItem.setActionCommand("mngc");
		simMemMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		simMemMenuItem.addActionListener(this); 
		memoryMenu.add(simMemMenuItem); 

		this.add(memoryMenu);

		filesMenu = new JMenu(); 
		filesMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		filesMenu.setText(Translation.getInstance().getLabel("all_58")); 
		filesMenu.setMnemonic(Translation.getInstance().getLabel("all_30").charAt(0));
		simFilesMenuItem = new JMenuItem(); 
		simFilesMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		simFilesMenuItem.setText(Translation.getInstance().getLabel("all_59"));
		simFilesMenuItem.setActionCommand("fls");
		simFilesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		simFilesMenuItem.addActionListener(this); 
		filesMenu.add(simFilesMenuItem); 
		this.add(filesMenu);

		diskMenu = new JMenu(); 
		diskMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		diskMenu.setText(Translation.getInstance().getLabel("all_56")); 
		diskMenu.setMnemonic(Translation.getInstance().getLabel("all_28").charAt(0));
		simdiskMngMenuItem = new JMenuItem(); 
		simdiskMngMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		simdiskMngMenuItem.setText(Translation.getInstance().getLabel("all_57"));
		simdiskMngMenuItem.setActionCommand("mngd");
		simdiskMngMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		simdiskMngMenuItem.addActionListener(this); 
		diskMenu.add(simdiskMngMenuItem); 

		this.add(diskMenu);

		helpMenu = new JMenu(); 
		helpMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		helpMenu.setText(Translation.getInstance().getLabel("all_61"));
		helpMenu.setMnemonic(Translation.getInstance().getLabel("all_33").charAt(0));
		helpMenuItem = new JMenuItem(); 
		helpMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		helpMenuItem.setText(Translation.getInstance().getLabel("all_61"));
		helpMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
		helpMenuItem.setActionCommand("help");
		helpMenuItem.addActionListener(this); 
		helpMenu.add(helpMenuItem); 

		// Submenu samples
		samplesMenu = new JMenu();
		samplesMenu.setFont(new Font(Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 12));
		samplesMenu.setText(Translation.getInstance().getLabel("all_62"));

		samplesSch = new JMenuItem();
		samplesSch.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 12));
		samplesSch.setText(Translation.getInstance().getLabel("all_53"));
		samplesSch.setActionCommand("samplesSch");
		samplesSch.addActionListener(this); 
		samplesMenu.add(samplesSch);

		samplesMem = new JMenuItem();
		samplesMem.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 12));
		samplesMem.setText(Translation.getInstance().getLabel("all_55"));
		samplesMem.setActionCommand("samplesMem");
		samplesMem.addActionListener(this); 
		samplesMenu.add(samplesMem);

		samplesFs = new JMenuItem();
		samplesFs.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 12));
		samplesFs.setText(Translation.getInstance().getLabel("all_59"));
		samplesFs.setActionCommand("samplesFs");
		samplesFs.addActionListener(this); 
		samplesMenu.add(samplesFs);

		samplesDisk = new JMenuItem();
		samplesDisk.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 12));
		samplesDisk.setText(Translation.getInstance().getLabel("all_57"));
		samplesDisk.setActionCommand("samplesDisk");
		samplesDisk.addActionListener(this); 
		samplesMenu.add(samplesDisk);

		helpMenu.add(samplesMenu); 

		// Submenu exercises
		exercisesMenu = new JMenu(); 
		exercisesMenu.setFont(new Font(Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 12));
		exercisesMenu.setText(Translation.getInstance().getLabel("all_63"));

		exercisesSch = new JMenuItem();
		exercisesSch.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 12));
		exercisesSch.setText(Translation.getInstance().getLabel("all_53"));
		exercisesSch.setActionCommand("exercisesSch");
		exercisesSch.addActionListener(this); 
		exercisesMenu.add(exercisesSch);

		exercisesMem = new JMenuItem();
		exercisesMem.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 12));
		exercisesMem.setText(Translation.getInstance().getLabel("all_55"));
		exercisesMem.setActionCommand("exercisesMem");
		exercisesMem.addActionListener(this); 
		exercisesMenu.add(exercisesMem);

		exercisesFs = new JMenuItem();
		exercisesFs.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 12));
		exercisesFs.setText(Translation.getInstance().getLabel("all_59"));
		exercisesFs.setActionCommand("exercisesFs");
		exercisesFs.addActionListener(this); 
		exercisesMenu.add(exercisesFs);

		exercisesDisk = new JMenuItem();
		exercisesDisk.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 12));
		exercisesDisk.setText(Translation.getInstance().getLabel("all_57"));
		exercisesDisk.setActionCommand("exercisesDisk");
		exercisesDisk.addActionListener(this); 
		exercisesMenu.add(exercisesDisk);


		helpMenu.add(exercisesMenu); 

		helpMenu.addSeparator();
		aboutMenuItem = new JMenuItem(); 
		aboutMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		aboutMenuItem.setText(Translation.getInstance().getLabel("all_60"));
		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		aboutMenuItem.setActionCommand("about");
		aboutMenuItem.addActionListener(this); 
		helpMenu.add(aboutMenuItem); 
		this.add(helpMenu); 

		this.add(Box.createHorizontalGlue());
		lang = new JLabel(AppSession.getInstance().getIdioma().getLanguage());
		this.add(lang);
		this.add(new JLabel(" "));

		ben = new JButton(Functions.getInstance().createImageIcon("en.gif"));
		ben.setPreferredSize(new Dimension(22,15));
		ben.setActionCommand("en");
		ben.addActionListener(this);
		this.add(ben);
		ben.setVisible(false);
		this.add(new JLabel(" "));

		bca = new JButton(Functions.getInstance().createImageIcon("ca.gif"));
		bca.setPreferredSize(new Dimension(22,15));
		bca.setActionCommand("ca");
		bca.addActionListener(this);
		this.add(bca);
		this.add(new JLabel(" "));

		bes = new JButton(Functions.getInstance().createImageIcon("es.gif"));
		bes.setPreferredSize(new Dimension(22,15));
		bes.setActionCommand("es");
		bes.addActionListener(this);
		this.add(bes);
		this.add(new JLabel(" "));
	} 

	/**
	 * Manage events generated by menu items<br/>
	 * 
	 * @param e	action event
	 */
	public void actionPerformed(ActionEvent e) {
		String scroll = "";
		int action = actions.get(e.getActionCommand()).intValue();
		// The first confirm accion
		boolean doIt = true;
		if (action == 1 || 
				(AppSession.getInstance().getPresenter() != null  && (action == 2 || action == 4 || action >= 20))) {
			if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(AppSession.getInstance().getApp().getComponent(), Translation.getInstance().getError("all_08"), "Warning", JOptionPane.OK_CANCEL_OPTION)) {
				doIt = false;	
			}
		}
		if (doIt) {
			switch (action) {
			case 1:	// exit 
				System.exit(0);
				break;
			case 2:	// open
				OpenSaveDialog open = new OpenSaveDialog(AppSession.getInstance().getApp().getComponent());
				File returnFile = open.showOpenFileChooser();
				if (returnFile != null) { 
					// Load's document root
					try {
						Functions.getInstance().openSimulation(returnFile.toURI().toURL());
					} catch (SoSimException ex) {
						JOptionPane.showMessageDialog(AppSession.getInstance().getApp().getComponent(),ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(AppSession.getInstance().getApp().getComponent(),ex.toString(),"Error",JOptionPane.ERROR_MESSAGE);
					}
				}
				break;
			case 3:	// save
				if (AppSession.getInstance().getPresenter() != null) AppSession.getInstance().getPresenter().actionPerformed(e);
				break;
			case 4:	// home
				if (AppSession.getInstance().getPresenter() != null)  AppSession.getInstance().getPresenter().closeInfo();
				AppSession.getInstance().setPresenter(null);
				saveMenuItem.setEnabled(false);
				AppSession.getInstance().getApp().loadView(new Home(this));
				break;
			case 5:	// about
				AppSession.getInstance().getApp().showMessage("<html>OS Sim v1.2<br/> Operating System Concepts Simulator<br/>Freely available for all uses under BSD license<br/>Alex Macia</html>");
				break;
			case 6:	// help
				HelpDialog helpPanel = new HelpDialog(AppSession.getInstance().getApp().getComponent(), "all_61", Functions.getInstance().getPropertyString("help_file"), "index", HELP_WIDTH, HELP_HEIGHT);
				helpPanel.setVisible(false);
				helpPanel.scrollToKey("index");
				break;

				// Language	

			case 10:	// en
			case 11:	// ca
			case 12:	// es
				ben.setVisible(action!=10);
				bca.setVisible(action!=11);
				bes.setVisible(action!=12);

				AppSession.getInstance().setIdioma(new Locale(e.getActionCommand()));
				lang.setText(AppSession.getInstance().getIdioma().getLanguage());
				Translation.getInstance().setLanguage();
				AppSession.getInstance().getLangNotifier().notifyObservers();
				break;	

				// Simulations

			case 20:	// Scheduler
				if (AppSession.getInstance().getPresenter() != null)  AppSession.getInstance().getPresenter().closeInfo();
				AppSession.getInstance().setPresenter(new ProcessPresenter(true));
				break;	
			case 30:	// Memory 
				if (AppSession.getInstance().getPresenter() != null)  AppSession.getInstance().getPresenter().closeInfo();
				AppSession.getInstance().setPresenter(new MemoryPresenter(true));
				break;	
			case 40:	// Disk
				if (AppSession.getInstance().getPresenter() != null)  AppSession.getInstance().getPresenter().closeInfo();
				AppSession.getInstance().setPresenter(new DiskPresenter(true));
				break;	
			case 50:	// File system
				if (AppSession.getInstance().getPresenter() != null)  AppSession.getInstance().getPresenter().closeInfo();
				AppSession.getInstance().setPresenter(new FileSystemPresenter(true));
				break;	

			case 60:	// samples
			case 61:
			case 62:
			case 63:

				if (action == 60) scroll = "scheduling_examples";
				if (action == 61) scroll = "memory_examples";
				if (action == 62) scroll = "filesystem_examples";
				if (action == 63) scroll = "disk_examples";

				HelpDialog samplesPanel = new HelpDialog(AppSession.getInstance().getApp().getComponent(), "all_61", Functions.getInstance().getPropertyString("help_file"), scroll, HELP_WIDTH, HELP_HEIGHT);
				samplesPanel.setVisible(false);
				samplesPanel.scrollToKey(scroll);
				break;
			case 70:	// exercises
			case 71:
			case 72:
			case 73:

				if (action == 70) scroll = "scheduling_exercises";
				if (action == 71) scroll = "memory_exercises";
				if (action == 72) scroll = "filesystem_exercises";
				if (action == 73) scroll = "disk_exercises";

				HelpDialog exercisesPanel = new HelpDialog(AppSession.getInstance().getApp().getComponent(), "all_61", Functions.getInstance().getPropertyString("help_file"), scroll, HELP_WIDTH, HELP_HEIGHT);
				exercisesPanel.setVisible(false);
				exercisesPanel.scrollToKey(scroll);
				break;
			}

			if (AppSession.getInstance().getPresenter() != null 
					&& !saveMenuItem.isEnabled() 
					&& AppSession.getInstance().getApp().allowOpenSave()) saveMenuItem.setEnabled(true);
		}
	}

	/**
	 * Translate menu
	 * 
	 * @param o		observable
	 * @param arg	unused
	 */
	public void update(Observable o, Object arg) {
		fileMenu.setText(Translation.getInstance().getLabel("all_50")); 
		fileMenu.setMnemonic(Translation.getInstance().getLabel("all_20").charAt(0));
		exitMenuItem.setText(Translation.getInstance().getLabel("all_51")); 
		openMenuItem.setText(Translation.getInstance().getLabel("all_12")); // Open simulation
		saveMenuItem.setText(Translation.getInstance().getLabel("all_13"));
		homeMenuItem.setText(Translation.getInstance().getLabel("all_14")); // Home screen
		processMenu.setText(Translation.getInstance().getLabel("all_52")); 
		processMenu.setMnemonic(Translation.getInstance().getLabel("all_24").charAt(0));
		simProcsMenuItem.setText(Translation.getInstance().getLabel("all_53"));
		memoryMenu.setText(Translation.getInstance().getLabel("all_54")); 
		memoryMenu.setMnemonic(Translation.getInstance().getLabel("all_26").charAt(0));
		simMemMenuItem.setText(Translation.getInstance().getLabel("all_55"));
		diskMenu.setText(Translation.getInstance().getLabel("all_56")); 
		diskMenu.setMnemonic(Translation.getInstance().getLabel("all_28").charAt(0));
		simdiskMngMenuItem.setText(Translation.getInstance().getLabel("all_57"));
		filesMenu.setText(Translation.getInstance().getLabel("all_58")); 
		filesMenu.setMnemonic(Translation.getInstance().getLabel("all_30").charAt(0));
		simFilesMenuItem.setText(Translation.getInstance().getLabel("all_59"));
		helpMenu.setText(Translation.getInstance().getLabel("all_61"));
		helpMenu.setMnemonic(Translation.getInstance().getLabel("all_33").charAt(0));
		helpMenuItem.setText(Translation.getInstance().getLabel("all_61"));
		aboutMenuItem.setText(Translation.getInstance().getLabel("all_60"));

		samplesMenu.setText(Translation.getInstance().getLabel("all_62"));
		samplesSch.setText(Translation.getInstance().getLabel("all_53"));
		samplesMem.setText(Translation.getInstance().getLabel("all_55"));
		samplesFs.setText(Translation.getInstance().getLabel("all_59"));
		samplesDisk.setText(Translation.getInstance().getLabel("all_57"));
		exercisesMenu.setText(Translation.getInstance().getLabel("all_63"));
		exercisesSch.setText(Translation.getInstance().getLabel("all_53"));
		exercisesMem.setText(Translation.getInstance().getLabel("all_55"));
		exercisesFs.setText(Translation.getInstance().getLabel("all_59"));
		exercisesDisk.setText(Translation.getInstance().getLabel("all_57"));

	}
}