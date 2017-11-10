package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.ReloadEvent;
import com.irisa.formulis.view.event.callback.ActionCallback;

public interface HasReloadHandler {

	public void addReloadHandler(ReloadHandler handler);
	public void fireReloadEvent(ActionCallback cb);
	public void fireReloadEvent(ReloadEvent event);
	
}
