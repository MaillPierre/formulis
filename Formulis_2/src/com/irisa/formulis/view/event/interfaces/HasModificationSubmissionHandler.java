package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.ModificationSubmissionEvent;
import com.irisa.formulis.view.event.callback.FormEventCallback;

public interface HasModificationSubmissionHandler {

	
	public void addModificationSubmissionHandler(ModificationSubmissionHandler handler);
	public void fireModificationSubmission(ModificationSubmissionEvent event);
	public void fireModificationSubmission(FormEventCallback cb);
	
}
