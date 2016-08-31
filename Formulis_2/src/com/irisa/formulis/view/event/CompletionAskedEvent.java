package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget;

/**
 * Demande de complétions
 */
public class CompletionAskedEvent extends FormEvent {
	
	private CustomSuggestionWidget.SuggestionCallback callback;
	
	public CompletionAskedEvent(Widget widget, CustomSuggestionWidget.SuggestionCallback cb) {
		super(widget);
		callback = cb;
	}
	
	@Override
	public CustomSuggestionWidget.SuggestionCallback getCallback() {
		return this.callback;
	}

	@Override
	public String toString() {
		return "Event: CompletionAsked from " + source;
	}

}
