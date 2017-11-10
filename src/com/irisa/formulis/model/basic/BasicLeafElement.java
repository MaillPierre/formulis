package com.irisa.formulis.model.basic;

import com.irisa.formulis.control.profile.ProfileLeafElement;
import com.irisa.formulis.model.exception.FormElementConversionException;

public abstract class BasicLeafElement extends ProfileLeafElement implements BasicElement {

	public BasicLeafElement() {
	}

	@Override
	public abstract String toLispql();

	@Override
	public String toLispql(boolean isFinalRequest) {
		return this.toLispql();
	}

	@Override
	public abstract <T extends BasicElement> T as(Class<T> c) throws FormElementConversionException ;
	
	@Override
	public boolean isFinishable() {
		return true;
	}

}
