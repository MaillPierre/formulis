package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.form.AbstractFormLineWidget;

public class ClassSuggestionWidget extends AbstractSuggestionWidget {

	public ClassSuggestionWidget(FormElement d, AbstractFormulisWidget fParent) {
		super(d, fParent);
		this.setSuggestionOnly(true);
	}

	@Override
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
		fireSuggestionSelection(event);
	}

	@Override
	public void addSuggestionToOracle(Increment inc) {
		if(inc.getKind() == KIND.CLASS && ! ControlUtils.FORBIDDEN_URIS.isForbidden(((URI) inc.getDisplayElement()).getUri()) ) {
			this.oracle.add(new Suggestion(inc));
		}
	}

	@Override
	public void setOracleSuggestions(Collection<Increment> c) {
		LinkedList<Suggestion> suggs = new LinkedList<Suggestion>();
		Iterator<Increment> itInc = c.iterator();
		while(itInc.hasNext()) {
			Increment inc = itInc.next();
			ControlUtils.debugMessage("ClassSuggestionWidget setOracleSuggestions inc:"+ inc);
			if(inc.getKind() == KIND.CLASS  && ! ControlUtils.FORBIDDEN_URIS.isForbidden(((URI) inc.getDisplayElement()).getUri()) ) {
				suggs.add(new Suggestion(inc));
			}
		}
		this.oracle.setSuggestions(suggs);
	}

}
