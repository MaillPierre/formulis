package com.irisa.formulis.model.form;

import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.model.Place;

public abstract class FormComponent implements FormElement {

	protected FormComponent parent;
	protected Place place;
	
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
	
	public abstract boolean isLine();
	
	public abstract boolean isForm();
	
	protected abstract ProfileElement toProfileElement();
}
