package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.MoreCompletionsEvent;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

public interface HasMoreCompletionsHandler {

	public void addMoreCompletionsHandler(MoreCompletionsHandler handler);
	public void fireMoreCompletionsEvent(MoreCompletionsEvent event);
	public void fireMoreCompletionsEvent(SuggestionCallback cb);
}
