package com.irisa.formulis.model.form;

import com.irisa.formulis.control.profile.ProfileElement;

/**
 * base data class for all form main, components (forms and lines)
 * @author pmaillot
 *
 */
public abstract class FormComponent implements FormElement {

	protected FormComponent parent;
	protected String tmpValue = "";
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

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	/**
	 * @return temporary value, used to keep in memory filled but not validated fields, or entity names passed to new forms
	 */
	public String getTempValue() {
		return tmpValue;
	}
	
	/**
	 * Set the temporary value of a Form component, used to keep in memory filled but not validated fields, or entity names passed to new forms
	 * @param value
	 */
	public void setTempValue(String value) {
		this.tmpValue = value;
	}
	
	public abstract boolean isLine();
	
	public abstract boolean isForm();
	
	protected abstract ProfileElement toProfileElement();
}
