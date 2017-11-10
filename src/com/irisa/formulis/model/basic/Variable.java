package com.irisa.formulis.model.basic;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Variable extends BasicLeafElement {

	private String var;
	
	public Variable(String v) {
		var = v;
	}
	
	public String getVariable() {
		return var;
	}
	
	@Override
	public String toString() {
		return "Var: " + var;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Variable.class) {
			throw new FormElementConversionException("No conversion from Variable to " + c);
		}
		return (T) this;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toLispql() {
		return var;
	}

	@Override
	public String getTag() {
		return "Variable";
	}

}
