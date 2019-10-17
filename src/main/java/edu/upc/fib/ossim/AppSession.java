package edu.upc.fib.ossim;

import java.util.Locale;
import java.util.Observable;
import java.util.Properties;

import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.utils.ObservableIdioma;

/**
 * Singleton class that manage sessions data: top level container (JFrame or JApplet), 
 * current presenter (Simulation manager), application language, properties files, 
 * resources (examples and exercises)
 * 
 * @author Alex Macia
 */

public class AppSession {
	private static final String RESOURCES_FILE = "/edu/upc/fib/ossim/help/resources.properties";
	private static final String PROPERTIES = "/edu/upc/fib/ossim/ossim.properties";
	private static AppSession instance = null;
	private Locale idioma; // Default
	private Observable langNotifier;
	private OSSim app; // top level container
	private Presenter presenter;
	private Properties resources;	// resources
	private Properties properties;	// properties

	private AppSession() { 
		idioma = new Locale("en");
		langNotifier = new ObservableIdioma();
	}
	
	/**
	 * Public access to Singleton
	 * 
	 * @return instance
	 */
	public static AppSession getInstance() {
		if(instance == null) {
			instance = new AppSession();
		}
		return instance;
	}
	
	/**
	 * Getter Locale language
	 * 
	 * @return Locale
	 */
	public Locale getIdioma() {
		return idioma;
	}
	
	/**
	 * Getter language Notifier
	 * 
	 * @return language Notifier
	 */
	public Observable getLangNotifier() {
		return langNotifier;
	}
	
	/**
	 * Getter top level container
	 * 
	 * @return top level container
	 */
	public OSSim getApp() {
		return app;
	}

	/**
	 * Getter presenter
	 * 
	 * @return presenter
	 */
	public Presenter getPresenter() {
		return presenter;
	}

	/**
	 * Getter resources 
	 * 
	 * @return resources
	 */
	public Properties getResources() {
		if (resources == null) {
			resources = new Properties(); // Initially loads resources (only once)  
			try {
				resources.load(getClass().getResourceAsStream(RESOURCES_FILE));
			} catch (Exception e) {
				// No resources available
			}
		}
		return resources;
	}

	/**
	 * Getter properties 
	 * 
	 * @return properties
	 */
	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties(); // Initially loads properties (only once)  
			try {
				properties.load(getClass().getResourceAsStream(PROPERTIES));
			} catch (Exception e) {
				// No resources available
			}
		}
		return properties;
	}
	
	/**
	 * Setter Locale language
	 * 
	 * @param idioma	Locale
	 */
	public void setIdioma(Locale idioma) {
		this.idioma = idioma;
	}

	/**
	 * Setter top level container
	 * 
	 * @param app top level container
	 */
	public void setApp(OSSim app) {
		this.app = app;
	}
	
	/**
	 * Setter presenter
	 * 
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
