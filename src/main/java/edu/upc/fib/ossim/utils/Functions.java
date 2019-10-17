package edu.upc.fib.ossim.utils;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.process.ProcessPresenter;

/**
 * Singleton class with miscellaneous utility functions	
 * 
 * @author Alex Macia
 */
public class Functions {
	private static Functions instance = null;
	private static final String ROOT="/edu/upc/fib/ossim/";
	private static final String PATH = "/edu/upc/fib/ossim/img/";
	private static final String RESOURCES="/edu/upc/fib/ossim/help/resources";
		
	private Functions() { }

	/**
	 * Public access to Singleton
	 * 
	 * @return instance
	 */
	public static Functions getInstance() {
		if(instance == null) {
			instance = new Functions();
		}
		return instance;
	}
	
	/**
	 * Returns help resource (URL format) according to current session language 
	 * 
	 * @param helpFile	file name (without language)
	 * @param reference	reference inside help file to scroll
	 * 
	 * @return help resource (URL format) according to current session language 
	 * 
	 * @throws MalformedURLException
	 */
	public URL getHelpURL(String helpFile, String reference) throws MalformedURLException {
		String file = ROOT + helpFile + "_" + AppSession.getInstance().getIdioma().getLanguage() +  ".htm";
		URL helpURL = getClass().getResource(file);
		URL url = new URL(helpURL + "#" + reference); // Add reference to scroll
		return url;
	}
	
	/**
	 * Returns resource file URL (At resources folder)
	 * 
	 * @param resourceFile	file name
	 * 
	 * @return resource file URL  
	 */
	public URL getResourceURL(String resourceFile) {
		String file = RESOURCES + "/" + resourceFile;
		return getClass().getResource(file);
	}
	
	/**
	 * Returns a resource identified by key from resources file. If resource contains \<lang\> it is 
	 * replace by session language   
	 * 
	 * @param key 	resource identifier
	 * @return	resource identified by key from resources file  
	 * 
	 * @see resources.list
	 */
	public String getResource(String key) {
		String resource = AppSession.getInstance().getResources().getProperty(key);
		if (resource != null) return resource.replaceFirst("<lang>", AppSession.getInstance().getIdioma().getLanguage());
		return null;
	}
	
	/**
	 * Returns an application property identified by key from properties file  
	 * 
	 * @param key 	property identifier
	 * @return	property identified by key from properties file  
	 * 
	 * @see ossim.properties
	 */
	public String getPropertyString(String key) {
		return AppSession.getInstance().getProperties().getProperty(key);
	}

	/**
	 * Returns a numeric application property identified by key from properties file  
	 * 
	 * @param key 	property identifier
	 * @return	numeric property identified by key from properties file  
	 * 
	 * @see ossim.properties
	 */
	public int getPropertyInteger(String key) {
		Integer property;
		try {
			property = new Integer(AppSession.getInstance().getProperties().getProperty(key));
		} catch (NumberFormatException e) {
			property = 0;
		}
		return property.intValue();
	}
	
	
	/**
	 * Opens a simulation from an xml file
	 * 
	 * @param file			xml file
	 * @throws SoSimException	xml validation or format error 
	 */
	public void openSimulation(URL file) throws SoSimException {
		XMLParserJDOM parser = new XMLParserJDOM(file);
		String sroot = parser.getRoot();
		if 	(!sroot.equals(getPropertyString("xml_root_pro")) 	&&
				!sroot.equals(getPropertyString("xml_root_mem")) 	&& 
				!sroot.equals(getPropertyString("xml_root_fs"))  	&&	
				!sroot.equals(getPropertyString("xml_root_disk")))
			throw new SoSimException("all_04");

		if (sroot.equals(getPropertyString("xml_root_pro"))) 
			AppSession.getInstance().setPresenter(new ProcessPresenter(false)); 
		if (sroot.equals(getPropertyString("xml_root_mem"))) 
			AppSession.getInstance().setPresenter(new MemoryPresenter(false));
		if (sroot.equals(getPropertyString("xml_root_disk"))) 
			AppSession.getInstance().setPresenter(new DiskPresenter(false)); 
		if (sroot.equals(getPropertyString("xml_root_fs")))
			AppSession.getInstance().setPresenter(new FileSystemPresenter(false));
		
		AppSession.getInstance().getPresenter().loadXML(file);	// Load file
		AppSession.getInstance().getPresenter().updateInfo(); // Update table info
		AppSession.getInstance().getPresenter().repaintPainters(); // Repaint painters 
	}
	
	/**
	 * Opens file and loads simulation 
	 * 
	 * @param file			xml file
	 * @throws Exception	xml validation or format error
	 * 
	 * @see openSimulation(File)
	 *  
	 */
	public void openSimulation(String file) throws SoSimException {
		try {
			URL url = getResourceURL(file);
			
			openSimulation(url);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SoSimException("all_04");
		}
	}
		
	/**
	 * Returns an ImageIcon from a file image 
	 * 
	 * @param name	File's name located into <code>/img</code> folder 
	 * @return	ImageIcon created from file or null if error	
	 */
	public ImageIcon createImageIcon(String name) {
		URL imgURL = getClass().getResource(PATH+name);
		
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.out.println("Couldn't find icon: " + PATH + name);
			return null;
		}
	}
	
	/**
	 * Returns an BufferedImage from a file image
	 * 
	 * @param name	File's name located into <code>/img</code> folder 
	 * @return	BufferedImage created from file or null if error	
	 */
	public BufferedImage loadImage(String name) {
		URL url =  getClass().getResource(PATH+name);
		BufferedImage img = null;
		try {
			img =  ImageIO.read(url);
		} catch (Exception e) {
			System.out.println("Couldn't find image: " + e.getMessage());
		}
		return img;
	}
	
	/**
	 * Draws a white rectangle filled with a colored texture   
	 * 
	 * @param g2		current application graphics state
	 * @param oval		rectangle texture color 
	 * @param x			x position
	 * @param y			y position
	 * @param width		rectangle width 
	 * @param height	rectangle height
	 */
	public void drawTexture(Graphics2D g2, Color oval, int x, int y, int width, int height) {
		BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		Graphics2D big = bi.createGraphics();
		big.setColor(Color.white);
        big.fillRect(0, 0, 7, 7);
        big.setColor(oval);  
        big.fillOval(0, 0, 3, 3);
        TexturePaint f_tp = new TexturePaint(bi, new Rectangle(0,0,5,5));
        big.dispose();
	
		g2.setPaint(f_tp);
		g2.fill(new Rectangle(x,y,width, height));
		
		g2.setColor(Color.black);	
		g2.draw(new Rectangle(x,y,width, height));
	}
	
	/**
	 * Returns max width of a set of Strings in a concrete Font context
	 * 
	 * @param s		String vector
	 * @param frc	font context
	 * @param font	font family
	 * @return		max string's width
	 */
	public int maxStringsWidth(String[] s, FontRenderContext frc, Font font) {
		if (s == null || s.length == 0) return 0; 
		int aux = 0;
		int max = (int) font.getStringBounds(s[0], frc).getBounds2D().getWidth();
		for(int i=1; i<s.length;i++) {
			aux = (int) font.getStringBounds(s[i], frc).getBounds2D().getWidth();
			if (aux > max) max = aux;
		}
		return max;
	}
	
	/**
     * Aligns the first <code>rows</code> * <code>cols</code>
     * components of <code>parent</code> in
     * a grid. Each component in a column is as wide as the maximum
     * preferred width of the components in that column;
     * height is similarly determined for each row.
     * The parent is made just big enough to fit them all.
     *
     * @param parent Container. Layout SpringLayout 		
     * @param rows number of rows
     * @param cols number of columns
     * @param initialX x location to start the grid at
     * @param initialY y location to start the grid at
     * @param xPad x padding between cells
     * @param yPad y padding between cells
     */
	public void makeCompactGrid(Container parent,
			int rows, int cols,
			int initialX, int initialY,
			int xPad, int yPad) {
		SpringLayout layout;
		SpringLayout.Constraints constraints;
		try {
			layout = (SpringLayout)parent.getLayout();
		} catch (ClassCastException exc) {
			System.out.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		//Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			int width = 0;
			for (int r = 0; r < rows; r++) {
				constraints = layout.getConstraints(parent.getComponent(r * cols + c)); 
				width = Spring.max(Spring.constant(width), constraints.getWidth()).getValue();
			}
			for (int r = 0; r < rows; r++) {
				constraints = layout.getConstraints(parent.getComponent(r * cols + c));
				constraints.setX(x);
			}
			x = Spring.sum(x, Spring.sum(Spring.constant(width), Spring.constant(xPad)));
		}

		//Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			int height = 0;
			for (int c = 0; c < cols; c++) {
				constraints = layout.getConstraints(parent.getComponent(r * cols + c));
				height = Spring.max(Spring.constant(height), constraints.getHeight()).getValue();
			}
			for (int c = 0; c < cols; c++) {
				constraints = layout.getConstraints(parent.getComponent(r * cols + c));
				constraints.setY(y);
				//constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(Spring.constant(height), Spring.constant(yPad)));
		}

		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}
}
