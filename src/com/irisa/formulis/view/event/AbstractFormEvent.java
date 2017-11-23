package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.event.callback.FormEventCallback;
import com.irisa.formulis.view.event.interfaces.FormEvent;

/**
 * Base class of all UI/Form events
 * @author pmaillot
 *
 */
public abstract class AbstractFormEvent implements FormEvent {
	
	protected Widget source = null;
	protected FormEventCallback callback = null;
	
	public AbstractFormEvent(Widget src) {
		this.source = src;
	}
	
	public AbstractFormEvent(Widget src, FormEventCallback cb) {
		this(src);
//		ControlUtils.debugMessage("DescribeUriEvent( " + src+", " + formEventCallback + " )");
		callback = cb;
	}
	
	@Override
	public Widget getSource() {
		return this.source;
	}

	@Override
	public FormEventCallback getCallback() {
		return this.callback;
	}
	
}
