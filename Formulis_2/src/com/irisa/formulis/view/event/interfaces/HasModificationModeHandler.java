package com.irisa.formulis.view.event.interfaces;

public interface HasModificationModeHandler {

	
	public void addModificationModeHandler(ModificationModeHandler handler);
	public void fireModificationModeChange(boolean modif);
}
