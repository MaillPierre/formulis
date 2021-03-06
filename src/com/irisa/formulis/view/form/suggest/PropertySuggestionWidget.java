package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.DataUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;

public class PropertySuggestionWidget extends AbstractSuggestionWidget {

	public PropertySuggestionWidget(FormElement d, AbstractFormulisWidget fParent) {
		super(d, fParent);
		this.setSuggestionOnly(true);
	}

	@Override
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
		fireSuggestionSelection(event);
	}

	@Override
	public void addSuggestionToOracle(Increment inc) {
//		ControlUtils.debugMessage("PropertySuggestionWidget addSuggestionToOracle " + inc);
		if(checkIncrement(inc)) {
			this.oracle.add(new Suggestion(inc));
		}
	}

	@Override
	public void setOracleSuggestions(Collection<Increment> c) {
//		ControlUtils.debugMessage("PropertySuggestionWidget setOracleSuggestions " + c );
		LinkedList<Suggestion> suggs = new LinkedList<Suggestion>();
		Iterator<Increment> itInc = c.iterator();
		while(itInc.hasNext()) {
			Increment inc = itInc.next();
			if(checkIncrement(inc)) {
				suggs.add(new Suggestion(inc));
			}
		}
		this.oracle.setSuggestions(suggs);
	}
	
	private boolean checkIncrement(Increment inc) {
		boolean result = inc.getKind() == KIND.PROPERTY || inc.getKind() == KIND.RELATION;
		if(result) {
			FormElement formElem = null;
			try {
				formElem = DataUtils.extractPropertyFromIncrement(inc);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
			result = result && formElem != null && formElem instanceof URI && ! ControlUtils.FORBIDDEN_URIS.isForbidden(((URI) formElem).getUri());
		}
		return result;
	}

}
