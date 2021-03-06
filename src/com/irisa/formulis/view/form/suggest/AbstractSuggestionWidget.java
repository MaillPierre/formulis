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
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.ElementCreationEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.callback.AbstractActionCallback;
import com.irisa.formulis.view.event.interfaces.CompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.ElementCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasCompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.HasElementCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasSuggestionSelectionHandler;
import com.irisa.formulis.view.event.interfaces.SuggestionSelectionHandler;
import com.irisa.formulis.view.form.AbstractFormLineWidget;

/**
 * Abstract class for all suggestion fields, handles basic behaviour, the subclasses have to specify which kind of suggestions to send to the oracle
 * @author pmaillot
 *
 */
public abstract class AbstractSuggestionWidget  extends AbstractFormulisWidget 
implements ValueChangeHandler<String>, HasValueChangeHandlers<String>, 
HasCompletionAskedHandler, 
SuggestionSelectionHandler, 
HasSuggestionSelectionHandler, 
HasElementCreationHandler,
FocusHandler, 
KeyDownHandler,
KeyUpHandler,
HasKeyUpHandlers {

	protected LinkedList<CompletionAskedHandler> completionAskedHandlers = new LinkedList<CompletionAskedHandler>();
	protected LinkedList<SuggestionSelectionHandler> suggestionSelectionHandlers = new LinkedList<SuggestionSelectionHandler>();
	protected LinkedList<ElementCreationHandler> elementCreationHandlers = new LinkedList<ElementCreationHandler>();
	
	protected TextBox element = new TextBox();
	protected SuggestionOracle oracle = new SuggestionOracle();
	protected SuggestionPopover popover = null;
	
	protected boolean waitingFor = false;
	protected boolean suggestionOnly = false;
	
	protected static int limit = 10;

	public AbstractSuggestionWidget(FormElement d, AbstractFormulisWidget fParent) {
		super(d, fParent);
		initWidget(element);
		
		popover = new SuggestionPopover(this);
		popover.addSuggestionSelectionHandler(this);
		
		element.addFocusHandler(this);
		element.addValueChangeHandler(this);
		element.addKeyDownHandler(this);
		element.addKeyUpHandler(this);
		element.addClickHandler(this);
//		element.setWidth("100%");
		element.addStyleName("input-block-level");
		element.getElement().setPropertyString("autocomplete", "off");
		element.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					popover.focus();
				} else if(! element.getValue().isEmpty()) {
					ValueChangeEvent.fire(element, element.getValue());
				}
			}
		});
	}
	
	public void setSuggestionOnly(boolean suggOnly) {
		this.suggestionOnly = suggOnly;
	}
	
	public boolean isSuggestionOnly() {
		return this.suggestionOnly;
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
	public void onClick(ClickEvent event) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget onClick popovershowing:" + popover.isShowing());
		if(! waitingFor || ! popover.isShowing()) {
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

	/**
	 * Call addSuggestionToOracle()
	 * @param c
	 */
	public void addAllSuggestionToOracle(Collection<Increment> c) {
		Iterator<Increment> itSugg = c.iterator();
		while(itSugg.hasNext()) {
			Increment inc = itSugg.next();
			this.addSuggestionToOracle(inc);
		}
	}
	public abstract void setOracleSuggestions(Collection<Increment> c);

	/**
	 * 
	 * @return un callback à appeler des que les suggestions sont prètes
	 */
	public SuggestionCallback getLineSelectionCompletionsCallback() {
		return new SuggestionCallback(this) {
			@Override
			public void call() {
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
			public void call() {
				if(Controller.instance().getPlace().getCurrentCompletions() != null) {
//					ControlUtils.debugMessage("AbstractSuggestionCallback getSetCallback " + Controller.instance().getPlace().getCurrentCompletions());
					source.setOracleSuggestions(Controller.instance().getPlace().getCurrentCompletions());
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
			public void call() {
				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
				if(Controller.instance().getPlace().getCurrentCompletions() != null) {
					waitingFor = false;
					source.setOracleSuggestions(Controller.instance().getPlace().getCurrentCompletions());
					source.showSuggestions();
				}
			}
		};
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget onValueChange");
//		fireCompletionAskedEvent(); // FIXME commented for testing
		fireValueChangeEvent(event.getValue());
		fireCompletionAskedEvent(event.getValue());
		if(getParentWidget() instanceof AbstractFormLineWidget) {
			((AbstractFormLineWidget) this.getParentWidget()).getData().setTempValue(event.getValue());
		}
	}
	
	public void fireValueChangeEvent(String value) {
		ValueChangeEvent.fire(this, value);
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

//	/**
//	 * Retransmet l'event avec le getValue
//	 */
//	public void fireElementCreationEvent() {
//		if(getParentWidget() instanceof AbstractFormLineWidget) {
//			((AbstractFormLineWidget) this.getParentWidget()).fireElementCreationEvent(getValue());
//		}
//	}

	@Override
	public void fireElementCreationEvent(String value) {
		if(getParentWidget() instanceof AbstractFormLineWidget) {
			this.fireElementCreationEvent(new ElementCreationEvent((AbstractFormLineWidget) this.getParentWidget(), getValue()));
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
	
	public void fireLineSelectionEvent() {
//		ControlUtils.debugMessage("AbstractSuggestionWidget fireLineSelectionEvent");
		if(getParentWidget() instanceof AbstractFormLineWidget) {
			waitingFor = true;
			((AbstractFormLineWidget) this.getParentWidget()).fireLineSelectionEvent(this.getLineSelectionCompletionsCallback());
		}
	}

	@Override
	public void onFocus(FocusEvent event) {
//		ControlUtils.debugMessage("AbstractSuggestionWidget onFocus");
		if(! popover.isShowing()) {
			fireLineSelectionEvent();
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if(event.isDownArrow()) {
			this.popover.focus();
		} else {
			this.fireValueChangeEvent(getValue());
		}
	}
	
	@Override
	public void onKeyUp(KeyUpEvent event) {
		this.fireValueChangeEvent(getValue());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
//		return this.element.addValueChangeHandler(handler);
		return this.addHandler(handler, ValueChangeEvent.getType());
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
