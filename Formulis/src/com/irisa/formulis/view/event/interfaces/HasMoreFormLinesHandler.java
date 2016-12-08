package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.MoreFormLinesEvent;
import com.irisa.formulis.view.form.FormEventCallback;

public interface HasMoreFormLinesHandler {

	public void addMoreFormLinesHandler(MoreFormLinesHandler handler);
	public void fireMoreFormLinesEvent(MoreFormLinesEvent event);
	public void fireMoreFormLinesEvent(FormEventCallback cb);

}
