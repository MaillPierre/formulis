package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;

public class LessCompletionsEvent extends CompletionEvent {

	public LessCompletionsEvent(Widget src, SuggestionCallback formEventCallback) {
		super(src, formEventCallback);
	}

}
