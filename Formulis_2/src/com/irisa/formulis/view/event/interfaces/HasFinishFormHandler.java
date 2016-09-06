package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.FinishFormEvent;

public interface HasFinishFormHandler {
	
	public void addFinishFormHandler(FinishFormHandler handler);
	public void fireFinishFormEvent(FinishFormEvent event);
	public void fireFinishFormEvent();
}
