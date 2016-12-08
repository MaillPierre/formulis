package com.irisa.formulis.view.form;

import com.irisa.formulis.control.Controller;

public interface FormEventCallback {

	/**
	 * Call the right functions in the controller
	 * @param control
	 */
	public void call(Controller control);
	
}
