package com.irisa.formulis.view.event;

import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.callback.ActionCallback;
import com.irisa.formulis.view.event.callback.FormEventCallback;

/**
 * Changement de statement, affichage des changements dans le formulaire
 * @author pmaillot
 *
 */
public class StatementChangedEvent extends AbstractFormEvent {
	
	public StatementChangedEvent(AbstractFormulisWidget src) {
		super(src);
	}

	public StatementChangedEvent(AbstractFormulisWidget src, FormEventCallback formEventCallback) {
		super(src, formEventCallback);
	}
	
	@Override
	public String toString() {
		return "Event: StatementChange from " + source;
	}

}
