package com.irisa.formulis.model.form;

import com.google.gwt.safehtml.shared.UriUtils;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.control.profile.ProfileClassLine;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.basic.URI.KIND;

public class FormClassLine extends FormLine {
	
	private String elementUri = "";
	private String elementLabel = "";
	private boolean anonymous = false;

	public FormClassLine(FormComponent par, FormElement fixed, FormElement varElement) {
		super(par, fixed, varElement);
		if(fixed.equals(ControlUtils.thingKeyword)) {
			anonymous = true;
		}
	}

	public FormClassLine(FormComponent par, FormElement fixed) {
		this(par, fixed, null);
	}
	
	public FormClassLine(FormClassLine l) {
		this(l.getParent(), l.getFixedElement(), l.getVariableElement());
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
//		Utils.debugMessage("ClassFormLine toLispql( selected=" + selected + ", isFinalRequest=" + isFinalRequest +" )");
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
				result += "; <" + ControlUtils.FORBIDDEN_URIS.rdfsLabel.getUri() + "> \"" + this.getEntityLabel() + "\"@fr"; 
			}
		}

//		ControlUtils.debugMessage("ClassFormLine toLispql( selected=" + selected + ", isFinalRequest=" + isFinalRequest +" ) = \"" + result + "\"");
		return result;
	}

	public URI getEntityUri() {
//		elementUri = Controller.newElementUri(elementLabel);
		if(this.getEntityLabel().isEmpty()) {
			return null;
		}
		return new URI(elementUri, KIND.ENTITY, elementLabel);
	}

	public void setEntityUri(String elementUri) {
//		ControlUtils.debugMessage("setElementUri : " + elementUri);
		this.elementUri = elementUri;
	}

	public String getEntityLabel() {
		return elementLabel;
	}

	public void setEntityLabel(String label) {
		this.elementLabel = label;
		String traitedLabel = UriUtils.encode(label).replace(" ", "_");
		setEntityUri(Controller.newElementUri(traitedLabel));
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
	public boolean isFinished() {
		return this.isNamed();
	}

	@Override
	public String getTag() {
		return "ClassLine";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof FormClassLine) {
			if(this.getEntityUri() != null) {
				return this.getFixedElement().equals(((FormClassLine) o).getFixedElement()) && this.getEntityUri().equals(((FormClassLine) o).getEntityUri());
			}
			return this.getFixedElement().equals(((FormClassLine) o).getFixedElement());
		}
		return false;
	}

}
