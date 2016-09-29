package com.irisa.formulis.view.form;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.ClassCreationEvent;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.ElementCreationEvent;
import com.irisa.formulis.view.event.FinishFormEvent;
import com.irisa.formulis.view.event.FinishLineEvent;
import com.irisa.formulis.view.event.LessCompletionsEvent;
import com.irisa.formulis.view.event.LineSelectionEvent;
import com.irisa.formulis.view.event.MoreCompletionsEvent;
import com.irisa.formulis.view.event.RelationCreationEvent;
import com.irisa.formulis.view.event.RemoveLineEvent;
import com.irisa.formulis.view.event.StatementChangeEvent;
import com.irisa.formulis.view.event.interfaces.ClassCreationHandler;
import com.irisa.formulis.view.event.interfaces.CompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.ElementCreationHandler;
import com.irisa.formulis.view.event.interfaces.FinishFormHandler;
import com.irisa.formulis.view.event.interfaces.FinishLineHandler;
import com.irisa.formulis.view.event.interfaces.FormEventChainHandler;
import com.irisa.formulis.view.event.interfaces.HasFormEventChainHandlers;
import com.irisa.formulis.view.event.interfaces.LessCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.LineSelectionHandler;
import com.irisa.formulis.view.event.interfaces.MoreCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.RelationCreationHandler;
import com.irisa.formulis.view.event.interfaces.RemoveLineHandler;
import com.irisa.formulis.view.event.interfaces.StatementChangeHandler;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

public abstract class AbstractFormElementWidget extends AbstractFormulisWidget
		implements FormEventChainHandler, HasFormEventChainHandlers {

	protected LinkedList<CompletionAskedHandler> completionAskedHandlers = new LinkedList<CompletionAskedHandler>();
	protected LinkedList<ClassCreationHandler> classCreationHandlers = new LinkedList<ClassCreationHandler>();
	protected LinkedList<ElementCreationHandler> elementCreationHandlers = new LinkedList<ElementCreationHandler>();
	protected LinkedList<FinishFormHandler> finishFormHandlers = new LinkedList<FinishFormHandler>();
	protected LinkedList<FinishLineHandler> finishLineHandlers = new LinkedList<FinishLineHandler>();
	protected LinkedList<LessCompletionsHandler> lessCompletionsHandlers = new LinkedList<LessCompletionsHandler>();
	protected LinkedList<LineSelectionHandler> lineSelectionHandlers = new LinkedList<LineSelectionHandler>();
	protected LinkedList<MoreCompletionsHandler> moreCompletionsHandlers = new LinkedList<MoreCompletionsHandler>();
	protected LinkedList<RelationCreationHandler> relationCreationHandlers = new LinkedList<RelationCreationHandler>();
	protected LinkedList<RemoveLineHandler> removeLineHandlers = new LinkedList<RemoveLineHandler>();
	protected LinkedList<StatementChangeHandler> statementChangeHandlers = new LinkedList<StatementChangeHandler>();
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
	public void addClassCreationHandler(ClassCreationHandler hand) {
		this.classCreationHandlers.add(hand);
	}

	@Override
	public void fireClassCreationEvent() {
		ClassCreationEvent event = new ClassCreationEvent(this);
		fireClassCreationEvent(event);
	}

	@Override
	public void fireClassCreationEvent(ClassCreationEvent event) {
//		ControlUtils.debugMessage(this.getClass().getSimpleName() + " fireClassCreationEvent");
		Iterator<ClassCreationHandler> itHand = this.classCreationHandlers.iterator();
		while(itHand.hasNext()) {
			ClassCreationHandler hand = itHand.next();
			hand.onClassCreation(event);
		}
	}

	@Override
	public void onClassCreation(ClassCreationEvent event) {
		fireClassCreationEvent(event);
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
	public void addFinishFormHandler(FinishFormHandler handler) {
		this.finishFormHandlers.add(handler);
	}

	@Override
	public void fireFinishFormEvent(boolean state) {
		if(this instanceof FormWidget) {
			FinishFormEvent event = new FinishFormEvent((FormWidget) this, state);
			fireFinishFormEvent(event);
		}
	}

	@Override
	public void fireFinishFormEvent(boolean state, FormEventCallback callback) {
		if(this instanceof FormWidget) {
			FinishFormEvent event = new FinishFormEvent((FormWidget) this, state, callback);
			fireFinishFormEvent(event);
		}
	}

	@Override
	public void fireFinishFormEvent(FinishFormEvent event) {
//		ControlUtils.debugMessage("fireFinishFormEvent " + this.getClass());
		Iterator<FinishFormHandler> itHand = this.finishFormHandlers.iterator();
		while(itHand.hasNext()) {
			FinishFormHandler hand = itHand.next();
			hand.onFinishForm(event);
		}
	}

	@Override
	public void onFinishForm(FinishFormEvent event) {
		fireFinishFormEvent(event);
	}

	@Override
	public void addFinishLineHandler(FinishLineHandler handler) {
		this.finishLineHandlers.add(handler);
	}

	@Override
	public void fireFinishLineEvent(boolean state) {
		if(this instanceof FormLineWidget) {
			FinishLineEvent event = new FinishLineEvent((FormLineWidget) this, state);
			fireFinishLineEvent(event);
		}
	}

	@Override
	public void fireFinishLineEvent(FinishLineEvent event) {
		Iterator<FinishLineHandler> itHand = this.finishLineHandlers.iterator();
		while(itHand.hasNext()) {
			FinishLineHandler hand = itHand.next();
			hand.onFinishLine(event);
		}
	}

	@Override
	public void onFinishLine(FinishLineEvent event) {
		fireFinishLineEvent(event);
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
	public void addMoreCompletionsHandler(MoreCompletionsHandler handler) {
		this.moreCompletionsHandlers.add(handler);
	}

	@Override
	public void fireMoreCompletionsEvent(MoreCompletionsEvent event) {
//		ControlUtils.debugMessage(this.getClass().getSimpleName() + " fireMoreCompletionsEvent");
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
	public void addLessCompletionsHandler(LessCompletionsHandler handler) {
		this.lessCompletionsHandlers.add(handler);
	}

	@Override
	public void fireLessCompletionsEvent(LessCompletionsEvent event) {
//		ControlUtils.debugMessage(this.getClass().getSimpleName() + " fireLessCompletionsEvent");
		Iterator<LessCompletionsHandler> itHand = this.lessCompletionsHandlers.iterator();
		while(itHand.hasNext()) {
			LessCompletionsHandler hand = itHand.next();
			hand.onLessCompletions(event);
		}
	}

	@Override
	public void fireLessCompletionsEvent(SuggestionCallback cb) {
		this.fireLessCompletionsEvent(new LessCompletionsEvent(this, cb));
	}

	@Override
	public void onLessCompletions(LessCompletionsEvent event) {
		fireLessCompletionsEvent(event);
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
	
	public abstract ProfileElement toProfileElement();

}
