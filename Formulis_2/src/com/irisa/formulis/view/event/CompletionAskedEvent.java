package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget;

/**
 * Demande de complétions
 */
public class CompletionAskedEvent extends CompletionEvent {
	
	public String search = "";
	
	public CompletionAskedEvent(Widget widget, CustomSuggestionWidget.SuggestionCallback cb) {
		super(widget, cb);
	}
	
	public CompletionAskedEvent(Widget src, CustomSuggestionWidget.SuggestionCallback cb, String srch) {
		super(src, cb);
		search = srch;
	}
	
	public String getSearch() {
		return search;
	}

	@Override
	public String toString() {
		return "CompletionAskedEvent from " + source;
	}

}
