package com.irisa.formulis.model.basic;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Keyword extends BasicLeafElement {

	private String keyword;
	
	public Keyword(String kwd) {
		keyword = kwd;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	@Override
	public String toString() {
		return "Keyword: " + keyword;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Keyword.class) {
			throw new FormElementConversionException("No conversion from Keyword to " + c);
		}
		return (T) this;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass() == Keyword.class) {
			return ((Keyword)o).getKeyword() == this.getKeyword();
		} else {
			return super.equals(o);
		}
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toLispql() {
		return keyword.toLowerCase();
	}

	@Override
	public String getTag() {
		return "Keyword";
	}
	
	

}
