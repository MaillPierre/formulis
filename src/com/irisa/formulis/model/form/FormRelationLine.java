package com.irisa.formulis.model.form;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.control.profile.ProfileRelationLine;
import com.irisa.formulis.model.basic.URI;

/**
 * Data element reprensenting a relation line, fixed element is a relation URI, variable element can be any FormElement
 * @author pmaillot
 *
 */
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
		String result = "";
		result = "is " + this.fixedElement.toLispql() + " of []";
		return result;
	}
	
	@Override
	public String toLispql(boolean isFinalRequest) {
		return this.toLispql(false, isFinalRequest);
	}

	@Override
	public String toLispql(boolean selected, boolean isFinalRequest) {
//		ControlUtils.debugMessage("FormRelationLine toLispql( selected=" + selected + " , isFinalRequest=" + isFinalRequest + " ) "+ this.fixedElement + " " + this.variableElement);
		String result = "";
		
		if(getParent() != null && this.getParent() instanceof Form && selected ) {
			result = this.getParent().toLispql(this);
		} else {
			result += this.fixedElement.toLispql();
			if(this.variableElement != null ) {
				result += " " + this.variableElement.toLispql(isFinalRequest) + " ";
			} else {
				result += " [] ";
			}
		}
//		if(isNew() && isFinalRequest) {
//			result += "; " + fixedElement.toLispql() + " a <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> ; "; 
//			result += fixedElement.toLispql() + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> " + (new Plain(((URI) fixedElement).getLabel()).toLispql()) + " " ;
//		}

//		ControlUtils.debugMessage("FormRelationLine toLispql FIN ( selected=" + selected + " , isFinalRequest=" + isFinalRequest + " ) "+ this.fixedElement + " " + this.variableElement);
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
	public boolean isFinishable() {
//		ControlUtils.debugMessage("FormRelation isFinished variable:" + this.variableElement  );
//		if(this.variableElement != null) {
//			ControlUtils.debugMessage("FormRelation isFinished variableIsFinished:" + this.variableElement.isFinished()  );
//		}
		return this.variableElement != null && this.variableElement.isFinishable();
	}

	@Override
	public String getTag() {
		return "RelationLine";
	}

	@Override
	public boolean equals(Object o) {
		try {
		if(o instanceof FormRelationLine) {
			FormRelationLine oRelation = (FormRelationLine) o;
			boolean fixedEqual = this.fixedElement.equals(oRelation.getFixedElement());
			if(this.variableElement != null && oRelation.getVariableElement() != null) {
				return fixedEqual && this.variableElement.equals(oRelation.getVariableElement());
			} else if(this.variableElement == null && oRelation.getVariableElement() == null) {
				return fixedEqual;
			} else {
				return false;
			}
		}
		} catch(Exception e) {
			ControlUtils.debugMessage("FormRelationLine equals EXCEPTION source:"+ this +" object:" + o);
		}
		return false;
	}

}
