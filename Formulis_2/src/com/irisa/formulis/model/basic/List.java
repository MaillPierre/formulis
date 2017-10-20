package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

/**
 * List for Answers display
 * @author pmaillot
 *
 */
public class List extends BasicElementList implements BasicElement {
	
	public List(BasicElementContener parent) {
		super(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != List.class) {
			throw new FormElementConversionException("No conversion from DisplayList to " + c);
		}
		return (T) this;
	}
	
	@Override
	public String toString() {
		return "List: " + super.toString();
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
		return "List";
	}

}
