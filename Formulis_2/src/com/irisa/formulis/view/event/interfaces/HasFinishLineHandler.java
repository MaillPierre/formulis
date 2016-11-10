package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.FinishableLineEvent;

public interface HasFinishLineHandler {
	
	public void addFinishLineHandler(FinishLineHandler handler);
	public void fireFinishLineEvent(FinishableLineEvent event);
	public void fireFinishableLineEvent(boolean state);

}
