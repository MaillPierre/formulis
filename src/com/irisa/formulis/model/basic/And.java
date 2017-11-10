package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class And extends BasicElementList {

	public And(BasicElementContener par) {
		super(par);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != And.class) {
			throw new FormElementConversionException("No conversion from And to " + c);
		}
		return (T) this;
	}
	
	@Override
	public String toString() {
		return "And: " + super.toString() ;
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		String contentString = "( ";
		Iterator<BasicElement> itCont = this.getContentIterator();
		while(itCont.hasNext()) {
			BasicElement elem = itCont.next();
			contentString += " and " + elem.toLispql(isFinalRequest);
		}
		contentString += " ) ";
		return contentString;
	}

	@Override
	public String getTag() {
		return "And";
	}
}
