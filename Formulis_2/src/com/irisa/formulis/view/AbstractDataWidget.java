package com.irisa.formulis.view;

import com.google.gwt.user.client.ui.Composite;
import com.irisa.formulis.model.form.FormElement;

public abstract class AbstractDataWidget extends Composite {
	
	protected FormElement data;

	public AbstractDataWidget(FormElement d) {
		this.data = d;
	}
	
	public FormElement getData() {
		return data;
	}
}
