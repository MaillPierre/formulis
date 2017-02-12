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
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.DataUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.UnexpectedAction;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;
import com.irisa.formulis.view.event.ClassCreationEvent;
import com.irisa.formulis.view.event.CompletionAskedEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.interfaces.ClassCreationHandler;
import com.irisa.formulis.view.event.interfaces.CompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.HasClassCreationHandler;
import com.irisa.formulis.view.event.interfaces.SuggestionSelectionHandler;
import com.irisa.formulis.view.form.FormWidget;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;
import com.irisa.formulis.view.form.suggest.ClassSuggestionWidget;
import com.irisa.formulis.view.form.suggest.Suggestion;

public class ClassCreateWidget extends AbstractDataWidget implements HasClassCreationHandler, SuggestionSelectionHandler, CompletionAskedHandler {

	private FormWidget parent;
	private FluidRow element = new FluidRow();
	private Column indentCol = new Column(1);
	private Column predentCol = new Column(1);
	private ClassSuggestionWidget nameBox;
	private Column nameBoxCol;
	private Button createButton = new Button("Create");
	private Column createButtonCol = new Column(2, createButton);

	private LinkedList<ClassCreationHandler> classCreationHandlers = new LinkedList<ClassCreationHandler>();
	
	public ClassCreateWidget(FormWidget par) {
		super(null);
		parent = par;
		initWidget(element);
		
		nameBox = new ClassSuggestionWidget((FormElement)null, par);
		nameBoxCol = new Column(8, nameBox);
		
		nameBox.addStyleName("input-block-level");
		nameBox.getElement().setPropertyString("placeholder", "Class name");
		nameBox.addSuggestionSelectionHandler(this);
		nameBox.addCompletionAskedHandler(par);
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

	@Override
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
		ControlUtils.debugMessage("ClassCreateWidget onSuggestionSelection " + event.getSuggestion().getElement());
		Suggestion sugg = event.getSuggestion();
		if(sugg.getElement() instanceof URI ) {
			URI uriEvent = (URI) sugg.getElement();
			FormClassLine newClassLine = new FormClassLine(parent.getData(), uriEvent);
			getParentWidget().getData().addTypeLine(newClassLine);
			getParentWidget().removeClassCreationWidget();
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
