package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.LineSelectionEvent;

public interface HasLineSelectionHandler {
	
	public void addLineSelectionHandler(LineSelectionHandler handler);
	public void fireLineSelectionEvent(LineSelectionEvent event);
	public void fireLineSelectionEvent();
	
}
