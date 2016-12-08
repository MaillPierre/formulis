package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.ReloadEvent;
import com.irisa.formulis.view.form.FormEventCallback;

public interface HasReloadHandler {

	public void addReloadHandler(ReloadHandler handler);
	public void fireReloadEvent(FormEventCallback cb);
	public void fireReloadEvent(ReloadEvent event);
	
}
