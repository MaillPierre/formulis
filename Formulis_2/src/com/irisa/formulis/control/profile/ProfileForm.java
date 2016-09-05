package com.irisa.formulis.control.profile;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormRelationLine;

public class ProfileForm extends ProfileLeafElement {

	private LinkedList<ProfileLine> lines = new LinkedList<ProfileLine>();
	private ProfileClassLine typeLine = null;

	public ProfileForm() {
	}
	
	public ProfileForm(ProfileClassLine cl) {
		this.typeLine = cl;
	}

	public ProfileClassLine getTypeLine() {
		return typeLine;
	}

	public void setTypeLine(URI type) {
		this.typeLine = new ProfileClassLine(type);
	}

	public void setTypeLine(ProfileClassLine typeLine) {
		this.typeLine = typeLine;
	}

	public LinkedList<ProfileLine> getLines() {
		return lines;
	}

	public void setLines(LinkedList<ProfileLine> lines) {
		this.lines = lines;
	}
	
	public void addLine(ProfileLine l) {
		this.lines.add(l);
	}
	
	public void addRelationLine(URI rel) {
		this.lines.add(new ProfileRelationLine(rel));
	}
	
	public void addAllLines(LinkedList<ProfileLine> lines) {
		this.lines.addAll(lines);
	}
	
	public Iterator<ProfileLine> lineIterator() {
		return this.lines.iterator();
	}
	
	public Form toForm(FormRelationLine parent) throws FormElementConversionException {
		ControlUtils.debugMessage("Form toForm( " + parent + " )");
		Form result = new Form(parent);

		ControlUtils.debugMessage("Form toForm typeLine " + this.getTypeLine());
		if(this.getTypeLine() != null) {
			result.addTypeLine(this.getTypeLine().toClassLine(result), false);
		}
		
		ControlUtils.debugMessage("Form toForm lines " + this.getLines());
		Iterator<ProfileLine> itLines = this.lineIterator();
		while(itLines.hasNext()) {
			ProfileLine line = itLines.next();
			ControlUtils.debugMessage("Form toForm line " + line);
			ControlUtils.debugMessage("Form toForm result (BEFORE)" + result);
			result.addLine(line.toFormLine(result));
			ControlUtils.debugMessage("Form toForm result (AFTER)" + result);
		}
		
		ControlUtils.debugMessage("Form toForm result " + result);
		
		return result;
	}
	
	@Override
	public String toString() {
		if(this.typeLine != null) {
			return "type: " + this.typeLine + " ; " + this.lines;
		}
		return this.lines.toString();
	}

}
