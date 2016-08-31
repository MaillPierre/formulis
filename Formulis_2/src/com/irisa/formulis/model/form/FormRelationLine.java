package com.irisa.formulis.model.form;

import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.control.profile.ProfileRelationLine;
import com.irisa.formulis.model.basic.URI;

public class FormRelationLine extends FormLine {

	public FormRelationLine(FormComponent par, FormElement fixed, FormElement variable) {
		super(par, fixed, variable);
	}

	public FormRelationLine(FormComponent par, FormElement fixed) {
		this(par, fixed, null);
	}
	
	public FormRelationLine(FormRelationLine l) {
		this(l.getParent(), l.getFixedElement(), l.getVariableElement());
	}

	@Override
	public FormLine repeatLine() {
		return new FormRelationLine(this.getParent() , this.getFixedElement(), this.getVariableElement());
	}
	
	@Override
	public URI getFixedElement() {
		if(super.getFixedElement() instanceof URI) {
			return (URI) super.getFixedElement();
		}
		return null;
	}
	
	/**
	 * Retourne la requête Lispql basique pour 
	 * @return
	 */
	public String toRootLispql() {
		return "is " + this.fixedElement.toLispql() + " of []";
	}
	
	@Override
	public String toLispql(boolean isFinalRequest) {
		return this.toLispql(false, isFinalRequest);
	}

	@Override
	public String toLispql(boolean selected, boolean isFinalRequest) {
//		Utils.displayDebugMessage("RelationLine toLispql( selected=" + selected + " , isFinalRequest=" + isFinalRequest + " )");
		String result = "";

		if(getParent() != null && this.getParent() instanceof Form && selected ) {
			result = this.getParent().toLispql(this);
		} else {
			result += this.fixedElement.toLispql();
			if(this.variableElement != null ) {
				result += " " + this.variableElement.toLispql(isFinalRequest);
			} else {
				result += " [] ";
			}
		}

//		Utils.displayDebugMessage("RelationLine toLispql( selected=" + selected + " , isFinalRequest=" + isFinalRequest + " ) " + result);
		return result;
	}
	
	@Override
	public String toString() {
		return "RelationLine ( " + this.getWeight() + " ) " + this.toLispql();
	}

	@Override
	protected ProfileElement toProfileElement() {
		ProfileRelationLine result = null;
		if(this.variableElement instanceof ProfileElement) {
			result = new ProfileRelationLine((URI) this.fixedElement, (ProfileElement) this.variableElement);
		} else if(this.variableElement instanceof Form) {
			result = new ProfileRelationLine((URI) this.fixedElement, ((Form) this.variableElement).toProfileForm());
		} else {
			result = new ProfileRelationLine((URI) this.fixedElement);
		}
		result.setIndex(getWeight());
		result.setInfo(getInfo());
		return result;
	}
	
	public ProfileRelationLine toProfileRelationLine() {
		return (ProfileRelationLine) toProfileElement();
	}

	@Override
	public boolean isFinished() {
		return this.variableElement != null && this.variableElement.isFinished();
	}

	@Override
	public String getTag() {
		return "RelationLine";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof FormRelationLine) {
			boolean fixedEqual = this.fixedElement.equals(((FormRelationLine) o).getFixedElement());
			if(this.variableElement != null) {
				return fixedEqual && this.variableElement.equals(((FormRelationLine) o).getVariableElement());
			} else {
				return fixedEqual;
			}
		}
		return super.equals(o);
	}

}
