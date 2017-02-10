package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.form.AbstractFormLineWidget;

public class ClassSuggestionWidget extends AbstractSuggestionWidget {

	public ClassSuggestionWidget(FormElement d, AbstractFormLineWidget fParent) {
		super(d, fParent);
	}

	@Override
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
		fireSuggestionSelection(event);
	}

	@Override
	public void addSuggestionToOracle(Increment inc) {
		if(inc.getKind() == KIND.CLASS) {
			this.oracle.add(new Suggestion(inc));
		}
	}

	@Override
	public void setOracleSuggestions(Collection<Increment> c) {
		LinkedList<Suggestion> suggs = new LinkedList<Suggestion>();
		Iterator<Increment> itInc = c.iterator();
		while(itInc.hasNext()) {
			Increment inc = itInc.next();
			if(inc.getKind() == KIND.CLASS) {
				suggs.add(new Suggestion(inc));
			}
		}
		this.oracle.setSuggestions(suggs);
	}

}
