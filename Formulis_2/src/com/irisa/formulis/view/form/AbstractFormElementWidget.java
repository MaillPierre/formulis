package com.irisa.formulis.view.form;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.ElementCreationEvent;
import com.irisa.formulis.view.event.LineSelectionEvent;
import com.irisa.formulis.view.event.MoreCompletionsEvent;
import com.irisa.formulis.view.event.NestedFormEvent;
import com.irisa.formulis.view.event.RelationCreationEvent;
import com.irisa.formulis.view.event.RemoveLineEvent;
import com.irisa.formulis.view.event.StatementChangeEvent;
import com.irisa.formulis.view.event.TypeLineSetEvent;
import com.irisa.formulis.view.event.interfaces.CompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.ElementCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasCompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.HasElementCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasLineSelectionHandler;
import com.irisa.formulis.view.event.interfaces.HasMoreCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.HasNestedFormHandler;
import com.irisa.formulis.view.event.interfaces.HasRelationCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasRemoveLineHandler;
import com.irisa.formulis.view.event.interfaces.HasStatementChangeHandler;
import com.irisa.formulis.view.event.interfaces.HasTypeLineSetHandler;
import com.irisa.formulis.view.event.interfaces.LineSelectionHandler;
import com.irisa.formulis.view.event.interfaces.MoreCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.NestedFormHandler;
import com.irisa.formulis.view.event.interfaces.RelationCreationHandler;
import com.irisa.formulis.view.event.interfaces.RemoveLineHandler;
import com.irisa.formulis.view.event.interfaces.StatementChangeHandler;
import com.irisa.formulis.view.event.interfaces.TypeLineSetHandler;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

public abstract class AbstractFormElementWidget extends AbstractFormulisWidget
		implements HasCompletionAskedHandler, CompletionAskedHandler, ElementCreationHandler, 
		HasElementCreationHandler, HasLineSelectionHandler, LineSelectionHandler, HasMoreCompletionsHandler, MoreCompletionsHandler, HasNestedFormHandler, NestedFormHandler, HasRemoveLineHandler, 
		RemoveLineHandler, HasStatementChangeHandler, StatementChangeHandler, TypeLineSetHandler, HasTypeLineSetHandler, RelationCreationHandler, HasRelationCreationHandler {

	protected LinkedList<CompletionAskedHandler> completionAskedHandlers = new LinkedList<CompletionAskedHandler>();
	protected LinkedList<ElementCreationHandler> elementCreationHandlers = new LinkedList<ElementCreationHandler>();
	protected LinkedList<LineSelectionHandler> lineSelectionHandlers = new LinkedList<LineSelectionHandler>();
	protected LinkedList<MoreCompletionsHandler> moreCompletionsHandlers = new LinkedList<MoreCompletionsHandler>();
	protected LinkedList<NestedFormHandler> nestedFormHandlers = new LinkedList<NestedFormHandler>();
	protected LinkedList<RelationCreationHandler> relationCreationHandlers = new LinkedList<RelationCreationHandler>();
	protected LinkedList<RemoveLineHandler> removeLineHandlers = new LinkedList<RemoveLineHandler>();
	protected LinkedList<StatementChangeHandler> statementChangeHandlers = new LinkedList<StatementChangeHandler>();
	protected LinkedList<TypeLineSetHandler> typeLineSetHandlers = new LinkedList<TypeLineSetHandler>();
	protected boolean profileMode = false;

	public AbstractFormElementWidget(FormElement d, AbstractFormulisWidget fParent) {
		super(d, fParent);
	}
	
	public abstract boolean isSelectedForProfile();
	public abstract void setSelectedForProfile(boolean val);
	
	public void setProfileMode(boolean value) {
		this.profileMode = value;
	}
	
	public void toggleProfileMode() {
		setProfileMode(!this.profileMode);
	}
	
	public boolean isInProfileMode() {
		return this.profileMode;
	}

	@Override
	public void addCompletionAskedHandler(CompletionAskedHandler hand) {
		this.completionAskedHandlers.add(hand);
	}

	@Override
	public void fireCompletionAskedEvent() {
		CompletionAskedEvent event = new CompletionAskedEvent(this, null);
		fireCompletionAskedEvent(event);
	}

	@Override
	public void fireCompletionAskedEvent(CompletionAskedEvent event) {
		Iterator<CompletionAskedHandler> itHand = this.completionAskedHandlers.iterator();
		while(itHand.hasNext()) {
			CompletionAskedHandler hand = itHand.next();
			hand.onCompletionAsked(event);
		}
	}

	@Override
	public void onCompletionAsked(CompletionAskedEvent event) {
		fireCompletionAskedEvent(event);
	}

	@Override
	public void addStatementChangeHandler(StatementChangeHandler handler) {
		this.statementChangeHandlers.add(handler);
	}
	
	@Override
	public void fireStatementChangeEvent() {
		StatementChangeEvent event = new StatementChangeEvent(this);
		fireStatementChangeEvent(event);
	}

	@Override
	public void fireStatementChangeEvent(StatementChangeEvent event) {
		Iterator<StatementChangeHandler> itHand = this.statementChangeHandlers.iterator();
		while(itHand.hasNext()) {
			StatementChangeHandler hand = itHand.next();
			hand.onStatementChange(event);
		}
	}

	@Override
	public void onStatementChange(StatementChangeEvent event) {
		fireStatementChangeEvent(event);
	}

	@Override
	public void addElementCreationHandler(ElementCreationHandler handler) {
		this.elementCreationHandlers.add(handler);
	}

	@Override
	public void fireElementCreationEvent() {
		if(this instanceof FormLineWidget) {
			ElementCreationEvent event = new ElementCreationEvent((FormLineWidget) this);
			fireElementCreationEvent(event);
		}
	}

	@Override
	public void fireElementCreationEvent(ElementCreationEvent event) {
		Iterator<ElementCreationHandler> itHand = this.elementCreationHandlers.iterator();
		while(itHand.hasNext()) {
			ElementCreationHandler hand = itHand.next();
			hand.onElementCreation(event);
		}
	}

	@Override
	public void onElementCreation(ElementCreationEvent event) {
		fireElementCreationEvent(event);
	}

	@Override
	public void addLineSelectionHandler(LineSelectionHandler handler) {
		this.lineSelectionHandlers.add(handler);
	}

	@Override
	public void fireLineSelectionEvent() {
		fireLineSelectionEvent(new LineSelectionEvent(this));
	}

	public void fireLineSelectionEvent(SuggestionCallback callback) {
		fireLineSelectionEvent(new LineSelectionEvent(this, callback));
	}

	@Override
	public void fireLineSelectionEvent(LineSelectionEvent event) {
		Iterator<LineSelectionHandler> itHand = this.lineSelectionHandlers.iterator();
		while(itHand.hasNext()) {
			LineSelectionHandler hand = itHand.next();
			hand.onLineSelection(event);
		}
	}

	@Override
	public void onLineSelection(LineSelectionEvent event) {
		fireLineSelectionEvent(event);
	}

	@Override
	public void addNestedFormHandler(NestedFormHandler handler) {
		this.nestedFormHandlers.add(handler);
	}

	@Override
	public void fireNestedFormEvent() {
		NestedFormEvent event = new NestedFormEvent(this);
		fireNestedFormEvent(event);
	}

	@Override
	public void fireNestedFormEvent(NestedFormEvent event) {
		Iterator<NestedFormHandler> itHand = this.nestedFormHandlers.iterator();
		while(itHand.hasNext()) {
			NestedFormHandler hand = itHand.next();
			hand.onNestedForm(event);
		}
	}

	@Override
	public void onNestedForm(NestedFormEvent event) {
		this.fireNestedFormEvent(event);
	}

	@Override
	public void addMoreCompletionsHandler(MoreCompletionsHandler handler) {
		this.moreCompletionsHandlers.add(handler);
	}

	@Override
	public void fireMoreCompletionsEvent(MoreCompletionsEvent event) {
		Iterator<MoreCompletionsHandler> itHand = this.moreCompletionsHandlers.iterator();
		while(itHand.hasNext()) {
			MoreCompletionsHandler hand = itHand.next();
			hand.onMoreCompletions(event);
		}
	}

	@Override
	public void fireMoreCompletionsEvent(SuggestionCallback cb) {
		this.fireMoreCompletionsEvent(new MoreCompletionsEvent(this, cb));
	}

	@Override
	public void onMoreCompletions(MoreCompletionsEvent event) {
		fireMoreCompletionsEvent(event);
	}

	@Override
	public void addRemoveLineHandler(RemoveLineHandler handler) {
		this.removeLineHandlers.add(handler);
	}

	@Override
	public void fireRemoveLineEvent() {
		if(this instanceof FormLineWidget) {
			RemoveLineEvent event = new RemoveLineEvent((FormLineWidget) this);
			fireRemoveLineEvent(event);
		}
	}

	@Override
	public void fireRemoveLineEvent(RemoveLineEvent event) {
		Iterator<RemoveLineHandler> itHand = this.removeLineHandlers.iterator();
		while(itHand.hasNext()) {
			RemoveLineHandler hand = itHand.next();
			hand.onRemoveLine(event);
		}
	}

	@Override
	public void onRemoveLine(RemoveLineEvent event) {
		fireRemoveLineEvent(event);
	}

	@Override
	public void onRelationCreation(RelationCreationEvent event) {
		fireRelationCreationEvent(event);
	}

	@Override
	public void addRelationCreationHandler(RelationCreationHandler handler) {
		relationCreationHandlers.add(handler);
	}

	@Override
	public void fireRelationCreationEvent(RelationCreationEvent event) {
		Iterator<RelationCreationHandler> itHand = this.relationCreationHandlers.iterator();
		while(itHand.hasNext()) {
			RelationCreationHandler hand = itHand.next();
			hand.onRelationCreation(event);
		}
	}

	@Override
	public void fireRelationCreationEvent() {
		if(this instanceof FormWidget) {
			fireRelationCreationEvent(new RelationCreationEvent(this));
		}
	}

	@Override
	public void onTypeLineSet(TypeLineSetEvent event) {
		fireTypeLineSetEvent(event);
	}

	@Override
	public void addTypeLineSetHandler(TypeLineSetHandler handler) {
		this.typeLineSetHandlers.add(handler);
	}

	@Override
	public void fireTypeLineSetEvent(TypeLineSetEvent event) {
		Iterator<TypeLineSetHandler> itHand = this.typeLineSetHandlers.iterator();
		while(itHand.hasNext()) {
			TypeLineSetHandler hand = itHand.next();
			hand.onTypeLineSet(event);
		}
	}

	@Override
	public void fireTypeLineSetEvent() {
		if(this instanceof FormWidget) {
			fireTypeLineSetEvent(new TypeLineSetEvent((FormWidget) this));
		}
	}
	
	public abstract ProfileElement toProfileElement();

}
