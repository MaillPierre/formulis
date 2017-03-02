package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.UnexpectedAction;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;
import com.irisa.formulis.view.basic.URIWidget;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.DescribeUriEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.callback.AbstractActionCallback;
import com.irisa.formulis.view.event.callback.ActionCallback;
import com.irisa.formulis.view.event.interfaces.DescribeUriHandler;
import com.irisa.formulis.view.event.interfaces.HasDescribeUriHandler;
import com.irisa.formulis.view.form.FormClassLineWidget;

public class EntitySuggestionWidget extends AbstractSuggestionWidget implements HasDescribeUriHandler {
	
	private LinkedList<DescribeUriHandler> describeUriHandlers = new LinkedList<DescribeUriHandler>();

	public EntitySuggestionWidget(FormElement d, FormClassLineWidget fParent) {
		super(d, fParent);
	}
	
	public void onKeyDown(KeyDownEvent event) {
		super.onKeyDown(event); 
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			if(element.getText() != "") {
				popover.hide();
			}
		}
	}

	@Override
	public void addSuggestionToOracle(Increment inc) {
		ControlUtils.debugMessage("EntitySuggestionWidget addSuggestionToOracle " + inc.getKind());
		if(inc.getKind() == KIND.ENTITY ) {
			this.oracle.add(new Suggestion(inc));
		}
	}

	@Override
	public void setOracleSuggestions(Collection<Increment> c) {
		LinkedList<Suggestion> suggs = new LinkedList<Suggestion>();
		Iterator<Increment> itInc = c.iterator();
		while(itInc.hasNext()) {
			Increment inc = itInc.next();
			if(inc.getKind() == KIND.ENTITY) {
				suggs.add(new Suggestion(inc));
			}
		}
		this.oracle.setSuggestions(suggs);
	}

//	@Override
//	public SuggestionCallback getSetCallback() {
////		ControlUtils.debugMessage("EntitySuggestionWidget getSetCallback");
//		return new SuggestionCallback(this){
//			@Override
//			public void call() {
////				ControlUtils.debugMessage("EntitySuggestionWidget getSetCallback call currentCompletions: " + control.getPlace().getCurrentCompletions());
//				if(Controller.instance().getPlace().getCurrentCompletions() != null) {
//					source.setOracleSuggestions(Controller.instance().getPlace().getCurrentCompletions());
//
//					waitingFor = false;
//				}
//				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
//				source.showSuggestions();
//			}
//		};
//	}
//
//	@Override
//	public SuggestionCallback getAddCallback() {
////		ControlUtils.debugMessage("EntitySuggestionWidget getAddCallback");
//		return new SuggestionCallback(this){
//			@Override
//			public void call() {
//				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
////				this.source.setMoreCompletionMode(! control.getPlace().hasMore()); // TODO gestion des "More" a ajouter pour relachement suggestion
//
//				if(Controller.instance().getPlace().getCurrentCompletions() != null) {
//					source.setOracleSuggestions(Controller.instance().getPlace().getCurrentCompletions());
//					source.showSuggestions();
//				}
//			}
//		};
//	}

	@Override
	public void fireDescribeUriEvent(DescribeUriEvent event) {
//		ControlUtils.debugMessage("EntitySuggestionWidget fireDescribeUriEvent");
		Iterator<DescribeUriHandler> itHand = this.describeUriHandlers.iterator();
		while(itHand.hasNext()) {
			DescribeUriHandler hand = itHand.next();
			hand.onDescribeUri(event);
		}
	}

	@Override
	public void fireDescribeUriEvent(ActionCallback cb, URI uri) {
		fireDescribeUriEvent(new DescribeUriEvent(this.getParentWidget(), cb, uri));
	}

	@Override
	public void addDescribeUriHandler(DescribeUriHandler hand) {
		describeUriHandlers.add(hand);
	}

	@Override
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
		fireSuggestionSelection(event);
	}

}
