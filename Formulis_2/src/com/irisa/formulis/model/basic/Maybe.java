package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Maybe extends BasicElementList {


	public Maybe(BasicElementContener par) {
		super(par);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Maybe.class) {
			throw new FormElementConversionException("No conversion from Maybe to " + c);
		}
		return (T) this;
	}
	
	@Override
	public String toString() {
		return "Maybe: " + super.toString();
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		String contentString = "maybe [ ";
		Iterator<BasicElement> itCont = this.getContentIterator();
		while(itCont.hasNext()) {
			BasicElement elem = itCont.next();
			contentString += elem.toLispql(isFinalRequest);
			if(itCont.hasNext()) {
				contentString += " ; ";
			}
		}
		contentString += " ] ";
		return contentString;
	}

	@Override
	public String getTag() {
		return "Maybe";
	}

}
