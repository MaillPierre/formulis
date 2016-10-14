package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.basic.URIWidget;
import com.irisa.formulis.view.basic.URIWidget.DescribeUriCallback;
import com.irisa.formulis.view.form.AbstractFormElementWidget;
import com.irisa.formulis.view.form.FormEventCallback;

public class DescribeUriEvent extends FormEvent {

	public DescribeUriEvent(Widget abstractFormElementWidget, FormEventCallback formEventCallback) {
		super(abstractFormElementWidget, formEventCallback);
	}
	
	public URIWidget getSource() {
		return (URIWidget) super.getSource();
	}
	
	public DescribeUriCallback getCallback() {
		return (DescribeUriCallback) super.getCallback();
	}

}
