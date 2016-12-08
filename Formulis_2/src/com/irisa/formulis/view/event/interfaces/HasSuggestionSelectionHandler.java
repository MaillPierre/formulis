package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.SuggestionSelectionEvent;

public interface HasSuggestionSelectionHandler {

	public void fireSuggestionSelection(SuggestionSelectionEvent event);
	public void addSuggestionSelectionHandler(SuggestionSelectionHandler handler);
	
}
