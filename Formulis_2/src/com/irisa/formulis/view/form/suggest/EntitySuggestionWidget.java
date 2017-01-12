package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.form.FormClassLineWidget;

public class EntitySuggestionWidget extends AbstractSuggestionWidget {

	public EntitySuggestionWidget(FormElement d, FormClassLineWidget fParent) {
		super(d, fParent);
	}

	@Override
	public void addSuggestionToOracle(Increment inc) {
		ControlUtils.debugMessage("EntitySuggestionWidget addSuggestionToOracle" + inc);
		if(inc.getKind() == KIND.ENTITY ) {
			this.oracle.add(new Suggestion(inc));
		}
	}

	@Override
	public void addAllSuggestionToOracle(Collection<Increment> c) {
		ControlUtils.debugMessage("EntitySuggestionWidget addAllSuggestionToOracle " + c);
		Iterator<Increment> itSugg = c.iterator();
		while(itSugg.hasNext()) {
			Increment inc = itSugg.next();
			this.addSuggestionToOracle(inc);
		}
	}

	@Override
	public void setOracleSuggestions(Collection<Increment> c) {
		ControlUtils.debugMessage("EntitySuggestionWidget setOracleSuggestions " + c);
		LinkedList<Suggestion> suggs = new LinkedList<Suggestion>();
		Iterator<Increment> itInc = c.iterator();
		while(itInc.hasNext()) {
			Increment inc = itInc.next();
//			if(inc.getKind() == KIND.ENTITY) {
				suggs.add(new Suggestion(inc));
//			}
		}
		this.oracle.setSuggestions(suggs);
	}

	@Override
	public SuggestionCallback getLineSelectionCompletionsCallback() {
//		ControlUtils.debugMessage("EntitySuggestionWidget getLineSelectionCompletionsCallback");
//		return new SuggestionCallback(this) {
//			@Override
//			public void call(Controller control) {
//				this.source.fireCompletionAskedEvent();
//			}
//		};
		return new SuggestionCallback(this) {
			@Override
			public void call(Controller control) {
				ControlUtils.debugMessage("EntitySuggestionWidget getLineSelectionCompletionsCallback call");
				this.source.fireCompletionAskedEvent();				
			}
		};
	}
	
	public SuggestionCallback getReturnCompletionsAfterStatChangeCallback() {
		return new SuggestionCallback(this) {
			@Override
			public void call(Controller control) {
				this.source.fireCompletionAskedEvent();
			}
		};
	}

	@Override
	public SuggestionCallback getSetCallback() {
//		ControlUtils.debugMessage("EntitySuggestionWidget getSetCallback");
		return new SuggestionCallback(this){
			@Override
			public void call(Controller control) {
				ControlUtils.debugMessage("EntitySuggestionWidget getSetCallback call currentCompletions: " + control.getPlace().getCurrentCompletions());
				if(control.getPlace().getCurrentCompletions() != null) {
					source.setOracleSuggestions(control.getPlace().getCurrentCompletions());

					waitingFor = false;
				}
				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
				source.showSuggestions();
			}
		};
	}

	@Override
	public SuggestionCallback getAddCallback() {
		ControlUtils.debugMessage("EntitySuggestionWidget getAddCallback");
		return new SuggestionCallback(this){
			@Override
			public void call(Controller control) {
				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
//				this.source.setMoreCompletionMode(! control.getPlace().hasMore()); // TODO gestion des "More" a ajouter pour relachement suggestion

				if(control.getPlace().getCurrentCompletions() != null) {
					source.setOracleSuggestions(control.getPlace().getCurrentCompletions());
					source.showSuggestions();
				}
			}
		};
	}

}
