package com.irisa.formulis.view.event;

import com.irisa.formulis.view.event.callback.ActionCallback;
import com.irisa.formulis.view.form.FormWidget;

public class FinishFormEvent extends FormEvent {

	private boolean finishState = false;
	
	public FinishFormEvent(FormWidget src, boolean state) {
		super(src);
		finishState = state;
	}
	
	public FinishFormEvent(FormWidget src, boolean state, ActionCallback formEventCallback) {
		super(src, formEventCallback);
		finishState = state;
	}
	
	public boolean getState() {
		return finishState;
	}

}
