package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Tuple extends BasicElementList {

	public Tuple(BasicElementContener par) {
		super(par);
	}

	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Tuple.class) {
			throw new FormElementConversionException("No conversion from Tuple to " + c);
		}
		return (T)this;
	}
	
	@Override
	public String toString() {
		return "Tuple: " + super.toString();
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		String contentString = "( ";
		Iterator<BasicElement> itCont = this.getContentIterator();
		while(itCont.hasNext()) {
			BasicElement elem = itCont.next();
			contentString += elem.toLispql(isFinalRequest);
			if(itCont.hasNext()) {
				contentString += " ; ";
			}
		}
		contentString += " ) ";
		return contentString;
	}

	@Override
	public String getTag() {
		return "Tuple";
	}

}
