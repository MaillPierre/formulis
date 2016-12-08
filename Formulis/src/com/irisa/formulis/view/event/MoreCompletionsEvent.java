package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

/**
 * Demande de suggestions complémentaires
 * @author pmaillot
 *
 */
public class MoreCompletionsEvent extends CompletionEvent {
	
	public MoreCompletionsEvent(CustomSuggestionWidget src) {
		super(src);
	}

	public MoreCompletionsEvent(Widget widget, SuggestionCallback cb) {
		super(widget, cb);
	}
	
}
