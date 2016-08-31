package com.irisa.formulis.view.basic;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.Focus;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.interfaces.ClickWidgetEventHandler;
import com.irisa.formulis.view.event.interfaces.StatementFocusChangeHandler;

public class FocusWidget extends AbstractFormulisWidget implements  ClickHandler, ClickWidgetEventHandler {

	private boolean focused;
//	private HorizontalPanel element;
	private FluidContainer element = new FluidContainer();
	private LinkedList<StatementFocusChangeHandler> fchangeHandlerList;
	
	public FocusWidget(Focus f, AbstractFormulisWidget par) {
		super(f, par);
		fchangeHandlerList = new LinkedList<StatementFocusChangeHandler>();
		
		initWidget(element);
		setFocused(false);
		

		Iterator<BasicElement> itElement = getData().getContentIterator();
		while(itElement.hasNext()) {
			BasicElement elemFocus = itElement.next();
			try {
				Composite elemWidget = FormulisWidgetFactory.getWidget(elemFocus, this);
				element.add(elemWidget);
			} catch (FormElementConversionException e) {
				ControlUtils.debugMessage("ERROR: FocusWidget ("+ getData() +") FocusWidget "+ ControlUtils.expandExceptionMessage(e));
			}
		}
		
	}
	
	@Override
	public Focus getData() {
		return (Focus) super.getData();
	}

	@Override
	public void onClick(ClickEvent event) {
		if(! focused) {
			setFocused(true);
			focusClicked();
		}
	}

	public Focus getFocus() {
		return getData();
	}

	public boolean isFocused() {
		return focused;
	}
	
	public void setFocused(boolean fd) {
		if(focused != fd) {
			focused = fd;
			if(focused) {
				this.setStyleName("focused");
			} else {
				this.setStyleName("focus");
			}
		}
	}

	private void focusClicked() {
		Iterator<StatementFocusChangeHandler> itHand = this.fchangeHandlerList.iterator();
		while(itHand.hasNext()) {
			StatementFocusChangeHandler h = itHand.next();
			h.onFocusChange(getFocus());
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + getData();
	}

	@Override
	public void onClickWidgetEvent(ClickWidgetEvent event) {
		focusClicked();
	}
	
}
