package com.irisa.formulis.control.profile;

import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.form.FormLine;
import com.irisa.formulis.model.form.FormRelationLine;

public class ProfileRelationLine extends ProfileLine {

	private URI relation;
	private ProfileElement variable;
	private int index = -1;
	
	public ProfileRelationLine(URI rel) {
		this(rel, null, "");
	}
	
	public ProfileRelationLine(URI rel, String i) {
		this(rel, null, i);
	}
	
	public ProfileRelationLine(URI rel, ProfileElement var) {
		this(rel, var, "");
	}
	
	public ProfileRelationLine(URI rel, ProfileElement var, String i) {
		super(i);
		this.relation = rel;
		this.variable = var;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setIndex(int i) {
		this.index = i;
	}
	
	public URI getRelation() {
		return this.relation;
	}
	
	public ProfileElement getVariable() {
		return this.variable;
	}
	
	public void setVariable(ProfileLeafElement var) {
		this.variable = var;
	}
	
	public FormRelationLine toRelationLine(Form parent) throws FormElementConversionException {
		FormRelationLine result = null;
		if(this.variable instanceof FormElement) {
			result =  new FormRelationLine(parent, relation, (FormElement) variable);
		} else if(this.variable instanceof ProfileForm) {
			result = new FormRelationLine(parent, relation);
			result.setVariableElement( ( (ProfileForm) variable).toForm(result) );
		} else if(this.variable == null) {
			result = new FormRelationLine(parent, relation);
		} else {
			throw new FormElementConversionException("toRelationLine unexpected variable element type, " + variable);
		}
		result.setInfo(getInfo());
		result.setWeight(getIndex());
		return result;
	}

	@Override
	public FormLine toFormLine(Form parent) throws FormElementConversionException {
		return toRelationLine(parent);
	}
	
	@Override
	public String toString() {
		if(this.variable != null) {
			return relation.toString() + " " + variable.toString();
		}
		return this.relation.toString();
	}

}
