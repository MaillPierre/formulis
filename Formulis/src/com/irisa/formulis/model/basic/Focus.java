package com.irisa.formulis.model.basic;

import java.util.Iterator;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Focus extends BasicElementList {

	private String id;
	
	public Focus(BasicElementContener par) {
		super(par);
		id=null;
	}
	
	public Focus(String ident, BasicElementContener par) {
		super(par);
		id=ident;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String ident) {
		id = ident;
	}

	@Override
	public String toString() {
		String result = "Focus(id:"+id+"): " + super.toString();
		return result;
	}
	
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Focus.class) {
			throw new FormElementConversionException("No conversion from Focus to " + c);
		}
		return (T) this;
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		String contentString = "";
		Iterator<BasicElement> itCont = this.getContentIterator();
		while(itCont.hasNext()) {
			BasicElement elem = itCont.next();
			contentString += elem.toLispql( isFinalRequest);
			if(itCont.hasNext()) {
				contentString += " ; ";
			}
		}
		return contentString;
	}

	@Override
	public String getTag() {
		return "Focus";
	}

}
