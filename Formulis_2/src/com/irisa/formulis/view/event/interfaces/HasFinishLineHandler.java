package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.FinishLineEvent;

public interface HasFinishLineHandler {
	
	public void addFinishLineHandler(FinishLineHandler handler);
	public void fireFinishLineEvent(FinishLineEvent event);
	public void fireFinishLineEvent(boolean state);

}
