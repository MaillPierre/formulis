package com.irisa.formulis.view.event.callback;

public interface StringCallback extends ObjectCallback {
	/**
	 * @param description contains the server answer to be parsed 
	 */
	public void call(String description);
}