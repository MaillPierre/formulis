package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.form.FormRelationLineWidget;

public class VariableSuggestionWidget extends AbstractSuggestionWidget {
	
//	private boolean moreCompletionMode = false;
	
	public VariableSuggestionWidget(FormRelationLineWidget par) {
		super(null, par );
	}

	@Override
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
		fireSuggestionSelection(event);
	}
	
	public void onKeyDown(KeyDownEvent event) {
		super.onKeyDown(event); 
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			if(element.getText() != "") {
				fireElementCreationEvent(element.getText());
				this.popover.hide();
			}
		}
	}
	
	public void addSuggestionToOracle(Increment inc) {
		if(inc.getKind() == KIND.ENTITY 
				|| inc.getKind() == KIND.SOMETHING 
				|| inc.getKind() == KIND.LITERAL) {
			this.oracle.add(new Suggestion(inc));
		}
	}
	
	public void setOracleSuggestions(Collection<Increment> c) {
		LinkedList<Suggestion> suggs = new LinkedList<Suggestion>();
		Iterator<Increment> itInc = c.iterator();
		while(itInc.hasNext()) {
			Increment inc = itInc.next();
			if(inc.getKind() == KIND.ENTITY 
					|| inc.getKind() == KIND.SOMETHING 
					|| inc.getKind() == KIND.LITERAL) {
				suggs.add(new Suggestion(inc));
			}
		}
		this.oracle.setSuggestions(suggs);
	}

}
