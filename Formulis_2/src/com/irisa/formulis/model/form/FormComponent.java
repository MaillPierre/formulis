package com.irisa.formulis.model.form;

import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.model.Place;

public abstract class FormComponent implements FormElement {

	protected FormComponent parent;
	protected Place place;
	private boolean finished = false;
	
	public FormComponent(FormComponent par) {
		parent = par;
	}
	
	public void setParent(FormComponent par) {
		parent = par;
	}
	
	public FormComponent getParent() {
		return parent;
	}
	
	public void setPlace(Place p) {
		this.place = p;
	}
	
	public Place getPlace() {
		return this.place;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public abstract boolean isLine();
	
	public abstract boolean isForm();
	
	protected abstract ProfileElement toProfileElement();
}
