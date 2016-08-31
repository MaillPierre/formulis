package com.irisa.formulis.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.Controller;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Transformation;
import com.irisa.formulis.view.ViewUtils;

public class Suggestions {

	private boolean canInsertEntity;
	private boolean canInsertRelation;
	private Increment currentRelationIncrement;
	private Increment currentEntityIncrement;
	private LinkedList<Increment> relationSuggestions;
	private LinkedList<Increment> entitySuggestions;
	private LinkedList<Transformation> transformationSuggestions;
	
	private HashMap<String, Increment> relationIdIncrementMap;
	private HashMap<String, Increment> entityIdIncrementMap;
	
	private Controller control;
	
	public Suggestions(Controller c) {
		control = c;
		
		canInsertEntity = true;
		canInsertRelation = true;
		currentRelationIncrement = null;
		currentEntityIncrement = null;
		relationSuggestions = new LinkedList<Increment>();
		entitySuggestions = new LinkedList<Increment>();
		transformationSuggestions = new LinkedList<Transformation>();
		relationIdIncrementMap = new HashMap<String, Increment>();
		entityIdIncrementMap = new HashMap<String, Increment>();
	}

	public boolean canInsertEntity() {
		return canInsertEntity;
	}

	public void setCanInsertEntity(boolean canInsertEntity) {
		this.canInsertEntity = canInsertEntity;
	}

	public boolean canInsertRelation() {
		return canInsertRelation;
	}

	public void setCanInsertRelation(boolean canInsertRelation) {
		this.canInsertRelation = canInsertRelation;
	}

	public Increment getCurrentRelationIncrement() {
		return currentRelationIncrement;
	}

	public void setCurrentRelationIncrement(Increment currentIncrement) {
		this.currentRelationIncrement = currentIncrement;
	}

	public Increment getCurrentEntityIncrement() {
		return currentEntityIncrement;
	}

	public void setCurrentEntityIncrement(Increment currentIncrement) {
		this.currentEntityIncrement = currentIncrement;
	}

	public Iterator<Increment> relationIterator() {
		return relationSuggestions.iterator();
	}
	
	public LinkedList<Increment> getRelationSuggestions() {
		return relationSuggestions;
	}

	public void setRelationSuggestions(LinkedList<Increment> l) {
		this.relationSuggestions = l;
		relationIdIncrementMap.clear();
		Iterator<Increment> itL = l.iterator();
		while(itL.hasNext()) {
			Increment inc = itL.next();

			relationIdIncrementMap.put(inc.getId(), inc);
		}
	}
	
	public void addRelationSuggestions(Increment i) {
		relationSuggestions.add(i);
		relationIdIncrementMap.put(i.getId(), i);
	}
	
	public void addAllRelationSuggestions(LinkedList<Increment> l) {
		Iterator<Increment> itL = l.iterator();
		while(itL.hasNext()) {
			Increment inc = itL.next();
			
			addRelationSuggestions(inc);
		}
	}
	
	public Iterator<Increment> entityIterator() {
		return entitySuggestions.iterator();
	}

	public LinkedList<Increment> getEntitySuggestions() {
		return entitySuggestions;
	}

	public void setEntitySuggestions(LinkedList<Increment> l) {
		this.entitySuggestions = l;
		entityIdIncrementMap.clear();
		Iterator<Increment> itL = l.iterator();
		while(itL.hasNext()) {
			Increment inc = itL.next();

			entityIdIncrementMap.put(inc.getId(), inc);
		}
	}
	
	public void addEntitySuggestions(Increment i) {
		entitySuggestions.add(i);
		entityIdIncrementMap.put(i.getId(), i);
	}
	
	public void addAllEntitySuggestions(LinkedList<Increment> l) {
		Iterator<Increment> itL = l.iterator();
		while(itL.hasNext()) {
			Increment inc = itL.next();
			
			addEntitySuggestions(inc);
		}
	}

	public LinkedList<Transformation> getTransformationSuggestions() {
		return transformationSuggestions;
	}

	public void setTransformationSuggestions(LinkedList<Transformation> transformationSuggestions) {
		this.transformationSuggestions = transformationSuggestions;
	}
	
	public Iterator<Transformation> transformationIterator() {
		return this.transformationSuggestions.iterator();
	}
	
	public void addTransformationSuggestions(Transformation t) {
		this.transformationSuggestions.add(t);
	}
	
	public void addAllTransformationSuggestions(LinkedList<Transformation> l) {
		this.transformationSuggestions.addAll(l);
	}
	
	public Increment entityIncrementById(String incId) {
		return this.entityIdIncrementMap.get(incId);
	}
	
	public Increment relationIncrementById(String incId) {
		return this.relationIdIncrementMap.get(incId);
	}
	
	@Override
	public String toString() {
		String result = "";
		
		if(canInsertRelation) {
			result += "Relations: " ;
			Iterator<Increment> itRel = relationSuggestions.iterator();
			while(itRel.hasNext()) {
				Increment inc = itRel.next();
				result += ViewUtils.getHTMLSpaceString() + inc + ViewUtils.getHTMLBreakLineString();
			}
		}
		if(canInsertEntity) {
			result += "Entity: ";
			Iterator<Increment> itEnt = entitySuggestions.iterator();
			while(itEnt.hasNext()) {
				Increment inc = itEnt.next();
				result += ViewUtils.getHTMLSpaceString() + inc + ViewUtils.getHTMLBreakLineString();
			}
		}
		
		return result;
	}
}
