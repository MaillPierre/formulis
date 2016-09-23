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
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.LessCompletionsEvent;
import com.irisa.formulis.view.event.MoreCompletionsEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.interfaces.CompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.ElementCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasCompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.HasLessCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.HasMoreCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.HasSuggestionSelectionHandler;
import com.irisa.formulis.view.event.interfaces.LessCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.MoreCompletionsHandler;
import com.irisa.formulis.view.event.interfaces.SuggestionSelectionHandler;
import com.irisa.formulis.view.form.FormEventCallback;
import com.irisa.formulis.view.form.FormRelationLineWidget;

public class CustomSuggestionWidget extends AbstractFormulisWidget 
	implements ValueChangeHandler<String>, 
	HasCompletionAskedHandler, 
	HasLessCompletionsHandler, 
	HasMoreCompletionsHandler, 
	HasSuggestionSelectionHandler, 
	FocusHandler, 
	KeyDownHandler {

	protected LinkedList<CompletionAskedHandler> completionAskedHandlers = new LinkedList<CompletionAskedHandler>();
	protected LinkedList<MoreCompletionsHandler> moreCompletionsHandlers = new LinkedList<MoreCompletionsHandler>();
	protected LinkedList<LessCompletionsHandler> lessCompletionsHandlers = new LinkedList<LessCompletionsHandler>();
	protected LinkedList<SuggestionSelectionHandler> suggestionSelectionHandlers = new LinkedList<SuggestionSelectionHandler>();
	protected LinkedList<ElementCreationHandler> elementCreationHandlers = new LinkedList<ElementCreationHandler>();
	
	private TextBox element = new TextBox();
	private CustomSuggestionOracle oracle = new CustomSuggestionOracle();
	private CustomSuggestionPopover popover;
	private static int limit = 10;
	
	private boolean moreCompletionMode = false;
	
	public CustomSuggestionWidget(FormRelationLineWidget par) {
		super(null, par );
		initWidget(element);
		
		element.addFocusHandler(this);
		element.addValueChangeHandler(this);
		element.addKeyDownHandler(this);
		element.addClickHandler(this);
		element.setWidth("100%");
		element.getElement().setPropertyString("autocomplete", "off");
		element.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(! element.getValue().isEmpty()) {
					ValueChangeEvent.fire(element, element.getValue());
				}
			}
		});
		
		popover = new CustomSuggestionPopover(this);
	}
	
	public static int getLimit() {
		return limit;
	}
	
	public static void setLimit(int l) {
		limit = l;
	}
	
	public boolean isMoreCompletionMode() {
		return moreCompletionMode;
	}

	public void setMoreCompletionMode(boolean moreCompletionMode) {
		this.moreCompletionMode = moreCompletionMode;
		this.popover.setMoreCompletionsMode(moreCompletionMode);
	}
	
	public void setPlaceholder(String placeholder) {
//		element.getElement().setPropertyString("placeholder", placeholder);
		element.setPlaceholder(placeholder);
	}
	
	@Override
	public FormRelationLineWidget getParentWidget() {
		return (FormRelationLineWidget) super.getParentWidget();
	}
	
	public String getValue() {
		return this.element.getValue();
	}
	
	public void addSuggestionToOracle(Increment inc) {
		if(inc.getKind() == KIND.ENTITY || inc.getKind() == KIND.PROPERTY || inc.getKind() == KIND.CLASS || inc.getKind() == KIND.SOMETHING) {
			this.oracle.add(new CustomSuggestion(inc));
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
		LinkedList<CustomSuggestion> suggs = new LinkedList<CustomSuggestion>();
		Iterator<Increment> itInc = c.iterator();
		while(itInc.hasNext()) {
			Increment inc = itInc.next();
			if(inc.getKind() == KIND.ENTITY || inc.getKind() == KIND.PROPERTY || inc.getKind() == KIND.CLASS || inc.getKind() == KIND.SOMETHING) {
				suggs.add(new CustomSuggestion(inc));
			}
		}
		this.oracle.setSuggestions(suggs);
	}

	@Override
	public void onClick(ClickEvent event) {
		ControlUtils.debugMessage("CustomSuggestionWidget onClick " );
//		fireCompletionAskedEvent();
		this.getParentWidget().fireLineSelectionEvent(this.getSetCallback());
	}
	
	public void refreshSuggestions() {
		this.popover.setContent(this.oracle.matchingIncrement(this.getValue(), limit));
		this.popover.refreshSuggestion();
	}
	
	public void showSuggestions() {
		this.refreshSuggestions();
		popover.show();
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		fireCompletionAskedEvent();
	}
	
	@Override
	public void addCompletionAskedHandler(CompletionAskedHandler handler) {
		this.completionAskedHandlers.add(handler);
	}
	
	@Override
	public void fireCompletionAskedEvent() {
		ControlUtils.debugMessage("CustomSuggestionWidget fireCompletionAskedEvent");
		CompletionAskedEvent event = new CompletionAskedEvent(this, this.getSetCallback());
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
	public void addLessCompletionsHandler(LessCompletionsHandler handler) {
		this.lessCompletionsHandlers.add(handler);
	}

	public void fireLessCompletionsEvent() {
		ControlUtils.debugMessage("CustomSuggestionWidget fireLessCompletionsEvent");
		this.fireLessCompletionsEvent(new LessCompletionsEvent(this, this.getSetCallback()));
	}

	@Override
	public void fireLessCompletionsEvent(LessCompletionsEvent event) {
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

	public void fireMoreCompletionsEvent() {
		ControlUtils.debugMessage("CustomSuggestionWidget fireMoreCompletionsEvent");
		this.fireMoreCompletionsEvent(new MoreCompletionsEvent(this, this.getAddCallback()));
	}

	@Override
	public void addSuggestionSelectionHandler(SuggestionSelectionHandler handler) {
		suggestionSelectionHandlers.add(handler);
	}

	public void suggestionSelected(CustomSuggestion suggest) {
		this.fireSuggestionSelection(new SuggestionSelectionEvent(suggest));
	}

	@Override
	public void fireSuggestionSelection(SuggestionSelectionEvent event) {
		Iterator<SuggestionSelectionHandler> itHand = this.suggestionSelectionHandlers.iterator();
		while(itHand.hasNext()) {
			SuggestionSelectionHandler hand = itHand.next();
			hand.onSelection(event);
		}
	}

	/**
	 * Retransmet l'event à son widget parent
	 */
	public void fireElementCreationEvent() {
		this.getParentWidget().fireElementCreationEvent();
	}

	@Override
	public void onFocus(FocusEvent event) {
		if(this.isMoreCompletionMode()) {
			popover.refreshSuggestion();
		} else {
			fireCompletionAskedEvent();
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if(event.isDownArrow()) {
			this.popover.focus();
		} if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			if(element.getText() != "") {
				
			}
		}
	}
	
	/**
	 * 
	 * @return un callback pour les evenement visant à remplacer les suggestions existantes
	 */
	public SuggestionCallback getSetCallback() {
		return new SuggestionCallback(this){
			@Override
			public void call(Controller control) {
				ControlUtils.debugMessage("CustomSuggestionWidget SetCallback call");
				Collection<Increment> increments = control.getPlace().getSuggestions().getEntitySuggestions();
				this.source.oracle.clear();
				Iterator<Increment> itInc = increments.iterator();
				while(itInc.hasNext()) {
					Increment inc = itInc.next();
					this.source.oracle.add(new CustomSuggestion(inc));
				}
				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
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
				Collection<Increment> increments = control.getPlace().getSuggestions().getEntitySuggestions();
				Iterator<Increment> itInc = increments.iterator();
				while(itInc.hasNext()) {
					Increment inc = itInc.next();
					this.source.oracle.add(new CustomSuggestion(inc));
				}
				popover.setContent(this.source.oracle.matchingIncrement(getValue(), limit));
//				this.source.setMoreCompletionMode(! control.getPlace().hasMore()); // TODO gestion des "More" a ajouter pour relachement suggestion
			}
		};
	}

	public abstract class SuggestionCallback implements FormEventCallback {
		
		protected CustomSuggestionWidget source;
		
		public SuggestionCallback(CustomSuggestionWidget src) {
			this.source = src;
		}
	}

}
