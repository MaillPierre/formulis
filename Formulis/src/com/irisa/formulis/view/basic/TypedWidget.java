package com.irisa.formulis.view.basic;

import com.google.gwt.event.dom.client.ClickEvent;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;

public class TypedWidget extends AbstractFormulisWidget {

	private InlineLabel element;
	
	public TypedWidget(Typed t, AbstractFormulisWidget par) {
		super(t, par);
		element = new InlineLabel(getData().getValue());
		
		initWidget(element);

		element.setTitle(getData().getUri());
		element.setStyleName("typedLiteral");
		element.addClickHandler(this);
	}

	@Override
	public Typed getData() {
		return (Typed) super.getData();
	}
	
	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}
}
