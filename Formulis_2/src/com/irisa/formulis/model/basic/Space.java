package com.irisa.formulis.model.basic;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Space extends BasicLeafElement {
	
	@Override
	public String toString() {
		return " ";
	}

	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Space.class) {
			throw new FormElementConversionException("No conversion from Space to " + c);
		}
		return (T) this;
	}

	@Override
	public String toLispql() {
		return " ";
	}

	@Override
	public String getTag() {
		return "Space";
	}

}
