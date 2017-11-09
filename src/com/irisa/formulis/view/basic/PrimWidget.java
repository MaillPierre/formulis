package com.irisa.formulis.view.basic;

import com.google.gwt.event.dom.client.ClickEvent;
import com.irisa.formulis.model.basic.Prim;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;

public class PrimWidget extends AbstractFormulisWidget {

	private InlineLabel element;
	
	public PrimWidget(Prim kwd, AbstractFormulisWidget par) {
		super(kwd, par);
		element = new InlineLabel(getData().getPrim());
		
		initWidget(element);
		
		element.addClickHandler(this);
	}
	
	@Override
	public Prim getData() {
		return (Prim) super.getData();
	}

	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}
}
