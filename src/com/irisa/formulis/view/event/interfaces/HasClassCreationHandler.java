package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.ClassCreationEvent;

public interface HasClassCreationHandler {
	
	public void addClassCreationHandler(ClassCreationHandler handler);
	public void fireClassCreationEvent();
	public void fireClassCreationEvent(ClassCreationEvent event);
	
}
