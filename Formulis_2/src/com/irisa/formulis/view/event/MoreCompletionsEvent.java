package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

/**
 * Demande de suggestions complémentaires
 * @author pmaillot
 *
 */
public class MoreCompletionsEvent extends FormEvent {
	
	private MoreCompletionsEvent(CustomSuggestionWidget src) {
		super(src);
	}

	public MoreCompletionsEvent(Widget src, SuggestionCallback cb) {
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
