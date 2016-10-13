package com.irisa.formulis.model.form;

public interface FormElement {

	public String toLispql();
	public String toLispql(boolean isFinalRequest);
	
	public boolean isFinished();
	public String getTag();
	
	public boolean equals(Object o);
}
