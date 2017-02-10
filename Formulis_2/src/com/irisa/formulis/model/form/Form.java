package com.irisa.formulis.model.form;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.control.profile.ProfileForm;

/**
 * Classe de données pour représenter une édition en cours.
 * Un form peut être traduit vers une requête lispql pour interroger le serveur, en demandant la relation courante (en cours d'édition)
 * Etat de Form: Anonyme: aucun type, liste de type: plus d'un type, Typé: un seul type
 * @author pmaillot
 *
 */
public class Form extends FormComponent {

	private LinkedList<FormRelationLine> relationLines = new LinkedList<FormRelationLine>();
	private LinkedList<FormClassLine> typeLines = new LinkedList<FormClassLine>();
	private boolean hasMoreFlag = false;
	private boolean hasLessFlag = false;
	
	private FormClassLine mainTypeLine = null;

	public Form(FormComponent par) {
		super(par);
	}
	
	public Form(FormComponent par, FormClassLine typeL) {
		super(par);
		this.typeLines.add(typeL);
	}
	
	/**
	 * Ajoute une ligne de type et efface les autres lignes
	 * @param l
	 */
	public void setMainTypeLine(FormClassLine l) {
		clearContent();
		this.addTypeLine(l);
		this.mainTypeLine = l;
	}
	
	public void resetMainTypeLine() {
		this.typeLines.clear();
		this.mainTypeLine = null;
	}

	public void addTypeLine(FormClassLine l) {
		this.addTypeLine(l, false);
	}
	
	public void addTypeLine(FormClassLine l, boolean clear) {
		if(! this.typeLines.contains(l)) {
			if(l != null && l.getParent() != this) {
				l.setParent(this);
			}
			if(clear) {
				this.typeLines.clear();
			}
			this.typeLines.add(l);
		}
	}
	
	public void addAllTypeLines(Collection<FormClassLine> c) {
		this.typeLines.clear();
		Iterator<FormClassLine> itClass = c.iterator();
		while(itClass.hasNext()) {
			FormClassLine lineC = itClass.next();
			addTypeLine(lineC, false);
		}
	}
	
	public void appendAllTypeLines(Collection<FormClassLine> c) {
		this.typeLines.clear();
		Iterator<FormClassLine> itClass = c.iterator();
		while(itClass.hasNext()) {
			FormClassLine lineC = itClass.next();
			if(! this.typeLines.contains(lineC)) {
				addTypeLine(lineC, false);
			}
		}
	}
	
	public void addLine(FormLine line) {
		if(line.getParent() != this) {
			line.setParent(this);
		}
		if(line instanceof FormRelationLine) {
			relationLines.add((FormRelationLine) line);
		} else if (line instanceof FormClassLine) {
			this.typeLines.add((FormClassLine) line);
		}
//		this.formIndex.getOrDefault(line.getFixedElement(), new LinkedList<FormComponent>());
	}

	public void addAllLines(Collection<? extends FormLine> c) {
		Iterator<? extends FormLine> itC = c.iterator();
		while(itC.hasNext()) {
			FormLine line = itC.next();
			this.addLine(line);
		}
	}

	public void appendAllLines(Collection<? extends FormLine> c) {
		Iterator<? extends FormLine> itC = c.iterator();
		while(itC.hasNext()) {
			FormLine line = itC.next();
			if(! this.relationLines.contains(line)) {
				this.addLine(line);
			}
		}
	}
	
//	/**
//	 * Ajoute une ligne si elle n'est pas déjà présente dans le formulaire
//	 * @param l ligne à ajouter
//	 */
//	public void appendLine(FormLine l) {
//		if(! this.formLines.contains(l)) {
//			addLine(l);
//		}
//	}
//	
//	public void appendAllLines(Collection<? extends FormLine> c) {
//		Iterator<? extends FormLine> itC = c.iterator();
//		while(itC.hasNext()) {
//			FormLine line = itC.next();
//			this.appendLine(line);
//		}
//	}
	
	public void repeatRelationLine(FormRelationLine l) {
		if(relationLines.contains(l)) {
			int index = relationLines.indexOf(l);
			FormRelationLine newLine = new FormRelationLine( l.getParent(), l.getFixedElement());
			newLine.setInfo(l.getInfo());
			newLine.setWeight(l.getWeight());
			relationLines.add(index+1, newLine);
			for(int pos = 0; pos <= index ; pos++) {
				FormRelationLine incWeightLine = relationLines.get(pos);
				incWeightLine.setWeight(incWeightLine.getWeight() + 1);
			}
		}
	}
	
	public void removeRelationLine(FormLine l) {
//		ControlUtils.debugMessage("Form removeRelationLine( " + l + " )");
		if(l != null && this.relationLines.contains(l)) {
			this.relationLines.remove(l);
		}
	}
	
	public void removeClassLine(FormLine l) {
		if(l != null && this.typeLines.contains(l)) {
			this.typeLines.remove(l);
		}
	}
	
	public LinkedList<FormRelationLine> getRelationLines() {
		return relationLines;
	}
	
	public LinkedList<FormClassLine> getTypeLines() {
		return typeLines;
	}
	
	public FormClassLine getMainType() {
//		return typeLines.getFirst();
		return this.mainTypeLine;
	}
	
	public Iterator<FormRelationLine> relationLinesIterator() {
		return this.relationLines.iterator();
	}
	
	public Iterator<FormClassLine> typeLinesIterator() {
		return this.typeLines.iterator();
	}
	
	public boolean isEmpty() {
		return relationLines.isEmpty() && (typeLines.isEmpty());
	}
	
	public boolean isTypeList() {
		return this.typeLines.size() > 1 && ! isTyped();
	}
	
	public boolean isAnonymous() {
//		return anonymous;
		return this.typeLines.isEmpty() || (this.isTyped() && this.getMainType().isAnonymous());
	}
	
	public boolean isTyped() {
//		return this.typeLines.size() == 1;
		return this.mainTypeLine != null;
	}
	
	public void insertLine(int index, FormRelationLine line) {
		this.relationLines.removeFirstOccurrence(line);
		this.relationLines.add(index, line);
		if(isTyped() || isAnonymous()) {
			int nbLines = relationLines.size();
			Iterator<FormRelationLine> itRelLines = relationLines.iterator();
			while(itRelLines.hasNext()) {
				FormRelationLine relLine = itRelLines.next();
				relLine.setWeight(nbLines);
				nbLines--;
			}	
		}
	}
	
	public void insertLineAfter(FormRelationLine line, FormRelationLine after) {
		int index = this.relationLines.indexOf(after);
		insertLine(index, line);
	}
	
	/**
	 * Delete non-main types and relations
	 */
	public void clearContent() {
		this.relationLines.clear();
		this.typeLines.clear();		
	}
	
	/**
	 * Delete everything
	 */
	public void clear() {
		clearContent();
		this.mainTypeLine = null;
	}
	
	public void clearRelations() {
		this.relationLines.clear();
	}

	public String toLispql(FormLine selectedLine) {
		return toLispql(selectedLine, false);
	}
	
	public String toLispql(FormLine selectedLine, boolean isFinalRequest) {
//		ControlUtils.debugMessage("Form toLispql( selectedLine="+ (selectedLine != null) +" , isFinalRequest=" + isFinalRequest + ") typeLine=" + this.getType() + " isRoot=" + this.isRoot() );
		String result = "";
		
		// Filtrage des lignes qui ne sont aps de type ou selectionnées
		LinkedList<String> otherlines = new LinkedList<String>();
		Iterator<FormRelationLine> itRelLines = this.relationLinesIterator();
		while(itRelLines.hasNext()) {
			FormLine line = itRelLines.next();
			try {
			if(! line.equals(selectedLine) &&  line.isFinishable()) {
				String lineString =  line.toLispql(isFinalRequest);
//				ControlUtils.debugMessage("Form toLispql line added:" + lineString);
				otherlines.add(lineString);
			}
			} catch(Exception e) {
				ControlUtils.debugMessage("Form toLispql EXCEPTION line:" + line + " selectedLine: " + selectedLine + " isFinished:" + line.isFinishable());
				throw e;
			}
		}
		if( this.isTypeList()) {
			Iterator<FormClassLine> itTypeLines = this.typeLinesIterator();
			while(itTypeLines.hasNext()) {
				FormClassLine line = itTypeLines.next();
				if(! line.equals(selectedLine) && ! line.isAnonymous()) {
					String lineString =  line.toLispql(isFinalRequest);
					otherlines.add(lineString);
				}
			}
		}
//		ControlUtils.debugMessage("Form toLispql [lisql des lignes extrait] " + result);
		
		// Ligne selectionée
		if(selectedLine != null && relationLines.contains(selectedLine)) {
			result += "is " + selectedLine.getFixedElement().toLispql() + " of ";
		} 
//		ControlUtils.debugMessage("Form toLispql [ligne selectionnée] " + result);

		// Ligne de type
		if(! this.isAnonymous() && ! this.isTypeList() && this.getMainType() != null) {
			if(isFinalRequest ) {
				result += this.getMainType().getEntityUri().toLispql(isFinalRequest) + " " ;
//				result += this.getType().toLispql(isFinalRequest) + " ";
			} 
//			else {
				result += "[ " + this.getMainType().toLispql(isFinalRequest) + " ";
//			}
			if(! otherlines.isEmpty()) {
				result += " ; ";
			}
		} else if(this.isAnonymous() ) {
			result += " [ a thing ";
			// Si il y a quelque chose à venir après
			if(! otherlines.isEmpty() || getParent() != null && getParent() instanceof FormLine) {
				result += " ; ";
			}
		} else {
			result += " [ ";
		}
//		ControlUtils.debugMessage("Form toLispql [ligne de type] " + result + " otherlines: " + otherlines.size());
		
		// Ajout des autres lignes
		if(! otherlines.isEmpty()) {
			Iterator<String> itSLines = otherlines.iterator();
			while(itSLines.hasNext()) {
				String sLine = itSLines.next();
				result += sLine + " ";
				if(itSLines.hasNext()) {
					result += " ; ";
				}
			}
		}
//		ControlUtils.debugMessage("Form toLispql [autres lignes] " + result);
		
		// Ajout de la liaison vers le parent
		if(getParent() != null && getParent() instanceof FormLine && ! isFinalRequest && selectedLine != null) {
			FormLine parentLine = (FormLine) getParent();
			if(parentLine.getParent() != null  && parentLine.getParent() instanceof Form) {
				Form parentForm = parentLine.getParent();
				String parentFormString = parentForm.toLispql(parentLine);
				if((! otherlines.isEmpty()) || ! this.isAnonymous()) {
					result += " ; ";
				}
				result += parentFormString;
			}
		}
//		ControlUtils.debugMessage("Form toLispql [lien vers parent] " + result);
		
		// Fermeture de la requête
		result += " ]";
		
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
		if(! this.isAnonymous() && ! this.isTypeList()) {
//			result += typeLines.getFirst().toLispql() + " ; ";
			result += this.mainTypeLine.toLispql() + " ; ";
		}
		if(! this.typeLines.isEmpty()) {
			Iterator<FormClassLine> itClassLines = this.typeLinesIterator();
			while(itClassLines.hasNext()) {
				FormLine line = itClassLines.next();
				result += line.toString();
				if(itClassLines.hasNext()) {
					result += " ; ";
				}
			}
		}
		Iterator<FormRelationLine> itRelLines = this.relationLinesIterator();
		while(itRelLines.hasNext()) {
			FormLine line = itRelLines.next();
			result += line.toString();
			if(itRelLines.hasNext()) {
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
		ProfileForm fo = new ProfileForm();
		
		Iterator<FormClassLine> itType = this.typeLinesIterator();
		while(itType.hasNext()) {
			FormClassLine type = itType.next();
			if(! type.isAnonymous()) {
				fo.addTypeLine(type.toProfileClassLine());
			}
		}
		
		Iterator<FormRelationLine> itLine = this.relationLinesIterator();
		while(itLine.hasNext()) {
			FormRelationLine line = itLine.next();
			fo.addLine( line.toProfileRelationLine());
		}
		
		return fo;
	}
	
	public ProfileForm toProfileForm() {
		return (ProfileForm) toProfileElement();
	}

	@Override
	
	public boolean isFinishable() {
		boolean result = false;
		Iterator<FormRelationLine> itRel = getRelationLines().iterator();
		while(itRel.hasNext()) {
			FormRelationLine rel = itRel.next();
			result = result || rel.isFinishable();
		}
		return ((isTyped() && this.getMainType().isFinishable()) ||(result &&  isAnonymous()));
	}

	@Override
	public String getTag() {
		return "Form";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Form) {
			if(((Form) o).isAnonymous() && this.isAnonymous()) {
				return this.relationLines.equals(((Form) o).relationLines); 
			}
			return this.relationLines.equals(((Form) o).relationLines) && this.typeLines.equals(((Form) o).typeLines);
		}
		return super.equals(o);
	}

	public boolean hasMore() {
		return hasMoreFlag;
	}

	public void setHasMore(boolean hasMoreFlag) {
		this.hasMoreFlag = hasMoreFlag;
	}

	public boolean hasLess() {
		return hasLessFlag;
	}

	public void setHasLess(boolean hasLessFlag) {
		this.hasLessFlag = hasLessFlag;
	}

}
