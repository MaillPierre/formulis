package com.irisa.formulis.view.basic;

import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.irisa.formulis.model.basic.Keyword;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class KeywordWidget extends AbstractFormulisWidget {

	private InlineLabel element;
	
	public KeywordWidget(Keyword kwd, AbstractFormulisWidget par) {
		super(kwd, par);
		element = new InlineLabel(getData().getKeyword());
		element.setStyleName("keyword");
		
		initWidget(element);
		
		element.addClickHandler(this);
	}

	@Override
	public Keyword getData() {
		return (Keyword) super.getData();
	}
	
	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}
}
