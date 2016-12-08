package com.irisa.formulis.control.profile;

import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormLine;

public abstract class ProfileLine  extends ProfileElement {

	private String info;
	
	public ProfileLine() {
		this.info = "";
	}
	
	public ProfileLine(String inf) {
		this.info = inf;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public abstract FormLine toFormLine(Form parent) throws FormElementConversionException;

}
