package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.AbstractFormElementWidget;

/**
 * Nouveau formulaire à ajouter en fin d'une ligne
 */
public class NestedFormEvent extends FormEvent {

	public NestedFormEvent(AbstractFormElementWidget src) {
		super(src);
	}

	@Override
	public String toString() {
		return "Event: NestedForm from " + source;
	}

}
