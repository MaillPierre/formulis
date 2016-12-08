package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Or extends BasicElementList {

	public Or(BasicElementContener par) {
		super(par);
	}

	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Or.class) {
			throw new FormElementConversionException("No conversion from Or to " + c);
		}
		return (T) this;
	}
	
	@Override
	public String toString() {
		return "Or: " + super.toString();
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		String contentString = "( ";
		Iterator<BasicElement> itCont = this.getContentIterator();
		while(itCont.hasNext()) {
			BasicElement elem = itCont.next();
			contentString += " or " + elem.toLispql(isFinalRequest);
		}
		contentString += " ) ";
		return contentString;
	}

	@Override
	public String getTag() {
		return "Or";
	}

}
