package com.irisa.formulis.view.custom;

import com.github.gwtbootstrap.client.ui.base.HtmlWidget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.irisa.formulis.model.form.FormElement;

public class SimpleFormWidget extends HtmlWidget implements HasClickHandlers {
	
	public FormElement data = null;

	public SimpleFormWidget(String tag, SafeHtml content, FormElement form) {
		super("simplewidget-"+tag, content.asString());
		data = form;
	}

	protected SimpleFormWidget(String tag) {
		super(tag);
	}

	public FormElement getData() {
		return data;
	}

	public void setData(FormElement data) {
		this.data = data;
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return this.addDomHandler(handler, ClickEvent.getType());
	}

}
