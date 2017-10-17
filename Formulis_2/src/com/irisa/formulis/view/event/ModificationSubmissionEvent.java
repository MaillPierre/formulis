package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.event.callback.FormEventCallback;

public class ModificationSubmissionEvent extends AbstractFormEvent {

	public ModificationSubmissionEvent(Widget src, FormEventCallback cb) {
		super(src, cb);
	}
	
	public ModificationSubmissionEvent(Widget src) {
		super(src);
	}

}
