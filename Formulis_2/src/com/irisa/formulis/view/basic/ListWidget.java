package com.irisa.formulis.view.basic;

import java.util.Iterator;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.List;
import com.irisa.formulis.model.basic.Space;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.custom.FluidHorizontalContainer;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class ListWidget extends AbstractFormulisWidget {

//	private FlexTable element;
	private FluidRow element = new FluidRow();
	
	public ListWidget(List l, AbstractFormulisWidget par) {
		super(l, par);
		
		initWidget(element);
		
		Column listCol = new Column(12);
		element.add(listCol);
		
		int numRow = 0;
		Iterator<BasicElement> itElement = getData().getContentIterator();
		while(itElement.hasNext()) {
			try {
				BasicElement elemList = itElement.next();
				if(elemList != null && elemList.getClass() != Space.class ) {
//					HorizontalPanel row = new HorizontalPanel();
					FluidHorizontalContainer row = new FluidHorizontalContainer();
					if(getData().getContent().size() > 1) {
						HTML numberHtml = new HTML(String.valueOf(numRow+1));
						numberHtml.addStyleName("listNumber");
						row.add( numberHtml );
					}
					row.add(FormulisWidgetFactory.getWidget(elemList, this));
					listCol.add(row);
					
					numRow++;
				}
			} catch (FormElementConversionException e) {
				Window.alert("ERROR: ListWidget ("+ getData() +") ListWidget "+ ControlUtils.expandExceptionMessage(e));
			}
		}
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
//		for(int i = 0; i < this.element.getWidgetCount(); i++) {
//			for(int j = 0; i < this.element.getCellCount(i); j ++) {
//				this.element.getWidget(i, j).addHandler(handler, ClickEvent.getType());
//			}
//		}
		return null;
	}

	@Override
	public List getData() {
		return (List) super.getData();
	}
	
	public List getList() {
		return getData();
	}

	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}
}
