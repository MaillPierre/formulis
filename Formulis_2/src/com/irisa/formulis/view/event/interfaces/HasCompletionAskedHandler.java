package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.CompletionAskedEvent;

public interface HasCompletionAskedHandler {

	public void addCompletionAskedHandler(CompletionAskedHandler hand);
	
	public void fireCompletionAskedEvent();
	public void fireCompletionAskedEvent(String value);
	public void fireCompletionAskedEvent(CompletionAskedEvent event);
	
}
