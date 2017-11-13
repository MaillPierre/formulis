package com.irisa.formulis.model.form;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.control.profile.ProfileClassLine;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.model.DataUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.basic.URI.KIND;

/**
 * Data object representing a class line, contains a class URI and a label or is anonymous
 * @author pmaillot
 *
 */
public class FormClassLine extends FormLine {
	
	private URI elementUri = null;
	private String elementLabel = "";
	private boolean anonymous = false;

	public FormClassLine(FormComponent par, FormElement fixed, FormElement varElement) {
		super(par, fixed, varElement);
		if(varElement != null && varElement instanceof URI) {
			this.setEntityUri(((URI)varElement));
		}
		if(fixed.equals(ControlUtils.thingKeyword)) {
			anonymous = true;
		}
	}

	public FormClassLine(FormComponent par, FormElement fixed) {
		this(par, fixed, null);
	}
	
	public FormClassLine(FormClassLine l) {
		this(l.getParent(), l.getFixedElement(), l.getVariableElement());
		this.elementUri = new URI(l.elementUri);
		this.elementLabel = l.elementLabel;
		this.anonymous = l.anonymous;
	}
	
	public FormClassLine(FormComponent par) {
		this(par, ControlUtils.thingKeyword);
	}
	
	@Override
	public String toLispql(boolean isFinalRequest) {
		return this.toLispql(false, isFinalRequest);
	}

	@Override
	public String toLispql(boolean selected, boolean isFinalRequest) {
		ControlUtils.debugMessage("ClassFormLine toLispql( selected=" + selected + ", isFinalRequest=" + isFinalRequest +" )");
		String result = "";

		if(getParent() != null && this.getParent() instanceof Form && selected ) {
			result = this.getParent().toLispql(this, isFinalRequest);
		} else {
			if(this.variableElement != null && this.variableElement.getClass() == Form.class) {
				// IMPOSSIBLE THEORIQUEMENT
			}
			if(! this.anonymous) {
				result += "a " + this.fixedElement.toLispql();
			}
			if(! this.anonymous && this.elementLabel != "" && isFinalRequest) {
				result += "; <" + ControlUtils.FORBIDDEN_URIS.rdfsLabel.getUri() + "> \"" + this.getEntityLabel() + "\"@"+ DataUtils.defaultLang(); 
			}
		}

//		ControlUtils.debugMessage("ClassFormLine toLispql( selected=" + selected + ", isFinalRequest=" + isFinalRequest +" ) = \"" + result + "\"");
		return result;
	}

	public URI getEntityUri() {
		return this.elementUri;
	}

	public void setEntityUri(URI varElement) {
		this.elementUri = varElement;
		this.setVariableElement(varElement);
	}

	public String getEntityLabel() {
		return elementLabel;
	}

	public void setEntityLabel(String label) {
		if(label.equals("")) {
			setEntityUri(null);
		} else {
			setEntityUri(new URI(Controller.newElementUri(label), KIND.ENTITY, label));
		}
		this.elementLabel = label;
	}
	
	@Override
	public String toString() {
		return "ClassLine ( " + this.getWeight() + " ) " + this.toLispql();
	}

	public boolean isAnonymous() {
		return anonymous;
	}
	
	public boolean isNamed() {
		return this.getEntityUri() != null;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.anonymous = isAnonymous;
	}

	@Override
	public ProfileElement toProfileElement() {
		if(isAnonymous()) {
			return new ProfileClassLine();
		}
		return new ProfileClassLine((URI) this.fixedElement);
	}
	
	public ProfileClassLine toProfileClassLine() {
		return (ProfileClassLine) toProfileElement();
	}

	@Override
	public boolean isFinishable() {
		return this.isNamed();
	}

	@Override
	public String getTag() {
		return "ClassLine";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof FormClassLine) {
			FormClassLine other = ((FormClassLine) o);
			if(this.getEntityUri() != null && other.getEntityUri() != null) {
				return this.getEntityUri().equals(other.getEntityUri());
			}
			return this.getFixedElement().equals(other.getFixedElement());
		}
		return false;
	}

}
