package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.FormLineWidget;

public class ElementCreationEvent extends FormEvent {

	public ElementCreationEvent(FormLineWidget src) {
		super(src);
	}
	
	@Override
	public FormLineWidget getSource() {
		return (FormLineWidget) super.getSource();
	}

}
