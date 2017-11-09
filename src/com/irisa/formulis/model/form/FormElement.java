package com.irisa.formulis.model.form;

/**
 * Base data class for all form element
 * @author pmaillot
 *
 */
public interface FormElement {

	public String toLispql();
	public String toLispql(boolean isFinalRequest);
	
	public boolean isFinishable();
	public String getTag();
}
