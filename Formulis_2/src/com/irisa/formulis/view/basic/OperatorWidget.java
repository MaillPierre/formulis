package com.irisa.formulis.view.basic;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.BasicElementList;
import com.irisa.formulis.model.basic.Keyword;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class OperatorWidget extends AbstractFormulisWidget {

	private HorizontalPanel element = new HorizontalPanel();
	
	public OperatorWidget(BasicElementList l, Keyword firstWord, AbstractFormulisWidget par) throws FormElementConversionException {
//		Controller.displayDebugMessage("FrameWidget " + l);
		super(l, par);

		initWidget(element);
		
		Composite firstWidget = FormulisWidgetFactory.getWidget(firstWord, this);
		element.add(firstWidget);
		element.addStyleName("operator");

		Iterator<BasicElement> itElement = getData().getContentIterator();
		while(itElement.hasNext()) {
			BasicElement elemOperator = itElement.next();
			try {
				Composite elemWidget = FormulisWidgetFactory.getWidget(elemOperator, this);
				element.add(elemWidget);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
		}
	}

	@Override
	public BasicElementList getData() {
		return (BasicElementList) super.getData();
	}
	
	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}
	
}
