package com.irisa.formulis.view.event;

import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.form.AbstractFormElementWidget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;

/**
 * Selection d'une ligne, changement de statement, mise à jour des données
 */
public class LineSelectionEvent  extends FormEvent  {
	
	public LineSelectionEvent(AbstractFormulisWidget src) {
		super(src);
	}

	public LineSelectionEvent(AbstractFormElementWidget src, SuggestionCallback cb) {
		super(src, cb);
	}

	@Override
	public String toString() {
		return "Event: LineSelection from " + source;
	}
	
}
