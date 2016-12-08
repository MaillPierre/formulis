package com.irisa.formulis.model.basic;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class URI extends BasicLeafElement {
	
	public enum KIND {
		CLASS,
		PROPERTY,
		ENTITY,
		DATATYPE
	};
	private String uriString;
	private URI.KIND kind;
	private String labelString;
	
	public URI(String uri, URI.KIND kindFlag, String label) {
		uriString = uri;
		kind= kindFlag;
		labelString = label;
	}
	
	public String getUri() {
		return uriString;
	}
	
	public URI.KIND getKind() {
		return kind;
	}
	
	public String getLabel() {
		return labelString;
	}
	
	@Override
	public String toString() {
		return "URI: " + getKindToString(kind) + ":'" + labelString + "'(" + uriString + ")";
	}
	
	public static String getKindToString(URI.KIND k) {

		String kindString = "";
		switch(k) {
			case CLASS :
				kindString = "class";
			break;
			case PROPERTY :
				kindString = "property";
			break;
			case ENTITY :
				kindString = "entity";
			break;
			case DATATYPE:
				kindString = "datatype";
				break;
			default:
				kindString = "unknown";
				break;
		}
		return kindString;
	}
	
	public String kindToString() {
		return getKindToString(kind);
	}
	
	public static URI.KIND getKindFromString(String kString) {

		URI.KIND kind = KIND.ENTITY;
		switch(kString) {
			case "class" :
				kind = KIND.CLASS;
			break;
			case "property" :
				kind = KIND.PROPERTY;
			break;
			case "entity" :
				kind = KIND.ENTITY;
			break;
		}
		return kind;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != URI.class) {
			throw new FormElementConversionException("No conversion from URI to " + c);
		}
		return (T) this;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toLispql() {
		return "<" + this.uriString + ">";
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if(o.getClass() == URI.class) {
			URI oUri = (URI)o;
			result = this.uriString == oUri.uriString;
		}
		
		return result;
	}

	@Override
	public String getTag() {
		return "URI";
	}

}
