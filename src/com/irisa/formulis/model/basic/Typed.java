package com.irisa.formulis.model.basic;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Typed extends BasicLeafElement {

	private String uri;
	private String value;
	
	public Typed(String u, String v) {
		uri = u;
		value = v;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "Typed: " + value + "^^<" + uri.toString() + ">";
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Typed.class) {
			throw new FormElementConversionException("No conversion from Typed to " + c);
		}
		return (T) this;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toLispql() {
		return "\"" + value + "\"^^<" + uri.toString() + ">";
	}

	@Override
	public String getTag() {
		return "Typed";
	}

}
