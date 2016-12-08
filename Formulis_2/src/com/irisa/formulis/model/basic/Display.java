package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Display extends BasicElementList {

	public Display(BasicElementContener par) {
		super(par);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Display.class) {
			throw new FormElementConversionException("No conversion from Display to " + c);
		}
		return (T) this;
	}
	
	@Override
	public String toString() {
		return "Display: " + super.toString();
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		String contentString = "";
		Iterator<BasicElement> itCont = this.getContentIterator();
		while(itCont.hasNext()) {
			BasicElement elem = itCont.next();
			contentString += elem.toLispql(isFinalRequest);
			if(itCont.hasNext()) {
				contentString += " ; ";
			}
		}
		return contentString;
	}

	@Override
	public String getTag() {
		return "Display";
	}

}
