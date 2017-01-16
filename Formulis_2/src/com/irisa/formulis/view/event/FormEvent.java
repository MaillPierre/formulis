package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.view.form.FormEventCallback;

public abstract class FormEvent {
	
	protected Widget source = null;
	protected FormEventCallback callback = null;
	
	public FormEvent(Widget src) {
		this.source = src;
	}
	
	public FormEvent(Widget src, FormEventCallback formEventCallback) {
		this(src);
//		ControlUtils.debugMessage("DescribeUriEvent( " + src+", " + formEventCallback + " )");
		callback = formEventCallback;
	}
	
	public Widget getSource() {
		return this.source;
	}
	
	public FormEventCallback getCallback() {
		return this.callback;
	}
	
}
