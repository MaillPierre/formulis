package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.AbstractFormLineWidget;

public class ElementCreationEvent extends AbstractFormEvent {

	protected String val;
	
	public ElementCreationEvent(AbstractFormLineWidget src, String value) {
		super(src);
		val = value;
	}
	
	@Override
	public AbstractFormLineWidget getSource() {
		return (AbstractFormLineWidget) super.getSource();
	}
	
	public String getValue() {
		return val;
	}

}
