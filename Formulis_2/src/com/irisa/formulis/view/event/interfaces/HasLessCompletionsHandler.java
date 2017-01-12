package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.LessCompletionsEvent;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;

public interface HasLessCompletionsHandler {

	void addLessCompletionsHandler(LessCompletionsHandler handler);

	void fireLessCompletionsEvent(LessCompletionsEvent event);

	void fireLessCompletionsEvent(SuggestionCallback cb);

}
