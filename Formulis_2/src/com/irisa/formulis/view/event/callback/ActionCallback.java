package com.irisa.formulis.view.event.callback;

import com.irisa.formulis.control.Controller;
import com.irisa.formulis.view.event.FormEvent;

public interface ActionCallback extends FormEventCallback {

	/**
	 * Call the right functions in the controller
	 * @param control
	 */
	public void call();
	
}
