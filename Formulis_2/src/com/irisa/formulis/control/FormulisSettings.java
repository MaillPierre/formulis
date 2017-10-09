package com.irisa.formulis.control;

public class FormulisSettings {

	private static String serverAdress = "http://127.0.0.1:9999/";
	
	public static String getServerAdress() {
		return serverAdress;
	}
	
	public static void setServerAdress(String adress) {
		serverAdress = adress;
	}

}
