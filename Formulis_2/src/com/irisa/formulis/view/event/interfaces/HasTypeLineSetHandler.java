package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.TypeLineSetEvent;

public interface HasTypeLineSetHandler {
	
	public void addTypeLineSetHandler(TypeLineSetHandler handler);
	public void fireTypeLineSetEvent(TypeLineSetEvent event);
	public void fireTypeLineSetEvent();
}
