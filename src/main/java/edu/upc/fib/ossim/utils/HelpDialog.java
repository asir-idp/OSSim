package edu.upc.fib.ossim.utils;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import java.awt.Dialog;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.upc.fib.ossim.AppSession;

/**
 * Shows a Dialog that loads a html file (containing help, examples and exercises) and manage its internal links (anchors) 
 * 
 * @author Alex Macia
 */
public class HelpDialog extends EscapeDialog implements HyperlinkListener { 
	private static final long serialVersionUID = 1L;
	private JEditorPane editorPane;
	private JScrollPane editorScrollPane;
	private JEditorPane detailPane;
	private JScrollPane detailScrollPane; 
	private JSplitPane splitPn;
	private int width;
	private int height;
	private Component parent;
	
	/**
	 * Constructs a HelpDialog and loads a html file. Url's identifying file contains a reference to an anchor
	 * Help files are located into <code>/help/(locale)<code> folder, file load depends on current session language     
	 * 
	 * @param parent	container
	 * @param keyTitle	dialog title
	 * @param keyHelp	file name
	 * @param keyRef	anchor name
	 * @param width		dialog width
	 * @param height	dialog height
	 */
	public HelpDialog(Component parent, String keyTitle, String keyHelp, String keyRef, int width, int height) { 
		this.setTitle(Translation.getInstance().getLabel(keyTitle));
		this.parent = parent;
		this.width = width;
		this.height = height;
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		splitPn = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		//pn.setLayout(new BoxLayout(pn, BoxLayout.PAGE_AXIS));
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(this);

		try {
			editorPane.setPage(Functions.getInstance().getHelpURL(keyHelp, keyRef));
			
			editorScrollPane = new JScrollPane(editorPane);
			editorScrollPane.setPreferredSize(new Dimension(width, height));
			splitPn.add(editorScrollPane);
			
			JPanel pn = new JPanel();
			pn.setLayout(new BoxLayout(pn, BoxLayout.PAGE_AXIS));
			pn.add(splitPn);
			this.setContentPane(pn);
			this.pack();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,Translation.getInstance().getError("all_06"),"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Scrolls html file to anchor defined at construction time   
	 * 
	 * @param key	anchor name
	 */
	public void scrollToKey(String key) {
		this.setLocationRelativeTo(parent);
		editorPane.scrollToReference(key);
		editorPane.revalidate();
		this.setVisible(true);
	}

	/**
	 * Manage link events  
	 * 
	 * @param e	link event
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            URL url = e.getURL();
            if (url != null) {
            	String ref = url.getRef();
            	if (ref != null && ref.length() > 0) {
                	// If associated resources, loads resource. Else scroll internal reference
                	String resource = Functions.getInstance().getResource(ref);
                	if (resource != null) {
                		if (resource.endsWith("xml")) {  // Load simulation
                			loadSimulation(resource);
                		} else { // Show explanation (html resource)
                			openDetail(resource);
                		}
                	} else { // Scroll event
                		scrollToKey(ref);
                	}
                } else {
                	// Opens external resource
                	externalResource(url);
                }
            }
        }
	}
	
	private void loadSimulation(String resource) {
		this.dispose();
		if (AppSession.getInstance().getPresenter() != null) 
			AppSession.getInstance().getPresenter().closeInfo();
		
		try {
			Functions.getInstance().openSimulation(resource);
		} catch (SoSimException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(AppSession.getInstance().getApp().getComponent(),ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(AppSession.getInstance().getApp().getComponent(),ex.toString(),"Error",JOptionPane.ERROR_MESSAGE);
		}     
	}
	
	private void openDetail(String resource) {
		try {
			if (detailPane == null) {
				detailPane = new JEditorPane();
    			detailPane.setEditable(false);
    			detailPane.addHyperlinkListener(this);
    			detailScrollPane = new JScrollPane(detailPane);
    			detailScrollPane.setPreferredSize(new Dimension(width, height/2));
				
    			editorScrollPane.setPreferredSize(new Dimension(width, height/2));
    			
    			splitPn.add(detailScrollPane);
				this.pack();
			}
			
			detailPane.setPage(Functions.getInstance().getResourceURL(resource));
			this.pack();
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(parent,Translation.getInstance().getError("all_06"),"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void externalResource(URL url) {
		Desktop d;
		if (AppSession.getInstance().getApp().allowOpenSave()) {
			d = Desktop.getDesktop();
			if(Desktop.isDesktopSupported()) { // Comprovar suport SO
				try {
					d.browse(url.toURI());
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(parent,Translation.getInstance().getError("all_04"),"Error",JOptionPane.ERROR_MESSAGE);
				}
			} else JOptionPane.showMessageDialog(parent,Translation.getInstance().getError("all_05"),"Error",JOptionPane.ERROR_MESSAGE);
		} else JOptionPane.showMessageDialog(parent,Translation.getInstance().getError("all_10"),"Error",JOptionPane.ERROR_MESSAGE);
	}
} 
