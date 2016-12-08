package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Brackets extends BasicElementList implements BasicElement {
	
	public Brackets(BasicElementContener parent) {
		super(parent);
	}

	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Brackets.class) {
			throw new FormElementConversionException("No conversion from Brackets to " + c);
		}
		return (T) this;
	}
	
	@Override
	public String toString(){
		return "Brackets: " + super.toString() ;
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		String contentString = "{ ";
		Iterator<BasicElement> itCont = this.getContentIterator();
		while(itCont.hasNext()) {
			BasicElement elem = itCont.next();
			contentString += elem.toLispql(isFinalRequest) + " ";
		}
		contentString += "}";
		return contentString;
	}

	@Override
	public String getTag() {
		return "Brackets";
	}

}
