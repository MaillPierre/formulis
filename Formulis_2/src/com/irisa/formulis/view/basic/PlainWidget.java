package com.irisa.formulis.view.basic;

import com.google.gwt.event.dom.client.ClickEvent;
import com.irisa.formulis.model.basic.Plain;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;

public class PlainWidget extends AbstractFormulisWidget {

	private InlineLabel element;
	
	public PlainWidget(Plain pln, AbstractFormulisWidget par) {
		super(pln, par);
		element = new InlineLabel(getData().getPlain());
		
		initWidget(element);

		element.setTitle(getData().getLang());
		element.setStyleName("plainLiteral");
		element.addClickHandler(this);
	}
	
	@Override
	public Plain getData() {
		return (Plain) super.getData();
	}

	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}
	
}
