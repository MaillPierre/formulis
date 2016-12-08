package com.irisa.formulis.view.basic;

import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.irisa.formulis.model.basic.Space;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class SpaceWidget extends AbstractFormulisWidget {
	
	private InlineLabel element;
	
	public SpaceWidget(AbstractFormulisWidget par) {
		super(new Space(), par);
		element = new InlineLabel("");
		element.setStyleName("space");
		
		initWidget(element);
		
		element.addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}

}
