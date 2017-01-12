package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;
import com.irisa.formulis.view.form.suggest.VariableSuggestionWidget;

/**
 * Event pour la gestion de clicks sur des widgets en interne puisque apparement on ne peux pas recréer des clickevent manuellement
 * @author pmaillot
 *
 */
public class ClickWidgetEvent {
	
	private Widget source;
	private SuggestionCallback callback = null;

	public ClickWidgetEvent(Widget src) {
		this.source = src;
	}
	
	public ClickWidgetEvent(AbstractSuggestionWidget src, SuggestionCallback cb) {
		this(src);
		callback = cb;
	}

	public Widget getSource() {
		return source;
	}
	
	public SuggestionCallback getCallback() {
		return this.callback;
	}
 	
}
