package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.event.callback.ActionCallback;

public class ReloadEvent extends AbstractFormEvent {

	public ReloadEvent(Widget src, ActionCallback formEventCallback) {
		super(src, formEventCallback);
	}

}
