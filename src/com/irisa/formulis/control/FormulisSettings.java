package com.irisa.formulis.control;

/**
 * Access to the application settings, such as server adress
 * @author pmaillot
 *
 */
public class FormulisSettings {

	private static String serverAdress = "http://127.0.0.1:9999/"; // Server to send the SEWELIS request
	private static int maxDisplayedClass = 3; // Maximum number of classes to display in a form
	
	public static String getServerAdress() {
		return serverAdress;
	}
	
	public static void setServerAdress(String adress) {
		serverAdress = adress;
	}

}
