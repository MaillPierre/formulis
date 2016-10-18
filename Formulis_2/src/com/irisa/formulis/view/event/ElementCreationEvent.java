package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.FormLineWidget;

public class ElementCreationEvent extends FormEvent {

	protected String val;
	
	public ElementCreationEvent(FormLineWidget src, String value) {
		super(src);
		val = value;
	}
	
	@Override
	public FormLineWidget getSource() {
		return (FormLineWidget) super.getSource();
	}
	
	public String getValue() {
		return val;
	}

}
