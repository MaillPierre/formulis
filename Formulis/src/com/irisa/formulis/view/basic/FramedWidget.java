package com.irisa.formulis.view.basic;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.BasicElementList;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.custom.FluidHorizontalContainer;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class FramedWidget extends AbstractFormulisWidget {

//	protected DisplayList dList;
//	private HorizontalPanel element;
	private FluidHorizontalContainer element = new FluidHorizontalContainer();
	
	public FramedWidget(BasicElementList l, Widget first, Widget last, AbstractFormulisWidget par) {
		super(l, par);

		initWidget(element);
		
//		int nbRowElement = getData().getContent().size();
//		if(first != null) {
//			nbRowElement++;
//		}
//		if(last!= null) {
//			nbRowElement++;
//		}
		
		if(first != null) {
			element.add(first);
		}

//		int line = 0;
		Iterator<BasicElement> itElement = getData().getContentIterator();
		while(itElement.hasNext()) {
			
//			if(first != null && line > 0) {
//				element.add(first);
//			}
			BasicElement elemOperator = itElement.next();

			try {
				Composite elemWidget = FormulisWidgetFactory.getWidget(elemOperator, this);
				if(elemWidget != null) {
					element.add(elemWidget);
				}
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e); 
			}
//			line++;
		}
		
		if(last != null) {
			element.add(last);
		}
		
		element.addDomHandler(this, ClickEvent.getType());
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
