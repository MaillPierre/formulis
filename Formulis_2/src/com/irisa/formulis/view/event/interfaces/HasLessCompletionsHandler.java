package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.LessCompletionsEvent;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

public interface HasLessCompletionsHandler {
	
	public void addLessCompletionsHandler(LessCompletionsHandler handler);
	public void fireLessCompletionsEvent(LessCompletionsEvent event);
	public void fireLessCompletionsEvent(SuggestionCallback cb);
	
}
