package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.RelationCreationEvent;

public interface HasRelationCreationHandler {

	public void addRelationCreationHandler(RelationCreationHandler handler);
	public void fireRelationCreationEvent(RelationCreationEvent event);
	public void fireRelationCreationEvent();
	
}
