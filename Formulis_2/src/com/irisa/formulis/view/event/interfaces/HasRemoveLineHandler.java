package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.RemoveLineEvent;

public interface HasRemoveLineHandler {
	
	public void addRemoveLineHandler(RemoveLineHandler handler);
	public void fireRemoveLineEvent(RemoveLineEvent event);
	public void fireRemoveLineEvent();
	
}
