package edu.upc.fib.ossim.utils;

import java.util.ResourceBundle;

import edu.upc.fib.ossim.AppSession;


/**
 * Singleton to manage language translation through ResourceBundle class.
 * Translations bundle files are located into <code>/lang</code> folder.
 * There are two different translation bundles types: labels and error, 
 * and one bundle file per type/language
 *    
 * @author Alex Macia
 * 
 * @see ResourceBundle
 */
public class Translation {
	private static Translation instance = null;
	private ResourceBundle labels;
	private ResourceBundle errors;

	private static final String PATH = "edu.upc.fib.ossim.lang.";
		
	private Translation() {
		setLanguage();
	}
	
	/**
	 * Public access to Singleton
	 * 
	 * @return instance
	 */
	public static Translation getInstance() {
		if(instance == null) {
			instance = new Translation();
		}
		return instance;
	}
	
	/**
	 * Sets bundle to session's language 
	 */
	public void setLanguage() {
		labels = ResourceBundle.getBundle(PATH + "LabelsBundle", AppSession.getInstance().getIdioma());
		errors = ResourceBundle.getBundle(PATH + "ErrorsBundle", AppSession.getInstance().getIdioma());
	}

	/**
	 * Returns label from current label's bundle identified by a key
	 * 
	 * @param key		label's key
	 * @return return 	label associated to the key 
	 */
	public String getLabel(String key) {
		return labels.getString(key);
	}

	/**
	 * Returns label from current label's bundle identified by a key and replace "p1" with parameter 
	 * 
	 * @param key		label's key
	 * @return return 	label associated to the key replacing p1 with param
	 */
	public String getLabel(String key, int param) {
		String label = labels.getString(key); 
		return label.replaceFirst("p1", new Integer(param).toString());
	}

	/**
	 * Returns label from current label's bundle identified by a key and replace "p1" with parameter and
	 * replace "p2" with label identified by keyparam2   
	 * 
	 * @param key		label's key
	 * @param keyparam1	first param's 
	 * @param keyparam2	second param's label key
	 * @return return 	label associated to the key replacing p1 with param1 and p2  with label associated to keyparam2
	 */
	public String getLabel(String key, int param1, String keyparam2) {
		String label = labels.getString(key);
		String param2 = labels.getString(keyparam2);
		
		label = label.replaceFirst("p1", new Integer(param1).toString());
		return label.replaceFirst("p2", param2);
	}
	
	/**
	 * Returns label from current label's bundle identified by a key and replace "p1" with label identified by keyparam 
	 * 
	 * @param key		label's key
	 * @param keyparam	Param's label key
	 * @return return 	label associated to the key replacing p1 with label associated to keyparam 
	 */
	public String getLabel(String key, String keyparam) {
		String label = labels.getString(key); 
		String param = labels.getString(keyparam);
		return label.replaceFirst("p1", param);
	}

	/**
	 * Returns label from current label's bundle identified by a key, replace "p1" with label identified by keyparam1 and
	 * replace "p2" with label identified by keyparam2   
	 * 
	 * @param key		label's key
	 * @param keyparam1	first param's label key
	 * @param keyparam2	second param's label key
	 * @return return 	label associated to the key replacing p1 with label associated to keyparam1 and p2  with label associated to keyparam2
	 */
	public String getLabel(String key, String keyparam1, String keyparam2) {
		String label = labels.getString(key); 
		String param1 = labels.getString(keyparam1);
		String param2 = labels.getString(keyparam2);
		
		label = label.replaceFirst("p1", param1);
		return label.replaceFirst("p2", param2);
	}


	/**
	 * Returns error from current error's bundle identified by a key
	 * 
	 * @param key		error's key
	 * @return return 	error associated to the key 
	 */
	public String getError(String key) {
		return errors.getString(key);
	}
}
