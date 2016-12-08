package com.irisa.formulis.model.basic;

import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.FormElement;

public interface BasicElement extends FormElement {
	
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException;
	
	@Override
	public int hashCode();
	
}
