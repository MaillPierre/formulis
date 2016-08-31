package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.FormWidget;

public class TypeLineSetEvent extends FormEvent {

	public TypeLineSetEvent(FormWidget src) {
		super(src);
	}

	@Override
	public FormWidget getSource() {
		return (FormWidget) super.getSource();
	}
	
}
