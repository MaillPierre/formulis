package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;

/**
 * Demande de suggestions complémentaires
 * @author pmaillot
 *
 */
public class MoreCompletionsEvent extends CompletionEvent {
	
	public MoreCompletionsEvent(AbstractSuggestionWidget src) {
		super(src);
	}

	public MoreCompletionsEvent(Widget widget, SuggestionCallback cb) {
		super(widget, cb);
	}
	
}
