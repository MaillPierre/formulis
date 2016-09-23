package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

public class LessCompletionsEvent extends CompletionEvent {

	public LessCompletionsEvent(Widget src) {
		super(src);
	}

	public LessCompletionsEvent(Widget widget, SuggestionCallback cb) {
		super(widget, cb);
	}

}
