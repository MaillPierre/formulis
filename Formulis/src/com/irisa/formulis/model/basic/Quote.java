package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Quote extends BasicElementList {

	public Quote(BasicElementContener par) {
		super(par);
	}

	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Quote.class) {
			throw new FormElementConversionException("No conversion from Quote to " + c);
		}
		return (T) this;
	}
	
	@Override
	public String toString() {
		return "Quote: " + super.toString();
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		String contentString = "\" ";
		Iterator<BasicElement> itCont = this.getContentIterator();
		while(itCont.hasNext()) {
			BasicElement elem = itCont.next();
			contentString += elem.toLispql(isFinalRequest) + " ";
		}
		contentString += "\"";
		return contentString;
	}

	@Override
	public String getTag() {
		return "Quote";
	}

}
