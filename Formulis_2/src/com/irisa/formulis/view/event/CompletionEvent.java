package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;

public abstract class CompletionEvent extends FormEvent {

	public CompletionEvent(Widget src) {
		super(src);
	}

	public CompletionEvent(Widget src, com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback suggestionCallback) {
		super(src, suggestionCallback);
	}

	@Override
	public AbstractSuggestionWidget getSource() {
		return (AbstractSuggestionWidget)super.getSource(); 
	}
	
	@Override
	public SuggestionCallback getCallback() {
		return (SuggestionCallback) super.getCallback();
	}
}
