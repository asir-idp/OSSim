package edu.upc.fib.ossim.utils;

/**
 * Application concrete exception. Translate error message depending on current session language  
 * 
 * @author Alex Macia
 */
public class SoSimException extends Exception {
	private static final long serialVersionUID = 1L;	
	private String key;
	
	/**
	 * Constructs exception translating error message.
	 * Error keys reference to strings and depends on current session language  
	 * 
	 * @param key	reference
	 */
	public SoSimException(String key) {
		super(Translation.getInstance().getError(key));
		this.key = key;
	}

	/**
	 * Constructs exception translating error message and adding additional information.
	 * Error keys reference to strings and depends on current application language  
	 * 
	 * @param key		reference
	 * @param moreInfo	additional exception information 
	 */
	public SoSimException(String key, String moreInfo) {
		super(Translation.getInstance().getError(key) + moreInfo);
		this.key = key;
	}
	
	/**
	 * Getter key
	 * 
	 * @return key
	 */
	public String getKey() {
		return key;
	}
}
