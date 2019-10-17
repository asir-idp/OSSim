package edu.upc.fib.ossim.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * Renders a table cell, setting background color, centering horizontal alignment and creating a blue border when row containing the cell is selected.  
 * 
 * @see ColorCell
 * 
 * @author Alex Macia
 * 
 */
public class ColorRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	private Border selectedBorder;
	private Border unselectedBorder;
	
	/**
	 * Constructs a ColorRender setting cell's opaque property to true 
	 * 
	 */
	public ColorRenderer() {
		setOpaque(true); //MUST do this for background to show up.
	}

	/**
	 * Returns cell component rendered: background color, alignment (center) and blue border if is selected 
	 * 
	 * @param table			cell's table to render
	 * @param color			cell's background color
	 * @param isSelected	true if cell is selected
	 * @param hasFocus		cell's focus	
	 * @param row			cell's row
	 * @param column		cell's column			 		 
	 * 
	 * @return Component rendered 
	 */
	public Component getTableCellRendererComponent(JTable table, Object color,	boolean isSelected, boolean hasFocus, int row, int column) {
		ColorCell cell = (ColorCell) color;
		setBackground(cell.getColor());
		setText((String) cell.getValue());
		setHorizontalAlignment(SwingConstants.CENTER);
		if (isSelected) {
			if (selectedBorder == null) selectedBorder = BorderFactory.createMatteBorder(1,1,1,1,table.getSelectionBackground());
            setBorder(selectedBorder);
            setForeground(Color.BLUE);
        } else {
        	if (unselectedBorder == null) unselectedBorder = BorderFactory.createMatteBorder(1,1,1,1,table.getBackground());
            setBorder(unselectedBorder);
            setForeground(Color.BLACK);
        }
		return this;
	}
}