package com.irisa.formulis.view.create.fixed;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
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
	private FluidRow nameBoxRow = new FluidRow(); // used to give the same appearance to this as the relation lines
	private Column nameBoxCol = new Column(8, nameBoxRow);
	private Column nameBoxSubCol = new Column(11);
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private FluidContainer buttonContainer = new FluidContainer();
	private Button createButton = new Button("Create");
	private Button cancelButton = new Button("Cancel");
	private Column buttonCol = new Column(4, buttonContainer);

	private LinkedList<RelationCreationHandler> relationCreationHandlers = new LinkedList<RelationCreationHandler>();
	
	public RelationCreateWidget(FormWidget par) {
		parent = par;
		initWidget(element);
		
		nameBox = new PropertySuggestionWidget(null, parent);
		nameBoxRow.add(nameBoxSubCol);
		nameBoxSubCol.add(nameBox);
		
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
					parent.removeRelationCreationWidget();
				}
			}
		});
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parent.removeRelationCreationWidget();
			}
		});
		
		nameBoxCol.addStyleName("no-gutter");
		buttonContainer.add(buttonPanel);
		createButton.setBlock(true);
		cancelButton.setBlock(true);
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);
		buttonPanel.setWidth("100%");
		buttonCol.addStyleName("no-gutter");
		
		element.add(nameBoxCol);
		element.add(buttonCol);
		
		reload();
	}
	
	public FormWidget getParentWidget() {
		return parent;
	}
	
	/**
	 * Just set the offset right
	 */
	public void reload() {
		if(! getParentWidget().getData().isAnonymous()) {
			nameBoxSubCol.setOffset(1);
		} else {
			nameBoxSubCol.setOffset(0);
		}
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
//		ControlUtils.debugMessage("RelationCreateWidget onSuggestionSelection " + event.getSuggestion().getElement());
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
