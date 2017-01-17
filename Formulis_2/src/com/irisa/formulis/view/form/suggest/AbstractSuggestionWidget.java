package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.ElementCreationEvent;
import com.irisa.formulis.view.event.LessCompletionsEvent;
import com.irisa.formulis.view.event.MoreCompletionsEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.callback.AbstractActionCallback;
import com.irisa.formulis.view.event.callback.ActionCallback;
import com.irisa.formulis.view.event.callback.FormEventCallback;
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
import com.irisa.formulis.view.form.AbstractFormLineWidget;
import com.irisa.formulis.view.form.FormRelationLineWidget;

public abstract class AbstractSuggestionWidget  extends AbstractFormulisWidget 
implements ValueChangeHandler<String>, HasValueChangeHandlers<String>, 
HasCompletionAskedHandler, 
//HasLessCompletionsHandler, 
//HasMoreCompletionsHandler, 
SuggestionSelectionHandler, HasSuggestionSelectionHandler, 
HasElementCreationHandler,
FocusHandler, 
KeyDownHandler,
HasKeyUpHandlers {

	protected LinkedList<CompletionAskedHandler> completionAskedHandlers = new LinkedList<CompletionAskedHandler>();
//	protected LinkedList<MoreCompletionsHandler> moreCompletionsHandlers = new LinkedList<MoreCompletionsHandler>();
//	protected LinkedList<LessCompletionsHandler> lessCompletionsHandlers = new LinkedList<LessCompletionsHandler>();
	protected LinkedList<SuggestionSelectionHandler> suggestionSelectionHandlers = new LinkedList<SuggestionSelectionHandler>();
	protected LinkedList<ElementCreationHandler> elementCreationHandlers = new LinkedList<ElementCreationHandler>();
	
	protected TextBox element = new TextBox();
	protected SuggestionOracle oracle = new SuggestionOracle();
	protected SuggestionPopover popover = null;
	
	protected boolean waitingFor = false;
	
	protected static int limit = 10;

	public AbstractSuggestionWidget(FormElement d, AbstractFormLineWidget fParent) {
		super(d, fParent);
		initWidget(element);
		
		popover = new SuggestionPopover(this);
		popover.addSuggestionSelectionHandler(this);
		
		element.addFocusHandler(this);
		element.addValueChangeHandler(this);
		element.addKeyDownHandler(this);
		element.addClickHandler(this);
//		element.setWidth("100%");
		element.addStyleName("input-block-level");
		element.getElement().setPropertyString("autocomplete", "off");
		element.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(! element.getValue().isEmpty()) {
					ValueChangeEvent.fire(element, element.getValue());
				}
			}
		});
	}
	
	public String getValue() {
		return this.element.getValue();
	}
	
	public static int getLimit() {
		return limit;
	}
	
	public static void setLimit(int l) {
		limit = l;
	}
	
	public void setText(String text) {
		this.element.setText(text);
	}
	
	public void setPlaceholder(String placeholder) {
		element.setPlaceholder(placeholder);
	}
	
	@Override
	public AbstractFormLineWidget getParentWidget() {
		return (AbstractFormLineWidget) super.getParentWidget();
	}

	@Override
	public void onClick(ClickEvent event) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget onClick popovershowing:" + popover.isShowing());
		if(! waitingFor) {
			fireLineSelectionEvent();
		}
	}
	
	public void refreshSuggestions() {
		this.popover.setContent(this.oracle.matchingIncrement(this.getValue(), limit));
		this.popover.refreshSuggestion();
	}
	
	public void showSuggestions() {
		this.refreshSuggestions();
		popover.show();
	}
	
	public abstract void addSuggestionToOracle(Increment inc);
	public abstract void addAllSuggestionToOracle(Collection<Increment> c);
	public abstract void setOracleSuggestions(Collection<Increment> c);
	
	public abstract SuggestionCallback getLineSelectionCompletionsCallback();
	public abstract SuggestionCallback getSetCallback();
	public abstract SuggestionCallback getAddCallback();
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget onValueChange");
//		fireCompletionAskedEvent(); // FIXME commented for testing
		fireCompletionAskedEvent(event.getValue());
		this.getParentWidget().getData().setTempValue(event.getValue());
	}

	@Override
	public void addElementCreationHandler(ElementCreationHandler handler) {
		this.elementCreationHandlers.add(handler);
	}
	
	@Override
	public void addCompletionAskedHandler(CompletionAskedHandler handler) {
		this.completionAskedHandlers.add(handler);
	}
	
	@Override
	public void fireCompletionAskedEvent() {
//		ControlUtils.debugMessage("AbstractSuggestionWidget fireCompletionAskedEvent");
		fireCompletionAskedEvent(getValue());
	}
	
	@Override
	public void fireCompletionAskedEvent(String search) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget fireCompletionAskedEvent");
		CompletionAskedEvent event = new CompletionAskedEvent(this, this.getSetCallback(), search);
		fireCompletionAskedEvent(event);
	}

	@Override
	public void fireCompletionAskedEvent(CompletionAskedEvent event) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget fireCompletionAskedEvent " + event.getSearch());
		waitingFor = true;
		Iterator<CompletionAskedHandler> itHand = this.completionAskedHandlers.iterator();
		while(itHand.hasNext()) {
			CompletionAskedHandler hand = itHand.next();
			hand.onCompletionAsked(event);
		}
	}

//	@Override
//	public void addLessCompletionsHandler(LessCompletionsHandler handler) {
//		this.lessCompletionsHandlers.add(handler);
//	}
//
//	public void fireLessCompletionsEvent() {
////		ControlUtils.debugMessage("CustomSuggestionWidget fireLessCompletionsEvent");
//		this.fireLessCompletionsEvent(new LessCompletionsEvent(this, this.getSetCallback()));
//	}
//
//	@Override
//	public void fireLessCompletionsEvent(LessCompletionsEvent event) {
//		Iterator<LessCompletionsHandler> itHand = this.lessCompletionsHandlers.iterator();
//		while(itHand.hasNext()) {
//			LessCompletionsHandler hand = itHand.next();
//			hand.onLessCompletions(event);
//		}
//	}
//
//	@Override
//	public void fireLessCompletionsEvent(SuggestionCallback cb) {
//		this.fireLessCompletionsEvent(new LessCompletionsEvent(this, cb));
//	}
//
//	@Override
//	public void addMoreCompletionsHandler(MoreCompletionsHandler handler) {
//		this.moreCompletionsHandlers.add(handler);
//	}
//
//	@Override
//	public void fireMoreCompletionsEvent(MoreCompletionsEvent event) {
//		Iterator<MoreCompletionsHandler> itHand = this.moreCompletionsHandlers.iterator();
//		while(itHand.hasNext()) {
//			MoreCompletionsHandler hand = itHand.next();
//			hand.onMoreCompletions(event);
//		}
//	}
//
//	@Override
//	public void fireMoreCompletionsEvent(SuggestionCallback cb) {
//		this.fireMoreCompletionsEvent(new MoreCompletionsEvent(this, cb));
//	}
//
//	public void fireMoreCompletionsEvent() {
////		ControlUtils.debugMessage("CustomSuggestionWidget fireMoreCompletionsEvent");
//		this.fireMoreCompletionsEvent(new MoreCompletionsEvent(this, this.getAddCallback()));
//	}

	@Override
	public void addSuggestionSelectionHandler(SuggestionSelectionHandler handler) {
		suggestionSelectionHandlers.add(handler);
	}

	public void suggestionSelected(Suggestion suggest) {
		this.fireSuggestionSelection(new SuggestionSelectionEvent(suggest));
	}

	@Override
	public void fireSuggestionSelection(SuggestionSelectionEvent event) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget fireSuggestionSelection ");
		Iterator<SuggestionSelectionHandler> itHand = this.suggestionSelectionHandlers.iterator();
		while(itHand.hasNext()) {
			SuggestionSelectionHandler hand = itHand.next();
			hand.onSuggestionSelection(event);
		}
	}

	/**
	 * Retransmet l'event avec le getValue
	 */
	public void fireElementCreationEvent() {
		this.getParentWidget().fireElementCreationEvent(getValue());
	}

	@Override
	public void fireElementCreationEvent(String value) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget fireElementCreationEvent " + value);
		this.fireElementCreationEvent(new ElementCreationEvent(this.getParentWidget(), getValue()));
	}

	@Override
	public void fireElementCreationEvent(ElementCreationEvent event) {
		Iterator<ElementCreationHandler> itHand = this.elementCreationHandlers.iterator();
		while(itHand.hasNext()) {
			ElementCreationHandler hand = itHand.next();
			hand.onElementCreation(event);
		}
	}
	
	public void fireLineSelectionEvent() {
		ControlUtils.debugMessage("AbstractSuggestionWidget fireLineSelectionEvent");
		waitingFor = true;
		this.getParentWidget().fireLineSelectionEvent(this.getLineSelectionCompletionsCallback());
	}

	@Override
	public void onFocus(FocusEvent event) {
		ControlUtils.debugMessage("AbstractSuggestionWidget onFocus");
		if(! popover.isShowing()) {
			fireLineSelectionEvent();
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if(event.isDownArrow()) {
			this.popover.focus();
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return this.element.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return this.element.addKeyUpHandler(handler);
	}

	public abstract class SuggestionCallback extends AbstractActionCallback {
		
		protected AbstractSuggestionWidget source;
		
		public SuggestionCallback(AbstractSuggestionWidget src) {
			this.source = src;
		}
	}

}
