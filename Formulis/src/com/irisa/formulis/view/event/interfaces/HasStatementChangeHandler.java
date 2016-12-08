package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.StatementChangeEvent;

public interface HasStatementChangeHandler {

	public void addStatementChangeHandler(StatementChangeHandler handler);
	public void fireStatementChangeEvent();
	public void fireStatementChangeEvent(StatementChangeEvent event);
	
}
