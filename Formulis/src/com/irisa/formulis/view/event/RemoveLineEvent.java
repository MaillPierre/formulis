package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.FormLineWidget;

public class RemoveLineEvent extends FormEvent {
	
	public RemoveLineEvent(FormLineWidget src) {
		super(src);
	}
	
	@Override
	public FormLineWidget getSource() {
		return (FormLineWidget) super.getSource();
	}

}
