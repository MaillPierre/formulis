package com.irisa.formulis.control.profile;

import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;

public class Profile {

	private String name;
	private String storeName;
	private ProfileForm form;
	
	public Profile(String n, String sto) {
		name= n;
		storeName = sto;
		form = new ProfileForm();
	}

	public ProfileForm getForm() {
		return form;
	}

	public void setForm(ProfileForm form) {
		this.form = form;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	public Form toForm() throws FormElementConversionException {
//		Form result = new Form(null);
		Form result = this.getForm().toForm(null);
		
//		if(this.getForm().getTypeLine() != null) {
//			result.setTypeLine(this.getForm().getTypeLine().toClassLine(result));
//		}
//		Iterator<ProfileLine> itLines = this.getForm().lineIterator();
//		while(itLines.hasNext()) {
//			ProfileLine line = itLines.next();
//			
//			result.addLine(line.toFormLine(result));
//		}
		
		return result;
	}
	
	@Override
	public String toString() {
		String result = "Profile " + this.name + ", store: " + this.storeName;
		if(this.form != null) {
			result += " " + this.form.toString();
		} else {
			result += " null";
		}
		
		return result;
	}
}
