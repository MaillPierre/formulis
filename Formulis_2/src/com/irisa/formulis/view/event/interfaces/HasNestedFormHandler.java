package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.NestedFormEvent;

public interface HasNestedFormHandler {
	
	public void addNestedFormHandler(NestedFormHandler handler);
	public void fireNestedFormEvent(NestedFormEvent event);
	public void fireNestedFormEvent();

}
