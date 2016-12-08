package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.ElementCreationEvent;

public interface HasElementCreationHandler {

	public void addElementCreationHandler(ElementCreationHandler handler);
	public void fireElementCreationEvent(String value);
	public void fireElementCreationEvent(ElementCreationEvent event);
	
}
