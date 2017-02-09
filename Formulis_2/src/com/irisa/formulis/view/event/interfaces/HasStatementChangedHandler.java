package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.StatementChangedEvent;

public interface HasStatementChangedHandler {

	public void addStatementChangeHandler(StatementChangedHandler handler);
	public void fireStatementChangeEvent();
	public void fireStatementChangeEvent(StatementChangedEvent event);
	
}
