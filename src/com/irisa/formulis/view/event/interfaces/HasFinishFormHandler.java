package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.FinishFormEvent;
import com.irisa.formulis.view.event.callback.ActionCallback;

public interface HasFinishFormHandler {
	
	public void addFinishFormHandler(FinishFormHandler handler);
	public void fireFinishFormEvent(FinishFormEvent event);
	public void fireFinishFormEvent(boolean state);
	public void fireFinishFormEvent(boolean state, ActionCallback callback);
}
