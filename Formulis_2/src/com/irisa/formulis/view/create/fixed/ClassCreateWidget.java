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
import com.irisa.formulis.view.event.ClassCreationEvent;
import com.irisa.formulis.view.event.interfaces.ClassCreationHandler;
import com.irisa.formulis.view.event.interfaces.HasClassCreationHandler;
import com.irisa.formulis.view.form.FormWidget;

public class ClassCreateWidget extends Composite implements HasClassCreationHandler {

	private FormWidget parent;
	private FluidRow element = new FluidRow();
	private Column indentCol = new Column(1);
	private Column predentCol = new Column(1);
	private TextBox nameBox = new TextBox();
	private Column nameBoxCol = new Column(8, nameBox);
	private Button createButton = new Button("Create");
	private Column createButtonCol = new Column(2, createButton);

	private LinkedList<ClassCreationHandler> classCreationHandlers = new LinkedList<ClassCreationHandler>();
	
	public ClassCreateWidget(FormWidget par) {
		parent = par;
		initWidget(element);
		
		nameBox.addStyleName("input-block-level");
		nameBox.getElement().setPropertyString("placeholder", "Class name");
		createButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(!getTextValue().isEmpty()) {
					fireClassCreationEvent();
					parent.putElementCreationButtons();
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
	public void addClassCreationHandler(ClassCreationHandler handler) {
		classCreationHandlers.add(handler);
	}
	
	public String getTextValue() {
		return this.nameBox.getValue();
	}

	@Override
	public void fireClassCreationEvent(ClassCreationEvent event) {
		Iterator<ClassCreationHandler> itHand = this.classCreationHandlers.iterator();
		while(itHand.hasNext()) {
			ClassCreationHandler hand = itHand.next();
			hand.onClassCreation(event);
		}
	}

	@Override
	public void fireClassCreationEvent() {
		fireClassCreationEvent(new ClassCreationEvent(this));
	}

}
