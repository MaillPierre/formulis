package com.irisa.formulis.view.create;

import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;

public abstract class AbstractCreateWidget extends AbstractDataWidget {

	public AbstractCreateWidget(FormElement d) {
		super(d);
	}
	
	public abstract void setStartingValue(String value);

}
