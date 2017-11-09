package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.view.event.callback.FormEventCallback;

public class DescribeUriEvent extends AbstractFormEvent {
	
	private URI uriToDescribe = null;

	public DescribeUriEvent(Widget abstractFormElementWidget, FormEventCallback cb, URI u) {
		super(abstractFormElementWidget, cb);
//		ControlUtils.debugMessage("DescribeUriEvent( " + abstractFormElementWidget+", " + formEventCallback + ", " + u + " )");
		uriToDescribe = u;
	}
	
	public URI getUri() {
		return uriToDescribe;
	}

}
