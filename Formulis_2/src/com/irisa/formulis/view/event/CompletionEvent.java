package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;

public abstract class CompletionEvent extends FormEvent {

	public CompletionEvent(Widget src) {
		super(src);
	}

	public CompletionEvent(Widget src, SuggestionCallback suggestionCallback) {
		super(src, suggestionCallback);
	}
	
	@Override
	public SuggestionCallback getCallback() {
		return (SuggestionCallback) super.getCallback();
	}
}
