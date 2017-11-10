package com.irisa.formulis.model.basic;


import com.irisa.formulis.model.exception.FormElementConversionException;

public class Prim extends BasicLeafElement {

	private String prim;
	
	public Prim(String p) {
		prim = p;
	}
	
	public String getPrim() {
		return prim;
	}
	
	@Override
	public String toString() {
		return "Prim: " + prim;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Prim.class) {
			throw new FormElementConversionException("No conversion from Prim to " + c);
		}
		return (T) this;
	}

	@Override
	public String toLispql() {
		return prim.toLowerCase();
	}

	@Override
	public String getTag() {
		return "Prim";
	}

	
	
}
