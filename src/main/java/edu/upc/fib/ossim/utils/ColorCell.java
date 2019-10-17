package edu.upc.fib.ossim.utils;

import java.awt.Color;

/**
 * Associates cells values with colors to paint background cell colors into application tables  
 *
 * @see ColorRenderer
 *  
 * @author Alex Macia
 * 
 */
public class ColorCell implements Comparable<ColorCell> {
	private String value;
	private Color color;
	
	/**
	 * Constructs a new ColorCell with a value and a background color  
	 * 
	 * @param value	Cell value
	 * @param color	Background cell color
	 */
	public ColorCell(String value, Color color) {
		super();
		this.value = value;
		this.color = color;
	}
	
	/**
	 * Getter of value
	 * 
	 * @return	cell value 
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Getter of color
	 * 
	 * @return	cell background color	
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Comparison between 2 ColorCell objects  
	 * 
	 * @param cc color cell to compare 
	 * 
	 * @return comparison result (compareTo defaults)	
	 */
	public int compareTo(ColorCell cc) {
		try{
			Integer valueToInteger = Integer.parseInt(this.value);
			Integer ccToInteger = Integer.parseInt(cc.getValue());
			return valueToInteger.compareTo(ccToInteger);
		} catch(Exception ex){
			return this.value.compareTo(cc.getValue());
		}  
	}
}
