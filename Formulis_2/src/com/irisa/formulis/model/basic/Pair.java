package com.irisa.formulis.model.basic;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Pair extends BasicElementContener {
	
	private boolean forceIndent;
	private LinkedList<BasicElement> line1;
	private LinkedList<BasicElement> line2;
	
	public Pair(BasicElementContener parent) {
		super(parent);
		forceIndent = false;
		line1 = new LinkedList<BasicElement>();
		line2 = new LinkedList<BasicElement>();
	}
	
	public Pair(boolean indent, BasicElementContener parent) {
		super(parent);
		forceIndent = indent;
		line1 = new LinkedList<BasicElement>();
		line2 = new LinkedList<BasicElement>();
	}
	
	public void addOnFirstLine(BasicElement e) {
		line1.add(e);
	}
	
	public void addAllOnFirstLine(LinkedList<BasicElement> e) {
		line1.addAll(e);
	}
	
	public void addOnSecondLine(BasicElement e) {
		line2.add(e);
	}
	
	public void addAllOnSecondLine(LinkedList<BasicElement> e) {
		line2.addAll(e);
	}

	public Iterator<BasicElement> getIteratorOnFirstLine() {
		return line1.iterator();
	}

	public Iterator<BasicElement> getIteratorOnSecondLine() {
		return line2.iterator();
	}
	
	public LinkedList<BasicElement> getFirstLine() {
		return line1;
	}
	
	public LinkedList<BasicElement> getSecondLine() {
		return line2;
	}
	
	public void setForceIndent(boolean indent) {
		forceIndent = indent;
	}
	
	public boolean getForceIndent() {
		return forceIndent;
	}
	
	@Override
	public String toString() {
		String result = "Pair";
		result += ": [";
		// First Line
		Iterator<BasicElement> itLine1 = line1.iterator();
		while(itLine1.hasNext()) {
			BasicElement elem = itLine1.next();
			result += elem.toString() + " ";
		}
		result += "] ; [ ";
		// Second Line
		Iterator<BasicElement> itLine2 = line2.iterator();
		while(itLine2.hasNext()) {
			BasicElement elem = itLine2.next();
			result += elem.toString() + " ";
		}
		result += " ]";
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Pair.class) {
			throw new FormElementConversionException("No conversion from Pair to " + c);
		}
		return (T) this;
	}


		@Override
		public String toLispql(boolean isFinalRequest) {
			String contentString = "";
			Iterator<BasicElement> itCont1 = line1.iterator();
			while(itCont1.hasNext()) {
				BasicElement elem = itCont1.next();
				contentString += elem.toLispql(isFinalRequest);
				if(itCont1.hasNext()) {
					contentString += " ";
				}
			}
			Iterator<BasicElement> itCont2 = line1.iterator();
			while(itCont2.hasNext()) {
				BasicElement elem = itCont2.next();
				contentString += elem.toLispql(isFinalRequest);
				if(itCont2.hasNext()) {
					contentString += " ";
				}
			}
			return contentString;
		}

		@Override
		public String getTag() {
			return "Pair";
		}
	
}
