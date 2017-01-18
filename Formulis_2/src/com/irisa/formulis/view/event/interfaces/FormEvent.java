package com.irisa.formulis.view.event.interfaces;

import com.google.gwt.user.client.ui.Widget;
import com.irisa.formulis.view.event.callback.FormEventCallback;

public interface FormEvent {
	
	public Widget getSource();
	
	public FormEventCallback getCallback();

}
