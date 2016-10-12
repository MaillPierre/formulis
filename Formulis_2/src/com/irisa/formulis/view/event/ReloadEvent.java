package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.form.FormEventCallback;

public class ReloadEvent extends FormEvent {

	public ReloadEvent(Widget src, FormEventCallback formEventCallback) {
		super(src, formEventCallback);
	}

}
