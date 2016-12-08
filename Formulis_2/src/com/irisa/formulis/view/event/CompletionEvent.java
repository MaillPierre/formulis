package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

public abstract class CompletionEvent extends FormEvent {

	public CompletionEvent(Widget src) {
		super(src);
	}

	public CompletionEvent(Widget src, SuggestionCallback cb) {
		super(src, cb);
	}

	@Override
	public CustomSuggestionWidget getSource() {
		return (CustomSuggestionWidget)super.getSource(); 
	}
	
	@Override
	public SuggestionCallback getCallback() {
		return (SuggestionCallback) super.getCallback();
	}
}
