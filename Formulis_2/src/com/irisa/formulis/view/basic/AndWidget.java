package com.irisa.formulis.view.basic;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.DataUtils;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.BasicElementList;
import com.irisa.formulis.model.basic.Keyword;
import com.irisa.formulis.model.basic.Pair;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class AndWidget extends AbstractFormulisWidget {

	private FluidRow element = new FluidRow();
	private Column contentCol = new Column(12);
	
	public AndWidget(BasicElementList l, AbstractFormulisWidget par) throws FormElementConversionException {
		super(l, par);

		initWidget(element);
		
		element.add(contentCol);
		element.addStyleName("operatorAnd");
		
		LinkedList<LinkedList<BasicElement>> lineList = new LinkedList<LinkedList<BasicElement>>();
		LinkedList<BasicElement> currentLine = null;
		Iterator<BasicElement> itData = getData().getContentIterator();
		while(itData.hasNext()) {
			BasicElement dataElem = itData.next();
			
			LinkedList<BasicElement> displayableElements = DataUtils.getFirstDisplayableElements(dataElem);
			Iterator<BasicElement> itBareData = displayableElements.iterator();
			while(itBareData.hasNext()) {
				BasicElement bareData = itBareData.next();
				if(currentLine == null) {
					currentLine = new LinkedList<BasicElement>();
				}
				if(bareData instanceof Pair) {
					if(!currentLine.isEmpty()) {
						lineList.add(currentLine);
					}
					currentLine = new LinkedList<BasicElement>();
					currentLine.add(new Keyword("that"));
					currentLine.add(bareData);
				}
			}
			if(! itData.hasNext()) {
				lineList.add(currentLine);
			}
		}
		
//		Utils.displayDebugMessage("AndWidget " + lineList);
		int nbRow = 0;
		Iterator<LinkedList<BasicElement>> itLines = lineList.iterator();
		while(itLines.hasNext()) {
			LinkedList<BasicElement> line = itLines.next();
			HorizontalPanel row = new HorizontalPanel();
			if(nbRow > 0) {
				Composite firstWidget = FormulisWidgetFactory.getWidget(new Keyword("And"), this);
				row.add(firstWidget);
			}
			
//			Utils.displayDebugMessage("AndWidget row start: " + row);
			Iterator<BasicElement> itElement = line.iterator();
			while(itElement.hasNext()) {
				BasicElement elemOperator = itElement.next();
				if(elemOperator instanceof Pair && ! ((Pair)elemOperator).getForceIndent() ) {
					Pair elemPair = (Pair)elemOperator;
					Iterator<BasicElement> itLine1 = elemPair.getIteratorOnFirstLine();
					while(itLine1.hasNext()) {
						BasicElement line1Elem = itLine1.next();
						row.add(FormulisWidgetFactory.getWidget(line1Elem, this));
					}
					Iterator<BasicElement> itLine2 = elemPair.getIteratorOnSecondLine();
					while(itLine2.hasNext()) {
						BasicElement line2Elem = itLine2.next();
						row.add(FormulisWidgetFactory.getWidget(line2Elem, this));
					}
				} else {
					try {
						Composite elemWidget = FormulisWidgetFactory.getWidget(elemOperator, this);
						row.add(elemWidget);
					} catch (FormElementConversionException e) {
						ControlUtils.exceptionMessage(e);
					}
				}
			}
//			Utils.displayDebugMessage("AndWidget row end: " + row);
			contentCol.add(row);
			nbRow++;
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

