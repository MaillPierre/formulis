package com.irisa.formulis.view;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.irisa.formulis.model.basic.URI;

public class URITooltip extends PopupPanel {
	
	private HTML widget;

	public URITooltip(String uri, URI.KIND kind) {
		super();
		widget = new HTML(uri);
		switch(kind) {
			case CLASS:
				widget.setStyleName("class");
			break;
			case PROPERTY:
				widget.setStyleName("property");
			break;
			case ENTITY:
				widget.setStyleName("entity");
			break;
			default:
				widget.setStyleName("datatype");
			break;
		}
		this.add(widget);
	}
	
}
