package com.irisa.formulis.control.profile;

import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormLine;

public class ProfileClassLine extends ProfileLine {
	
	private URI uriClass = null;
	private boolean anonymous = false;

	public ProfileClassLine(URI u, String inf) {
		super(inf);
		this.uriClass = u;
	}
	
	public ProfileClassLine(URI u) {
		this(u, "");
	}

	public ProfileClassLine(String inf) {
		super(inf);
		this.setAnonymous(true);
	}

	public ProfileClassLine() {
		this("");
	}
	
	public URI getClassUri() {
		if(isAnonymous()) {
			return null;
		}
		return this.uriClass;
	}
	
	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public FormClassLine toClassLine(Form parent) {
		return new FormClassLine( parent, this.uriClass);
	}

	@Override
	public FormLine toFormLine(Form parent) {
		return toClassLine(parent);
	}
	
	@Override
	public String toString() {
		return this.uriClass.toString();
	}

}
