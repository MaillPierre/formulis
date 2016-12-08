package com.irisa.formulis.view;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickHandler;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.interfaces.ClickWidgetHandler;

public abstract class AbstractFormulisWidget extends AbstractDataWidget implements ClickHandler {

	protected AbstractFormulisWidget parentWid;

	private LinkedList<ClickWidgetHandler> handlers = new LinkedList<ClickWidgetHandler>();
	
	public AbstractFormulisWidget(FormElement d, AbstractFormulisWidget fParent){
		super(d);
		this.parentWid = fParent;
	}
	
	public AbstractFormulisWidget getParentWidget() {
		return this.parentWid;
	}
	
	public void setParentWidget(AbstractFormulisWidget par) {
		this.parentWid = par;
	}
	
	public void addClickWidgetEventHandler(ClickWidgetHandler handler) {
		handlers.add(handler);
	}
	
	public void fireClickWidgetEvent(ClickWidgetEvent event) {
		Iterator<ClickWidgetHandler> itHand = handlers.iterator();
		while(itHand.hasNext()) {
			ClickWidgetHandler hand = itHand.next();
			hand.onClickWidgetEvent(event);
		}
	}

}
