package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.HistoryEvent;

public interface HasHistoryHandler {

	public void addHistoryHandler(HistoryHandler handler);
	public void fireHistoryEvent(HistoryEvent event);
	public void fireHistoryEvent();
}
