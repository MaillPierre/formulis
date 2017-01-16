package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.view.basic.URIWidget;
import com.irisa.formulis.view.event.DescribeUriEvent.DescribeUriCallback;
import com.irisa.formulis.view.form.AbstractFormElementWidget;
import com.irisa.formulis.view.form.FormEventCallback;

public class DescribeUriEvent extends FormEvent {
	
	public interface DescribeUriCallback extends FormEventCallback {
		/**
		 * @param description contains the server answer to be parsed 
		 */
		public void call(String description);
	}

	private URI uriToDescribe = null;

	public DescribeUriEvent(Widget abstractFormElementWidget, FormEventCallback formEventCallback, URI u) {
		super(abstractFormElementWidget, formEventCallback);
//		ControlUtils.debugMessage("DescribeUriEvent( " + abstractFormElementWidget+", " + formEventCallback + ", " + u + " )");
		uriToDescribe = u;
	}
	
	public URI getUri() {
		return uriToDescribe;
	}
	
	public DescribeUriEvent.DescribeUriCallback getCallback() {
		return (DescribeUriEvent.DescribeUriCallback) super.getCallback();
	}

}
