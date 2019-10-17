package edu.upc.fib.ossim.filesystem.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;


/**
 * Paints Storage Device (Secondary Memory Device). File System objects (files, folders and links) 
 * are allocated into memory or released from it, device painter shows all device blocks, block may be free
 * (available) or may contain piece of a file system object, then it is highlighted using that object's color.
 * 
 * @author Alex Macia
 */
public class DevicePainter extends PainterTemplate {
		
	private static final long serialVersionUID = 1L;	
	private static final int ROW_UNITS = 64;
	private static final int BLOCK_HEIGTH = 16;
	public static final int HEIGHT = 1044; // - TOPMARGIN - BOTTOM_MARGIN Min 512 o multiples
	public static final int WIDTH = 	552; // - LEFTMARGIN - RIGHT_MARGIN Min 512 o multiples
	private static final int LEFT_MARGIN = 35;
	private static final int RIGHT_MARGIN = 5;
	private static final int TOP_MARGIN = 10;
	private static final int BOTTOM_MARGIN = 10;
	private double colwidth;
	private double rowheight;
	private int blockxrow;
	private String admStr;
	
	/**
	 * Constructs a DevicePainter.  
	 * 
	 * @param presenter	event manager
	 * @param admStr	text shown over administrative purpose blocks (at device's beginning)		
	 */
	public DevicePainter(Presenter presenter, String admStr) {
		super(presenter, WIDTH, HEIGHT);
		this.admStr = admStr;
		this.removeMouseListener(presenter);
	}
	
	/**
	 * Draws device blocks as a grid, every block is a colored rectangles, 
	 * when it contains a piece of a file system object it is highlighted using that object's color.
	 * When memory size height exceeds canvas height, memory enlarge and revalidates to perform scroll update
	 * 
	 * @param g	graphic context
	 */
	public void paint(Graphics g) {
		g2 = (Graphics2D) g;
		Dimension size = getSize();
		int w = (int)size.getWidth();
		int h = (int)size.getHeight();
		
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, w, h);
		
		g2.fillRect(LEFT_MARGIN, TOP_MARGIN, w-LEFT_MARGIN - RIGHT_MARGIN, h-TOP_MARGIN-BOTTOM_MARGIN);
		
		map.clear();

		int diskSize = ((FileSystemPresenter) presenter).getDiskSize();
		int blockSize = ((FileSystemPresenter) presenter).getBlockSize();
		int rows = diskSize/ROW_UNITS;

		int newHeight = BLOCK_HEIGTH*rows + TOP_MARGIN + BOTTOM_MARGIN;
		setPreferredSize(new Dimension(w, newHeight));
		revalidate(); // Updates scroll 
		
		blockxrow = ROW_UNITS/blockSize; // Block in every row 
		colwidth = (w-LEFT_MARGIN-RIGHT_MARGIN) / blockxrow;  // block's width
		//rowheight = (h-TOP_MARGIN-BOTTOM_MARGIN) / rows; // row height. Constant
		rowheight = BLOCK_HEIGTH;
		
		g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 10));
		g2.setColor(Color.BLACK);
		//g2.drawString("@0", 0, TOP_MARGIN + 5);
		
		// Init device
		for (int i=0; i< diskSize/blockSize; i++) {
			drawBlock(i, ((FileSystemPresenter) presenter).getBlockString(i), ((FileSystemPresenter) presenter).getBlockColor(i)); 
		}

		// Init addresses
		for (int i=0; i<= rows; i += 2) {  // Every 2 rows, shows address
			int y = (int) (TOP_MARGIN + 7 + rowheight*i);
			g2.drawString("@" + i*ROW_UNITS, 0, y); 
		}

		g2.setColor(Color.WHITE);
		g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		g2.drawString(admStr, LEFT_MARGIN + 20, TOP_MARGIN + 20);
	}
	
	private void drawBlock(int b, String s, Color color) {
		
		int row = b / blockxrow;
		int col = b % blockxrow;
        double x = LEFT_MARGIN + col*colwidth;
        double y = TOP_MARGIN + row*rowheight;
        
        Rectangle2D.Double block = new Rectangle2D.Double(x,y,colwidth,rowheight);
		
        // Solid 
        g2.setColor(color);
		g2.fill(block);
		
		// Bound
		g2.setColor(Color.BLACK);
		g2.draw(block);
		
		g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		//g2.drawString(s, (int) (x + colwidth/2), (int) (y + rowheight/2));
		//g2.drawString(s, (int) x+2, (int) y+2);
		g2.drawString(s, (int) x+2, (int) (y + rowheight));
	}

	/**
 	 * Returns false. No need of event control over this device       
	 * 
	 * @param o 	unused
	 * @param x	x 	unused
	 * @param y	y 	unused
	 * 
	 * @return	false 
	 */
	public boolean contains(Object o, int x, int y){
		return false;
	}
}


