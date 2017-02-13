package com.irisa.formulis.view.create.fixed;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormRelationLine;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.RelationCreationEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.interfaces.CompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.HasRelationCreationHandler;
import com.irisa.formulis.view.event.interfaces.RelationCreationHandler;
import com.irisa.formulis.view.event.interfaces.SuggestionSelectionHandler;
import com.irisa.formulis.view.form.FormWidget;
import com.irisa.formulis.view.form.suggest.PropertySuggestionWidget;
import com.irisa.formulis.view.form.suggest.Suggestion;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;

public class RelationCreateWidget extends Composite implements HasRelationCreationHandler, SuggestionSelectionHandler, CompletionAskedHandler {

	private FormWidget parent;
	private FluidRow element = new FluidRow();
//	private TextBox nameBox = new TextBox();
	private PropertySuggestionWidget nameBox;
	private Column nameBoxCol;
	private Button createButton = new Button("Create");
	private Column createButtonCol = new Column(2, createButton);
	private Button cancelButton = new Button("Cancel");
	private Column cancelButtonCol = new Column(2, cancelButton);

	private LinkedList<RelationCreationHandler> relationCreationHandlers = new LinkedList<RelationCreationHandler>();
	
	public RelationCreateWidget(FormWidget par) {
		parent = par;
		initWidget(element);
		
		nameBox = new PropertySuggestionWidget(null, parent);
		nameBoxCol = new Column(8, nameBox);
		
		nameBox.addStyleName("input-block-level");
		nameBox.getElement().setPropertyString("placeholder", "Property name");
		nameBox.addSuggestionSelectionHandler(this);
		nameBox.addCompletionAskedHandler(par);
		nameBox.setSuggestionOnly(true);
		createButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(!getTextValue().isEmpty()) {
					fireRelationCreationEvent();
					parent.putElementCreationButtons();
				}
			}
		});
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parent.removeRelationCreationWidget();
			}
		});
		
		element.add(nameBoxCol);
		element.add(createButtonCol);
		element.add(cancelButtonCol);
	}
	
	public FormWidget getParentWidget() {
		return parent;
	}

	@Override
	public void addRelationCreationHandler(RelationCreationHandler handler) {
		relationCreationHandlers.add(handler);
	}
	
	public String getTextValue() {
		return this.nameBox.getValue();
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
		fireRelationCreationEvent(new RelationCreationEvent(this));
	}

	@Override
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
		ControlUtils.debugMessage("RelationCreateWidget onSuggestionSelection " + event.getSuggestion().getElement());
		Suggestion sugg = event.getSuggestion();
		if(sugg.getElement() instanceof URI ) {
			URI uriEvent = (URI) sugg.getElement();
			FormRelationLine newRelLine = new FormRelationLine(parent.getData(), uriEvent);
			getParentWidget().getData().addLine(newRelLine);
			getParentWidget().removeRelationCreationWidget();
			getParentWidget().reload();
		}
	}
	
	public SuggestionCallback getSetCallback() {
		return this.nameBox.getSetCallback();
	}

	@Override
	public void onCompletionAsked(CompletionAskedEvent event) {
		getParentWidget().onCompletionAsked(event);
	}

}
