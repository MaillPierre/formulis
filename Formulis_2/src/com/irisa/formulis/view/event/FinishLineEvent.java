package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.FormLineWidget;

public class FinishLineEvent extends FormEvent {

	private boolean finishState = false;
	
	public FinishLineEvent(FormLineWidget src, boolean state) {
		super(src);
		finishState = state;
	}
	
	public boolean getState() {
		return finishState;
	}

}
