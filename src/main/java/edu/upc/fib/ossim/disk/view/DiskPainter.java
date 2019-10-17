package edu.upc.fib.ossim.disk.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;


/**
 * Paints a Hard Disk Platter, divided in cylinders and sectors. Sectors requests
 * are colored and disk header (black) moves seeking them, once requests are served 
 * they are not shown any more
 * 
 * @author Alex Macia
 */
public class DiskPainter extends PainterTemplate {
		
	private static final long serialVersionUID = 1L;
	
	private double disk_x;
	private double disk_y;
	private double disk_w;
	private double disk_h;
	private double cylinder_offset;
	// Correction values to keep blocks inside sectors
	private double intcorr_degree;
	private double intcorr_radi;
	private double intcorr_curve;
	double grade; 
	double radi;
	double center_x;
	double center_y;
	
	/**
	 * Constructs a DiskPainter, creates the pop up menu and initialize it 
	 * 
	 * @param presenter	event manager
	 * @param menuItems	pop up menu items
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public DiskPainter(Presenter presenter, Vector<String[]> menuItems, int width, int height) {
		super(presenter, menuItems, width, height);
	}
	
	/**
	 * Draws Hard Disk Platter as a circle and cylinders as circles inside of it,
	 * lines from center to perimeter at different degrees intersects those cylinders, 
	 * shapes resulting are sectors.  
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
		g2.setPaint(Color.BLACK);
		g2.drawRect(0, 0, w-1, h-1);

		setupParameters(w, h);
		
		// Draw cylinders
		for(int i=0; i<= ((DiskPresenter) presenter).getCylinders();i++) {
			g2.draw(new Ellipse2D.Double(disk_x+i*cylinder_offset, disk_y+i*cylinder_offset, disk_w - 2*i *cylinder_offset, disk_h- 2*i *cylinder_offset));
		}

		// Draw sectors
		double grade_a = 0;
		double sin = 0;
		double cos = 0;
		double rad = 0;
		for(int i=0; i<((DiskPresenter) presenter).getSectors();i++) {
			rad = Math.toRadians(grade_a);
			sin = Math.sin(rad);
			cos = Math.cos(rad);
			
			//g2.draw(new Line2D.Double(center_x, center_y, center_x + radi*cos, center_y - radi*sin));
			g2.draw(new Line2D.Double(center_x + radi*cos*0.3, center_y - radi*sin*0.3, center_x + radi*cos, center_y - radi*sin));
			
			grade_a += grade;
		}
		
		// Draw requests
		map.clear();
		drawBlock(((DiskPresenter) presenter).getRunning(), "", Color.BLACK); // Draw head position
		Iterator<Integer> it = presenter.iterator(0);
		while (it.hasNext()) {
			int block = it.next().intValue();
			Color color = presenter.getColor(block);
			drawBlock(block, presenter.getInfo(block).get(0), color);
		}
	}
	
	private void drawBlock(int block, String info, Color c) {
	    g2.setColor(c);
		GeneralPath polyline;

		BlockSquares bs = getBlockSquares(block);
		polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		polyline.moveTo (bs.getPoint(0).x, bs.getPoint(0).y);
		polyline.quadTo(bs.getPoint(1).x, bs.getPoint(1).y, bs.getPoint(2).x, bs.getPoint(2).y);
		polyline.lineTo(bs.getPoint(3).x, bs.getPoint(3).y);
		polyline.quadTo(bs.getPoint(4).x, bs.getPoint(4).y, bs.getPoint(5).x, bs.getPoint(5).y);
		polyline.closePath();

		map.put(polyline, new Integer(block));
		
		g2.draw(polyline);
		g2.fill(polyline);
		
		g2.setColor(Color.BLACK);
		g2.drawString(info, (int) bs.getPoint(4).x, (int) bs.getPoint(4).y);
	}
	
	private void setupParameters(int w, int h) {
		disk_x = 5;
		disk_y = 5;
		disk_w = w-10;
		disk_h = h-10;
		radi = disk_w/2;
		cylinder_offset = (radi*0.7)/ ((DiskPresenter) presenter).getCylinders();
		grade = 360 / ((DiskPresenter) presenter).getSectors(); // p.e. 360 º / 12 sectors = 30 º per sector
		center_x = disk_x + disk_w/2;
		center_y = disk_y + disk_h/2;
		
		intcorr_degree = 0.5;
		intcorr_radi = 1;
		intcorr_curve = 5; 
	}
	
	private BlockSquares getBlockSquares(Integer block) {
		double block_grade;
		double block_rad, bsin, bcos;
		BlockSquares bs = new BlockSquares();
		
		int cil = block / ((DiskPresenter) presenter).getSectors();  // Cylinder
		int sec = block % ((DiskPresenter) presenter).getSectors();  // Sector
		double block_radi = radi - cylinder_offset * cil;
		
		block_grade = 90 - sec * grade;
		block_rad = Math.toRadians(block_grade - intcorr_degree);
		bsin = Math.sin(block_rad);
		bcos = Math.cos(block_rad);
		bs.addPoint(center_x + (block_radi-intcorr_radi)*bcos, center_y - (block_radi-intcorr_radi)*bsin);  // First Point

		block_grade -= grade/2;
		block_rad = Math.toRadians(block_grade);

		bsin = Math.sin(block_rad);
		bcos = Math.cos(block_rad);
		bs.addPoint(center_x + (block_radi+intcorr_curve)*bcos, center_y - (block_radi+intcorr_curve)*bsin); // Curve Point between 1 - 2
	
		block_grade -= grade/2;
		block_rad = Math.toRadians(block_grade + intcorr_degree);
		bsin = Math.sin(block_rad);
		bcos = Math.cos(block_rad);
		bs.addPoint(center_x + (block_radi-intcorr_radi)*bcos, center_y - (block_radi-intcorr_radi)*bsin); // Second Point

		block_radi -= cylinder_offset;
		bs.addPoint(center_x + (block_radi+intcorr_radi)*bcos, center_y - (block_radi+intcorr_radi)*bsin);  // Third Point

		block_grade += grade/2;
		block_rad = Math.toRadians(block_grade + intcorr_degree);
		bsin = Math.sin(block_rad);
		bcos = Math.cos(block_rad);
		bs.addPoint(center_x + (block_radi+intcorr_curve)*bcos, center_y - (block_radi+intcorr_curve)*bsin); // Curve Point between 3 - 4

		block_grade += grade/2;
		block_rad = Math.toRadians(block_grade - intcorr_degree);
		bsin = Math.sin(block_rad);
		bcos = Math.cos(block_rad);
		bs.addPoint(center_x + (block_radi+intcorr_radi)*bcos, center_y - (block_radi+intcorr_radi)*bsin); // Forth Point	
		
		return bs;
	}
	
	/**
 	 * Returns true when any shape representing a sector contains position (x,y) and false otherwise    
	 * 
	 * @param o	shape representing sector
	 * @param x	x position
	 * @param y	y position
	 * 
	 * @return	queued process contains (x,y) 
	 */
	public boolean contains(Object o, int x, int y){
		GeneralPath r = (GeneralPath) o;  
		return r.contains(x, y);
	}
}

class BlockSquares {
	private Vector<Point2D.Double> points;		
	
	public BlockSquares() {
		points = new Vector<Point2D.Double>();
	}
	
	public void addPoint(double x, double y) {
		points.add(new Point2D.Double(x, y));	
	}
	
	public Point2D.Double getPoint(int i) {
		return points.elementAt(i);	
	}
	
	public int getSize() {
		return points.size();	
	}
}
