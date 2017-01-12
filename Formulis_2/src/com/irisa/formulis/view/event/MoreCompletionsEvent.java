package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;
import com.irisa.formulis.view.form.suggest.SuggestionWidget;

/**
 * Demande de suggestions complémentaires
 * @author pmaillot
 *
 */
public class MoreCompletionsEvent extends CompletionEvent {
	
	public MoreCompletionsEvent(SuggestionWidget src) {
		super(src);
	}

	public MoreCompletionsEvent(Widget widget, SuggestionCallback cb) {
		super(widget, cb);
	}
	
}
