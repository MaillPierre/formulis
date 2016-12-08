package com.irisa.formulis.view.basic;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.BasicElementList;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.interfaces.ClickWidgetHandler;

public class DisplayWidget extends AbstractFormulisWidget implements ClickHandler, ClickWidgetHandler {

	private HorizontalPanel element = new HorizontalPanel();
	private LinkedList<ClickHandler> handlers;
	
	public DisplayWidget( BasicElementList l, AbstractFormulisWidget par) {
		super(l, par);
		handlers = new LinkedList<ClickHandler>();
		
		initWidget(element);
		element.addStyleName("display");
		element.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		try {
			Iterator<BasicElement> itElement = getData().getContentIterator();
			while(itElement.hasNext()) {
				BasicElement elemDisplay = itElement.next();
				if(elemDisplay != null) {
					Composite elemWidget = FormulisWidgetFactory.getWidget(elemDisplay, this);
					element.add(elemWidget);
				}
			}
		} catch (FormElementConversionException e) {
			ControlUtils.exceptionMessage(e);
		}
	}
	
	@Override
	public BasicElementList getData() {
		return (BasicElementList) super.getData();
	}

	@Override
	public void onClick(ClickEvent event) {
//		Window.alert("onClick " + event.getSource());
		Iterator<ClickHandler> itHand = handlers.iterator();
		while(itHand.hasNext()) {
			ClickHandler han = itHand.next();
			
			han.onClick(event);
		}
	}

	@Override
	public void onClickWidgetEvent(ClickWidgetEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}

}
