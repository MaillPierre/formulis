package com.irisa.formulis.view.basic;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.DataUtils;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.Pair;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class PairWidget extends AbstractFormulisWidget {
	
	private Column line1Col;
	private HorizontalPanel line1Row = new HorizontalPanel();
	private Column line2Col;
	private HorizontalPanel line2Row = new HorizontalPanel();
	private FluidContainer element;
	
	public PairWidget(Pair p, AbstractFormulisWidget par) {
		super(p, par);
//		Utils.displayDebugMessage("PairWidget " + p);
		element = new FluidContainer();
		initWidget(element);
		element.addStyleName("pair");
		
		if(getData().getForceIndent()) { // Deux lignes indentées	
			
			element.add(new Column(12, line1Row, line2Row));
			line1Row.addStyleName("pairLine");
			line2Row.addStyleName("pairLine");
			
			Iterator<BasicElement> itLine1 = getData().getIteratorOnFirstLine();
			while(itLine1.hasNext()) {
				BasicElement elemLine1 = itLine1.next();
				try {
//					line1Row.add(new Column(12/getData().getFirstLine().size(), WeblisWidgetFactory.getWidget(elemLine1, this)));
					line1Row.add(FormulisWidgetFactory.getWidget(elemLine1, this));
				} catch (FormElementConversionException e) {
					ControlUtils.exceptionMessage(e);
				}
			}
			
			Iterator<BasicElement> itLine2 = getData().getIteratorOnSecondLine();
			while(itLine2.hasNext()) {
				BasicElement elemLine2 = itLine2.next();
				try {
//					line2Row.add(new Column(12/getData().getSecondLine().size(), WeblisWidgetFactory.getWidget(elemLine2, this)));
					line2Row.add(FormulisWidgetFactory.getWidget(elemLine2, this));
				} catch (FormElementConversionException e) {
					ControlUtils.exceptionMessage(e);
				}
			}
			
		} else { // Tout sur une seule ligne
			
			LinkedList<BasicElement> elemList = new LinkedList<BasicElement>();
			Iterator<BasicElement> itLine1 = getData().getIteratorOnFirstLine();
			while(itLine1.hasNext()) {
				BasicElement elemLine1 = itLine1.next();
				elemList.addAll(DataUtils.getFirstDisplayableElements(elemLine1));
			}
			Iterator<BasicElement> itLine2 = getData().getIteratorOnSecondLine();
			while(itLine2.hasNext()) {
				BasicElement elemLine2 = itLine2.next();
				elemList.addAll(DataUtils.getFirstDisplayableElements(elemLine2));
			}
			
			Iterator<BasicElement> itElemList = elemList.iterator();
			while(itElemList.hasNext()) {
				BasicElement elemLine = itElemList.next();
				try {
					element.add(FormulisWidgetFactory.getWidget(elemLine, this));
				} catch (FormElementConversionException e) {
					ControlUtils.exceptionMessage(e);
				}
			}
		}
	}
	
	@Override
	public Pair getData() {
		return (Pair) super.getData();
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		for(int i = 0; i < this.line1Col.getWidgetCount(); i++) {
			this.line1Col.getWidget(i).addHandler(handler, ClickEvent.getType());
		}
		for(int i = 0; i < this.line2Col.getWidgetCount(); i++) {
			this.line2Col.getWidget(i).addHandler(handler, ClickEvent.getType());
		}
		return null;
	}

	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}

}
