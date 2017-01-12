package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;
import com.irisa.formulis.view.form.suggest.SuggestionWidget;

public abstract class CompletionEvent extends FormEvent {

	public CompletionEvent(Widget src) {
		super(src);
	}

	public CompletionEvent(Widget src, com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback suggestionCallback) {
		super(src, suggestionCallback);
	}

	@Override
	public SuggestionWidget getSource() {
		return (SuggestionWidget)super.getSource(); 
	}
	
	@Override
	public SuggestionCallback getCallback() {
		return (SuggestionCallback) super.getCallback();
	}
}
