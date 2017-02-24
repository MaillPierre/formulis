package com.irisa.formulis.view.create;

import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;

/**
 * Base View class for all creation widgets
 * @author pmaillot
 *
 */
public abstract class AbstractCreateWidget extends AbstractDataWidget {

	public AbstractCreateWidget(FormElement d) {
		super(d);
	}
	
	public abstract void setStartingValue(String value);

}
