package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.ElementCreationEvent;
import com.irisa.formulis.view.event.LessCompletionsEvent;
import com.irisa.formulis.view.event.MoreCompletionsEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.interfaces.CompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.ElementCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasCompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.HasElementCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasLessCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.HasMoreCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.HasSuggestionSelectionHandler;
import com.irisa.formulis.view.event.interfaces.LessCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.MoreCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.SuggestionSelectionHandler;
import com.irisa.formulis.view.form.FormEventCallback;
import com.irisa.formulis.view.form.FormRelationLineWidget;

public class VariableSuggestionWidget extends AbstractSuggestionWidget {
	
//	private boolean moreCompletionMode = false;
	
	public VariableSuggestionWidget(FormRelationLineWidget par) {
		super(null, par );
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
		if(inc.getKind() == KIND.ENTITY || inc.getKind() == KIND.PROPERTY || inc.getKind() == KIND.CLASS || inc.getKind() == KIND.SOMETHING) {
			this.oracle.add(new Suggestion(inc));
		}
	}
	
	public void addAllSuggestionToOracle(Collection<Increment> c) {
		Iterator<Increment> itSugg = c.iterator();
		while(itSugg.hasNext()) {
			Increment inc = itSugg.next();
			this.addSuggestionToOracle(inc);
		}
	}
	
	public void setOracleSuggestions(Collection<Increment> c) {
		LinkedList<Suggestion> suggs = new LinkedList<Suggestion>();
		Iterator<Increment> itInc = c.iterator();
		while(itInc.hasNext()) {
			Increment inc = itInc.next();
			if(inc.getKind() == KIND.ENTITY || inc.getKind() == KIND.PROPERTY || inc.getKind() == KIND.CLASS || inc.getKind() == KIND.SOMETHING) {
				suggs.add(new Suggestion(inc));
			}
		}
		this.oracle.setSuggestions(suggs);
	}
	
	/**
	 * 
	 * @return un callback à appeler des que les suggestions sont prètes
	 */
	public SuggestionCallback getLineSelectionCompletionsCallback() {
		return new SuggestionCallback(this) {
			@Override
			public void call(Controller control) {
				this.source.fireCompletionAskedEvent();
			}
		};
	}
	
	/**
	 * 
	 * @return un callback pour les evenement visant à remplacer les suggestions existantes par celle en mémoire
	 */
	public SuggestionCallback getSetCallback() {
		return new SuggestionCallback(this){
			@Override
			public void call(Controller control) {
//				ControlUtils.debugMessage("CustomSuggestionWidget SetCallback call");
//				Collection<Increment> increments = control.getPlace().getSuggestions().getEntitySuggestions();
//				this.source.oracle.clear();
//				Iterator<Increment> itInc = increments.iterator();
//				while(itInc.hasNext()) {
//					Increment inc = itInc.next();
//					this.source.oracle.add(new CustomSuggestion(inc));
//				}
//				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
//				ControlUtils.debugMessage("CustomSuggestionWidget SetCallback call END");

				if(control.getPlace().getCurrentCompletions() != null) {
					source.setOracleSuggestions(control.getPlace().getCurrentCompletions());

					waitingFor = false;
				}
				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
				source.showSuggestions();
			}
		};
	}

	/**
	 * 
	 * @return un callback pour les evenement visant à ajouter des suggestions aux existantes
	 */
	public SuggestionCallback getAddCallback() {
		return new SuggestionCallback(this){
			@Override
			public void call(Controller control) {
//				Collection<Increment> increments = control.getPlace().getSuggestions().getEntitySuggestions();
//				Iterator<Increment> itInc = increments.iterator();
//				while(itInc.hasNext()) {
//					Increment inc = itInc.next();
//					this.source.oracle.add(new CustomSuggestion(inc));
//				}
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
