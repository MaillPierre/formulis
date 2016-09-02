package com.irisa.formulis.model.form;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.control.profile.ProfileForm;
import com.irisa.formulis.control.profile.ProfileLine;
import com.irisa.formulis.model.basic.URI;

/**
 * Classe de données pour représenter une édition en cours.
 * Un form peut être traduit vers une requête lispql pour interroger le serveur, en demandant la relation courante (en cours d'édition)
 * @author pmaillot
 *
 */
public class Form extends FormComponent {

	private LinkedList<FormLine> formLines = new LinkedList<FormLine>();
	private FormClassLine typeLine = null;
	private boolean anonymous;
	
	private HashMap<URI, LinkedList<FormComponent>> formIndex = new HashMap<URI, LinkedList<FormComponent>>(); // TEST

	public Form(FormComponent par) {
		super(par);
	}
	
	public Form(FormComponent par, FormClassLine typeL) {
		super(par);
		this.typeLine = typeL;
	}
	
	public FormClassLine getTypeLine() {
		return this.typeLine;
	}
	
	/**
	 * Ajoute une ligne de type et efface les autres lignes
	 * @param l
	 */
	public void setTypeLine(FormClassLine l) {
		this.setTypeLine(l, true);
	}
	
	public void setTypeLine(FormClassLine l, boolean clear) {
		if(l != null && l.getParent() != this) {
			l.setParent(this);
		}
		this.typeLine = l;
		if(clear) {
			this.formLines.clear();
		}
	}
	
	public void addLine(FormLine line) {
		if(line.getParent() != this) {
			line.setParent(this);
		}
		formLines.addLast(line);
//		this.formIndex.getOrDefault(line.getFixedElement(), new LinkedList<FormComponent>());
	}

	public void addAllLines(Collection<? extends FormLine> c) {
		Iterator<? extends FormLine> itC = c.iterator();
		while(itC.hasNext()) {
			FormLine line = itC.next();
			this.addLine(line);
		}
	}
	
	/**
	 * Ajoute une ligne si elle n'est pas déjà présente dans le formulaire
	 * @param l ligne à ajouter
	 */
	public void appendLine(FormLine l) {
		if(! this.formLines.contains(l)) {
			addLine(l);
		}
	}
	
	public void appendAllLines(Collection<? extends FormLine> c) {
		Iterator<? extends FormLine> itC = c.iterator();
		while(itC.hasNext()) {
			FormLine line = itC.next();
			this.appendLine(line);
		}
	}
	
	public void repeatLine(FormLine l) {
		if(formLines.contains(l)) {
			int index = formLines.indexOf(l);
			FormLine newLine = l.repeatLine();
			formLines.add(index+1, newLine);
		}
	}
	
	public void removeLine(FormLine l) {
		if(l != null && this.formLines.contains(l)) {
			this.formLines.remove(l);
			this.formIndex.remove(l.getFixedElement());
		}
	}
	
	public LinkedList<FormLine> getLines() {
		return formLines;
	}
	
	public Iterator<FormLine> linesIterator() {
		return this.formLines.iterator();
	}
	
	public boolean isEmpty() {
		return formLines.isEmpty() && (typeLine == null);
	}
	
	public boolean isAnonymous() {
//		return anonymous;
		return this.typeLine == null || this.typeLine.isAnonymous();
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	public void clear() {
		this.formLines.clear();
		this.formIndex.clear();
	}

	public String toLispql(FormLine selectedLine) {
		return toLispql(selectedLine, false);
	}
	
	public String toLispql(FormLine selectedLine, boolean isFinalRequest) {
//		Utils.debugMessage("Form toLispql( selectedLine="+ (selectedLine != null) +" , isFinalRequest=" + isFinalRequest + ") typeLine=" + typeLine + " isRoot=" + this.isRoot() );
		String result = "";
		
		// Filtrage des lignes qui ne sont aps de type ou selectionnées
		LinkedList<String> otherlines = new LinkedList<String>();
		Iterator<FormLine> itLines = this.linesIterator();
		while(itLines.hasNext()) {
			FormLine line = itLines.next();
			if(! line.equals(selectedLine) && ! line.equals(typeLine) && line.getVariableElement() != null) {
				String lineString =  line.toLispql();
				otherlines.add(lineString);
			}
		}
		
		// Ligne selectionée
		if(selectedLine != null && formLines.contains(selectedLine)) {
			if (selectedLine instanceof FormRelationLine) {
				result += "is " + selectedLine.getFixedElement().toLispql() + " of ";
			} 
		} 

		// Ligne de type
		if(! this.isAnonymous()) {
			if(isFinalRequest) {
				result += "<" + typeLine.getElementUri() + "> ";
			}
			result += "[ " + typeLine.toLispql(isFinalRequest);
			if(! otherlines.isEmpty()) {
				result += " ; ";
			}
		} else {
			result += " [ ";
		}
		
		// Ajout des autres lignes
		if(! otherlines.isEmpty()) {
//			if(this.typeLine != null || (selectedLine != null && selectedLine.getClass() == ClassLine.class)) {
//				result += " ; ";
//			}
			Iterator<String> itSLines = otherlines.iterator();
			while(itSLines.hasNext()) {
				String sLine = itSLines.next();
				result += sLine + " ";
				if(itSLines.hasNext()) {
					result += " ; ";
				}
			}
		}
		
		// Ajout de la liaison vers le parent
		if(getParent() != null && getParent() instanceof FormLine && selectedLine != null) {
			FormLine parentLine = (FormLine) getParent();
			if(parentLine.getParent() != null  && parentLine.getParent() instanceof Form) {
				Form parentForm = parentLine.getParent();
				String parentFormString = parentForm.toLispql(parentLine);
				if((! otherlines.isEmpty()) || typeLine != null) {
					result += " ; ";
				}
				result += parentFormString;
			}
		}
		
		// Fermeture de la requête
//		if(selectedLine != null && selectedLine.getClass() == RelationLine.class && formLines.contains(selectedLine)) {
			result += " ]";
//		}
		
//		Utils.debugMessage("Form toLispql( selectedLine="+ (selectedLine != null) +" , isFinalRequest=" + isFinalRequest + ") result " + result);
		return result;
	}

	@Override
	public String toLispql(boolean isFinalRequest) {
		return this.toLispql(null, isFinalRequest);
	}

	@Override
	public String toLispql() {
		return this.toLispql(false);
	}
	
	@Override
	public String toString() {
		String result = "";
		if(typeLine != null && ! anonymous) {
			result += typeLine + " ; ";
		}
		Iterator<FormLine> itLines = this.linesIterator();
		while(itLines.hasNext()) {
			FormLine line = itLines.next();
			result += line.toString();
			if(itLines.hasNext()) {
				result += " ; ";
			}
		}
		
		return result;
	}

	@Override
	public boolean isLine() {
		return false;
	}

	@Override
	public boolean isForm() {
		return true;
	}
	
	public boolean isRoot() {
		return this.getParent() == null;
	}

	@Override
	protected ProfileElement toProfileElement() {
		ProfileForm fo = new ProfileForm(this.typeLine.toProfileClassLine());
		
		Iterator<FormLine> itLine = this.linesIterator();
		while(itLine.hasNext()) {
			FormLine line = itLine.next();
			fo.addLine((ProfileLine) line.toProfileElement());
		}
		
		return fo;
	}
	
	public ProfileForm toProfileForm() {
		return (ProfileForm) toProfileElement();
	}

	@Override
	public boolean isFinished() {
		boolean result = true;
		Iterator<? extends FormLine> itC = linesIterator();
		while(itC.hasNext()) {
			FormLine line = itC.next();
			result = result && line.isFinished();
		}
		return result;
	}

	@Override
	public String getTag() {
		return "Form";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Form) {
			if(((Form) o).isAnonymous() && this.isAnonymous()) {
				return this.formLines.equals(((Form) o).formLines); 
			}
			return this.formLines.equals(((Form) o).formLines) && this.typeLine.equals(((Form) o).typeLine);
		}
		return super.equals(o);
	}

}
