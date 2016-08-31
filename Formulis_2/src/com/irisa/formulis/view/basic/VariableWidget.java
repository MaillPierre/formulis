package com.irisa.formulis.view.basic;

import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.irisa.formulis.model.basic.Variable;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class VariableWidget extends AbstractFormulisWidget {

	private InlineLabel element;
	
	public VariableWidget(Variable kwd, AbstractFormulisWidget par) {
		super(kwd, par);
		element = new InlineLabel(getData().getVariable());
		
		initWidget(element);
		
		element.addClickHandler(this);
	}
	
	@Override
	public Variable getData() {
		return (Variable) super.getData();
	}

	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}

}
