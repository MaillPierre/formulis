package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.AbstractFormLineWidget;

public class FinishableLineEvent extends FormEvent {

	private boolean finishState = false;
	
	public FinishableLineEvent(AbstractFormLineWidget src, boolean state) {
		super(src);
		finishState = state;
	}
	
	public boolean getState() {
		return finishState;
	}

}
