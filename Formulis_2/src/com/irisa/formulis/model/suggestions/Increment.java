package com.irisa.formulis.model.suggestions;

import com.irisa.formulis.model.basic.BasicElement;

public class Increment implements Comparable<Increment>{

	public enum KIND {
		SOMETHING,
		ENTITY,
		THING,
		CLASS,
		OPERATOR,
		PROPERTY,
		INVERSEPROPERTY,
		RELATION
	}
	
	private String id;
	private KIND kind;
	private int ratioLeft;
	private int ratioRight;
	private boolean isNew;
	private BasicElement element;
	
	public Increment(String i) {
		setId(i);
		setKind(KIND.SOMETHING);
		setRatioLeft(0);
		setRatioRight(0);
		setIsNew(false);
		setDisplayElement(null);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public KIND getKind() {
		return kind;
	}

	public void setKind(KIND kind) {
		this.kind = kind;
	}

	public int getRatioLeft() {
		return ratioLeft;
	}

	public void setRatioLeft(int ratioLeft) {
		this.ratioLeft = ratioLeft;
	}

	public int getRatioRight() {
		return ratioRight;
	}

	public void setRatioRight(int ratioRight) {
		this.ratioRight = ratioRight;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setIsNew(boolean nw) {
		this.isNew = nw;
	}

	public BasicElement getDisplayElement() {
		return element;
	}

	public void setDisplayElement(BasicElement element) {
		this.element = element;
	}
	
	@Override
	public String toString() {
		String result = kindToString(kind) + " id: " + id + " ";
		
		result += "(" + ratioLeft +") ";
		result += element.toString();
		result += " (" + ratioRight + ") ";
		result += kindToString(kind);
		if(isNew()) {
			result += " new";
		}
		
		return result;
	}
	
	public String getDisplayString() {
		return element.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass() == Increment.class) {
			return ((Increment)o).getId() == this.getId();
		}
		return false;
	}
	
	public static KIND kindFromString(String s) {
		KIND result = KIND.SOMETHING;
		
		switch(s) {
		case "entity":
			result = KIND.ENTITY;
		break;
		case "relation":
			result = KIND.RELATION;
		break;
		case "thing":
			result = KIND.THING;
		break;
		case "class":
			result = KIND.CLASS;
		break;
		case "operator":
			result = KIND.OPERATOR;
		break;
		case "property":
			result = KIND.PROPERTY;
		break;
		case "inverseProperty":
			result = KIND.INVERSEPROPERTY;
		break;
		}
		
		return result;
	}
	
	public static String kindToString(KIND k) {
		String result = "something";
		switch(k) {
		case ENTITY:
			result = "entity";
		break;
		case RELATION:
			result = "relation";
		break;
		case THING:
			result = "thing";
		break;
		case CLASS:
			result = "class";
		break;
		case OPERATOR:
			result = "operator";
		break;
		case PROPERTY:
			result = "property";
		break;
		case INVERSEPROPERTY:
			result = "inverseProperty";
		break;
		}
		return result;
	}

	@Override
	public int compareTo(Increment o) {
		if(o.getRatioRight() - this.getRatioRight() != 0) {
			return o.getRatioRight() - this.getRatioRight();
		} else if(o.getRatioLeft() - this.getRatioLeft() != 0) {
			return o.getRatioLeft() - this.getRatioLeft() ;
		} else {
			return o.getDisplayString().compareTo(this.getDisplayString()) ;
		}
	}
	
	@Override
	public int hashCode() {
		return getDisplayString().hashCode();
	}
	
}
