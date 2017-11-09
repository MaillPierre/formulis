package com.irisa.formulis.model.basic;

import com.irisa.formulis.model.exception.FormElementConversionException;

public class Plain extends BasicLeafElement {

	private String plain;
	private String lang;
	
	public Plain(String p) {
		plain = p;
		lang = null;
	}
	
	public Plain(String p, String l) {
		plain = p;
		lang = l;
	}
	
	public String getPlain() {
		return plain;
	}
	
	public String getLang() {
		return lang;
	}
	
	@Override
	public String toString() {
		String result = "Plain: " + plain ;
		if(lang != null) {
			result += "@" + lang;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException {
		if(c != Plain.class) {
			throw new FormElementConversionException("No conversion from Plain to " + c);
		}
		return (T) this;
	}

	@Override
	public String toLispql() {
		String result = "\"" + plain + "\"";
		if(lang != null) {
			result += "@" + lang;
		}
		return result;
	}

	@Override
	public String getTag() {
		return "Plain";
	}

}