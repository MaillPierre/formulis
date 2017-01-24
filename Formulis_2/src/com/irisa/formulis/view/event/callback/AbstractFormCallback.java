package com.irisa.formulis.view.event.callback;

import com.irisa.formulis.model.Statement;
import com.irisa.formulis.model.form.Form;

public abstract class AbstractFormCallback extends Object implements ObjectCallback {

	@Override
	public void call(Object object) {
		if(object instanceof Form) {
			call((Statement)object);
		} else {
			throw new IllegalArgumentException("Expecting a Statement for this callback");
		}
	}
	
	public abstract void call(Form stat);

}
