package com.irisa.formulis.view.event;

import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.form.FormEventCallback;

/**
 * Changement de statement, affichage des changements dans le formulaire
 * @author pmaillot
 *
 */
public class StatementChangeEvent extends FormEvent {
	
	public StatementChangeEvent(AbstractFormulisWidget src) {
		super(src);
	}

	public StatementChangeEvent(AbstractFormulisWidget src, FormEventCallback formEventCallback) {
		super(src, formEventCallback);
	}
	
	@Override
	public String toString() {
		return "Event: StatementChange from " + source;
	}

}
