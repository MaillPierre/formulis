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
import com.irisa.formulis.view.event.RelationCreationEvent;
import com.irisa.formulis.view.event.interfaces.HasRelationCreationHandler;
import com.irisa.formulis.view.event.interfaces.RelationCreationHandler;
import com.irisa.formulis.view.form.FormWidget;

public class RelationCreateWidget extends Composite implements HasRelationCreationHandler {

	private FormWidget parent;
	private FluidRow element = new FluidRow();
	private Column indentCol = new Column(1);
	private Column predentCol = new Column(1);
	private TextBox nameBox = new TextBox();
	private Column nameBoxCol = new Column(8, nameBox);
	private Button createButton = new Button("Create");
	private Column createButtonCol = new Column(2, createButton);

	private LinkedList<RelationCreationHandler> relationCreationHandlers = new LinkedList<RelationCreationHandler>();
	
	public RelationCreateWidget(FormWidget par) {
		parent = par;
		initWidget(element);
		
		nameBox.addStyleName("weblis-max-width");
		nameBox.getElement().setPropertyString("placeholder", "Nom de la propriété");
		createButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(!getTextValue().isEmpty()) {
					fireRelationCreationEvent();
					parent.putRelationCreationButton();
				}
			}
		});
		
		element.add(indentCol);
		element.add(nameBoxCol);
		element.add(createButtonCol);
		element.add(predentCol);
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

}
