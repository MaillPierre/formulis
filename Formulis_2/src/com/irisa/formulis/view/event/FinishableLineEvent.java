package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.FormLineWidget;

public class FinishableLineEvent extends FormEvent {

	private boolean finishState = false;
	
	public FinishableLineEvent(FormLineWidget src, boolean state) {
		super(src);
		finishState = state;
	}
	
	public boolean getState() {
		return finishState;
	}

}
