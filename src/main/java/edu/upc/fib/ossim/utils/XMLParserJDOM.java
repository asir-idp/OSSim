package edu.upc.fib.ossim.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * XML parser that uses JDOM. Persistence is managed through <code>xml</code> files.
 * This parser manage all reading/writing operations that load/save <code>xml</code> files containing simulations 
 * 
 * @author Alex Macia
 */
public class XMLParserJDOM {
	private URL file;
	private Document doc;
	
	/**
	 * Constructs Document structure for writing
	 * 
	 * @param file	file to save
	 * @param sroot	root element value
	 */
	public XMLParserJDOM(URL file, String sroot) {
		try {
			this.file = new URL(file.toString().replaceAll("%20"," "));
			Element root = new Element(sroot);
			doc = new Document(root);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs Document structure for reading from a file
	 * 
	 * @param file				file to load	
	 * @throws SoSimException	I/O problems reading file
	 */
	public XMLParserJDOM(URL file) throws SoSimException {
		this.file = file;
		doc = readXmlFile();
	}
	
	/**
	 * Getter root element value
	 * 
	 * @return	document's root element value
	 */
	public String getRoot() {
		return doc.getRootElement().getName();
	}
	
	/**
	 * Returns xml data structure from parent node. <br/> 
	 * Data Structure is: <br/>
	 * <code>child elements x attributes x 2 rows --> (name, value)</code>
	 *   	 
	 * @param sparent	parent node value	
	 * @return	xml data from parent
	 * 
	 * @throws SoSimException	parent doesn't exist
	 */
	@SuppressWarnings("unchecked")
	public Vector<Vector<Vector<String>>> getElements(String sparent) throws SoSimException {
		// Return Data elements x attributes x 2  rows --> (name, value) from sparent  
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();
		
		Element parent = findNode(doc.getRootElement(), sparent);
		
		if (parent != null) {
			List children = parent.getChildren();
			Iterator itc = children.iterator();
			while (itc.hasNext()) {
				Vector<Vector<String>> object = new Vector<Vector<String>>();
				Vector<String> fileAttribute = new Vector<String>();

				Element child = (Element) itc.next(); // Model Objects
				// XML Attribute is first attribute (identifier) object 
				fileAttribute.add(((Attribute) child.getAttributes().get(0)).getName());
				fileAttribute.add(((Attribute) child.getAttributes().get(0)).getValue());
				object.add(fileAttribute);
				
				List attrs = child.getChildren(); // Attibutes
				Iterator ito = attrs.iterator();
				while (ito.hasNext()) {
					Element att = (Element) ito.next(); // Attribute object
					fileAttribute = new Vector<String>();
					fileAttribute.add(att.getName());
					fileAttribute.add(att.getText());
					object.add(fileAttribute);
				}
				
				data.add(object);
			}
			return data;
		}
		throw new SoSimException("all_04");
	}

	/**
	 * Adds data structure to a node (parent). <br/>  
	 * Data Structure is: <br/>
	 * <code>child elements x attributes x 2 rows --> (name, value)</code>
	 * 
	 * @param sparent	parent's element value 
	 * @param data		data structure
	 */
	public void addElements(String sparent, Vector<Vector<Vector<String>>> data) {
		// Data elements x attributes x 2  rows --> (name, value) 
		if (data != null) {
			for (int i = 0; i<data.size();i++) {
				// Element i
				Vector<Vector<String>> element = data.get(i);
				Element xmlobject = addElement(sparent, "object", null);
				xmlobject.setAttribute(element.get(0).get(0), element.get(0).get(1));
				for (int j = 1; j<element.size();j++) { // First attribute identifies object
					// Attribute j
					addElement(xmlobject, element.get(j).get(0), element.get(j).get(1));
				}
			}
		}
	}
	
	/**
	 * Adds single child element to parent and returns it
	 * 
	 * @param sparent 	parent's element value
	 * @param name		element name
	 * @param value		element value
	 * 
	 * @return child or null if parent doesn't exist	
	 */
	public Element addElement (String sparent, String name, String value) {
		Element parent = findNode(doc.getRootElement(), sparent);
		
		if (parent != null) {
			return addElement(parent, name, value);
		}
		return null; 
	}

	private Element addElement (Element parent, String name, String value) {
		Element element = null;
		
		element = new Element(name);
		if (value != null) element.setText(value);
		parent.addContent(element);

		return element; 
	}

	/**
	 * Finds node identified by name and returns (Recursive method)  
	 * 
	 * @param current	current element
	 * @param name		wanted element identifier
	 * @return	node identified by name
	 */
	@SuppressWarnings("unchecked")
	private Element findNode(Element current, String name) {
		if (current == null) return null;
		if (current.getName().equals(name)) return current;
		List children = current.getChildren();
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			Element child = (Element) iterator.next();
			Element node = findNode(child, name);
			if (node != null) return node;
		}
		return null;
	}

	/**
	 * Writes file containing document structure 
	 * 
	 * @throws SoSimException	I/O problems writing file
	 */
	public void writeXmlFile() throws SoSimException {
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		FileWriter writer = null;
		try {
			writer = new FileWriter(file.getFile());
			outputter.output(doc, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SoSimException("all_03");
		} 
	}
	
	/**
	 * Reads <code>xml</code> file an returns document contained
	 * 
	 * @return	document associated with file
	 * @throws SoSimException 	I/O (or <code>xml</code> validation) problems reading file 
	 */
	private Document readXmlFile() throws SoSimException  {
		SAXBuilder builder = new SAXBuilder(false);	// Turn off validation
		// command line should offer URIs or file names
		try {
			doc = builder.build(file);
			return doc;
		} catch (Exception e) {
			throw new SoSimException("all_04");
		}
	}
}
