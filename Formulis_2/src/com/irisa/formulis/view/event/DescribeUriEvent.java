package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.view.basic.URIWidget;
import com.irisa.formulis.view.event.callback.StringCallback;
import com.irisa.formulis.view.event.callback.FormEventCallback;
import com.irisa.formulis.view.event.callback.ActionCallback;
import com.irisa.formulis.view.form.AbstractFormElementWidget;

public class DescribeUriEvent extends FormEvent {
	
	private URI uriToDescribe = null;

	public DescribeUriEvent(Widget abstractFormElementWidget, FormEventCallback cb, URI u) {
		super(abstractFormElementWidget, cb);
//		ControlUtils.debugMessage("DescribeUriEvent( " + abstractFormElementWidget+", " + formEventCallback + ", " + u + " )");
		uriToDescribe = u;
	}
	
	public URI getUri() {
		return uriToDescribe;
	}
	
	public StringCallback getCallback() {
		return (StringCallback) super.getCallback();
	}

}
