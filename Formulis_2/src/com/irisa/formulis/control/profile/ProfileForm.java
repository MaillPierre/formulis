package com.irisa.formulis.control.profile;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormRelationLine;

public class ProfileForm extends ProfileLeafElement {

	private LinkedList<ProfileClassLine> typeLines = new LinkedList<ProfileClassLine>();
	private LinkedList<ProfileRelationLine> relationLines = new LinkedList<ProfileRelationLine>();

	public ProfileForm() {
	}
	
	public ProfileForm(ProfileClassLine cl) {
		this.typeLines.add(cl);
	}

	public LinkedList<ProfileClassLine> getTypeLines() {
		return typeLines;
	}

	public void setTypeLines(LinkedList<ProfileClassLine> type) {
		this.typeLines = type;
	}

	public void setTypeLine(ProfileClassLine typeLine) {
		this.typeLines.add(typeLine);
	}
	
	public void addTypeLine(ProfileClassLine lines) {
		this.typeLines.add(lines);
	}
	
	public void addAllTypeLines(LinkedList<ProfileClassLine> lines) {
		this.typeLines.addAll(lines);
	}

	public LinkedList<ProfileRelationLine> getLines() {
		return relationLines;
	}

	public void setLines(LinkedList<ProfileRelationLine> lines) {
		this.relationLines = lines;
	}
	
	public void addLine(ProfileRelationLine l) {
		this.relationLines.add(l);
	}
	
	public void addAllLines(LinkedList<ProfileRelationLine> lines) {
		this.relationLines.addAll(lines);
	}
	
	public Iterator<ProfileClassLine> typeIterator() {
		return this.typeLines.iterator();
	}
	
	public Iterator<ProfileRelationLine> relationIterator() {
		return this.relationLines.iterator();
	}
	
	public Form toForm(FormRelationLine parent) throws FormElementConversionException {
		ControlUtils.debugMessage("Form toForm( " + parent + " )");
		Form result = new Form(parent);

		ControlUtils.debugMessage("Form toForm typeLine " + this.getTypeLines());
		if(! this.getTypeLines().isEmpty()) {
			Iterator<ProfileClassLine> itType = this.typeIterator();
			while(itType.hasNext()) {
				ProfileClassLine type = itType.next();
				result.addTypeLine(type.toClassLine(result));
			}
		}
		
		ControlUtils.debugMessage("Form toForm lines " + this.getLines());
		Iterator<ProfileRelationLine> itLines = this.relationIterator();
		while(itLines.hasNext()) {
			ProfileRelationLine line = itLines.next();
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
		if(! this.typeLines.isEmpty()) {
			return "type: " + this.typeLines + " ; " + this.relationLines;
		}
		return this.relationLines.toString();
	}

}
