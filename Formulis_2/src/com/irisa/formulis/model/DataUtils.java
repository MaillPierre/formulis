package com.irisa.formulis.model;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.And;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.BasicElementList;
import com.irisa.formulis.model.basic.Display;
import com.irisa.formulis.model.basic.Focus;
import com.irisa.formulis.model.basic.Keyword;
import com.irisa.formulis.model.basic.Pair;
import com.irisa.formulis.model.basic.Plain;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.form.FormLine;
import com.irisa.formulis.model.form.FormRelationLine;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;

public class DataUtils {
	
	public static Keyword theKeyword = new Keyword("the");
	public static Keyword thingKeyword = new Keyword("thing");
	

	/**
	 * Simple classe utilitaire qui faire une traversée de l'arborescence donnée pour en extraire les premiers éléments qui ne sont pas des Focus ou des Display imbriqués
	 * @param elem
	 * @return
	 */
	public static LinkedList<BasicElement> getFirstDisplayableElements(BasicElement elem) {
		LinkedList<BasicElement> result = new LinkedList<BasicElement>();
	
		if(elem instanceof Display || elem instanceof Focus ) {
			BasicElementList listElem = (BasicElementList) elem;
			Iterator<BasicElement> itList = listElem.getContentIterator();
			while(itList.hasNext()) {
				BasicElement elemIt = itList.next();
				result.addAll(getFirstDisplayableElements(elemIt));
			}
		} else {
			result.add(elem);
		}
	
		return result;
	}

	public static FormElement extractDisplayElementFromIncrement(Increment inc) {
		try {
			switch(inc.getKind()) {
			case CLASS:
				return DataUtils.extractClassFromIncrement(inc);
			case PROPERTY:
				return DataUtils.extractPropertyFromIncrement(inc);
			case ENTITY:
				return DataUtils.extractEntityFromIncrement(inc);
			case SOMETHING:
				return DataUtils.extractSomethingFromIncrement(inc);
			default:
				return null;
			}
		} catch (FormElementConversionException e) {
			ControlUtils.exceptionMessage(e);
		}
		return null;
	}

	public static FormElement extractSomethingFromIncrement(Increment inc) throws FormElementConversionException {
		FormElement result = null;
		if(inc.getKind() == KIND.SOMETHING) {
			LinkedList<BasicElement> elements = getFirstDisplayableElements(inc.getDisplayElement());
			try {
				if(elements.size() == 1 && elements.getFirst() instanceof Typed) {
					result = elements.getFirst().as(Typed.class);
				} else if(elements.size() == 1 && elements.getFirst() instanceof Plain) {
					result = elements.getFirst();
				} else {
					throw new FormElementConversionException("extractSomethingDisplayElementFromIncrement expect fourth element to be one Typed or Plain : " + elements);
				}
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
		} else {
			throw new FormElementConversionException("extractSomethingDisplayElementFromIncrement expect increment of kind ENTITY");
		}
		return result;
	}

	public static FormElement extractPropertyFromIncrement(Increment inc) throws FormElementConversionException {
		FormElement result = null;
		if(inc.getKind() == KIND.PROPERTY) {
			LinkedList<BasicElement> elements = getFirstDisplayableElements(inc.getDisplayElement());
			try {
				if(elements.size() == 1 && elements.getFirst() instanceof Pair) {
					Pair pai = elements.getFirst().as(Pair.class);
					if(pai.getFirstLine().size() == 1 && pai.getFirstLine().getLast() instanceof Display) {
						Display disp3 = pai.getFirstLine().getFirst().as(Display.class);
						if(disp3.getContent().size() == 2 && disp3.getContent().getLast() instanceof URI) {
							result = disp3.getContent().getLast().as(URI.class);
						} else if(disp3.getContent().size() == 3 && disp3.getContent().getFirst() instanceof Keyword && disp3.getContent().getLast() instanceof Keyword){
							result = disp3.getContent().get(1);
						} else {
							throw new FormElementConversionException("extractPropertyUriFromIncrement expect last or third element to be URI, got " + disp3.getContent().size() + " " + disp3.getContent() + " from " + inc);
						}
					} else {
						throw new FormElementConversionException("extractPropertyUriFromIncrement expect fifth element to be DISPLAY, got " + pai.getFirstLine().getFirst().toString() + " from " + inc);
					}
				} else {
					throw new FormElementConversionException("extractPropertyUriFromIncrement expect fourth element to be PAIR, got " + elements.getLast().toString() + " from " + inc);
				}
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
		} else {
			throw new FormElementConversionException("extractClassUriFromIncrement expect increment of kind CLASS");
		}
		return result;
	}

	public static FormElement extractEntityFromIncrement(Increment inc) throws FormElementConversionException {
			FormElement result = null;
			if(inc.getKind() == KIND.ENTITY) {
				LinkedList<BasicElement> elements = getFirstDisplayableElements(inc.getDisplayElement());
				if(elements.size() == 1 && elements.getFirst() instanceof URI) { // Entité URI
					result = elements.getFirst();
				
				} else if(elements.size() == 1 && elements.getFirst() instanceof Pair) { // Objet anonyme, création d'un formulaire pour contenir son contenu
					result = DataUtils.pairToForm((Pair) elements.getFirst(), null);
				
				} else if(elements.size() == 2 && elements.getFirst() == theKeyword) {
				} else {
					throw new FormElementConversionException("extractEntityFromIncrement expect element to be URI or keyword or Pair " + elements);
				}
			} else {
				throw new FormElementConversionException("extractEntityFromIncrement expect increment of kind ENTITY");
			}
			return result;
		}

	public static FormElement extractClassFromIncrement(Increment inc) throws FormElementConversionException {
		FormElement result = null;
		if(inc.getKind() == KIND.CLASS) {
			LinkedList<BasicElement> elements = getFirstDisplayableElements(inc.getDisplayElement());
			if(elements.size() == 2 && elements.getLast() instanceof URI) {
				result = elements.getLast();
			} else {
				throw new FormElementConversionException("extractClassUriFromIncrement expect last element to be URI, got " + elements.toString());
			}
		} else {
			throw new FormElementConversionException("extractClassUriFromIncrement expect increment of kind CLASS");
		}
		return result;
	}

	public static FormLine pairToLine(Pair pair, Form parent) throws FormElementConversionException {
			FormLine result = null;
			LinkedList<BasicElement> line1FirstPair = pair.getFirstLine();
			if(pair.getFirstLine().size() == 1) {
				line1FirstPair = getFirstDisplayableElements(pair.getFirstLine().getLast());
			} 
			if(line1FirstPair.size() == 2 && line1FirstPair.getFirst().equals(new Keyword("has"))) { // propriété
				FormRelationLine newLine = new FormRelationLine(parent, line1FirstPair.getLast());
				LinkedList<BasicElement> line2FirstPair = pair.getSecondLine();
	//			Utils.displayDebugMessage("pairToLine potential variable element " + pair.getSecondLine());
				if(pair.getSecondLine().size() == 1) {
					line2FirstPair = getFirstDisplayableElements(pair.getSecondLine().getFirst());
				}
				if(line2FirstPair.size() == 1) {
					if(line2FirstPair.getFirst() instanceof Pair) {
						Pair potentialForm = (Pair) line2FirstPair.getFirst();
						newLine.setVariableElement(DataUtils.pairToForm(potentialForm, newLine));
					} else {
						newLine.setVariableElement(line2FirstPair.getFirst());
					}
				} else {
					throw new FormElementConversionException("pairToLine expect form line to end by one element: " + line2FirstPair);
				}
				result = newLine;
			} else { // Structure inconnue
				throw new FormElementConversionException("pairToLine expect form line to begin by \"has\" keyword: " + pair);
			}
			return result;
		}

	public static Form pairToForm(Pair pair, FormLine parent) throws FormElementConversionException {
		Form result = new Form(parent);
		if(pair.getFirstLine().size() == 1 && (pair.getFirstLine().getFirst() instanceof Display)) {
			LinkedList<BasicElement> firstLine = getFirstDisplayableElements(pair.getFirstLine().getFirst());
			if(firstLine.getFirst() instanceof Keyword ) { // "the something" something = "thing" ou classe
				Keyword firstKwd = (Keyword) firstLine.getFirst();
				BasicElement lastWord = firstLine.getLast();
				FormClassLine thingLine = new FormClassLine(parent, lastWord);
				if(firstKwd.getKeyword() == "the" && (lastWord == new Keyword("thing")) ) {
					thingLine = new FormClassLine(parent, ControlUtils.thingUri);
					thingLine.setAnonymous(true);
				}
				result.setTypeLine(thingLine, false);
			}
			
		} else {
			throw new FormElementConversionException("pairToForm expect firstline to contain only one element " + pair.getFirstLine());
		}
		//	Utils.displayDebugMessage("extractEntityFromIncrement firstPair.secondLine ( " + firstPair.getSecondLine().size() + " ) = " + firstPair.getSecondLine());
		if(pair.getSecondLine().size() == 1) {
			LinkedList<BasicElement> secondLine = getFirstDisplayableElements(pair.getSecondLine().getFirst());
			if(secondLine.getFirst() instanceof And) { // Liste de conjonction
				And andLines = (And) secondLine.getFirst();
				Iterator<BasicElement> itAndlines = andLines.getContentIterator();
				while(itAndlines.hasNext()) {
					BasicElement andElem = itAndlines.next();
					if( ! (andElem instanceof Keyword && andElem.equals(new Keyword("that"))) ) {
						if(andElem instanceof Pair) {
							Pair currentPair = (Pair) andElem;
							FormLine newLine = pairToLine(currentPair, result);
							result.addLine(newLine);
						}
					}
				}
			} else if(secondLine.getFirst() instanceof Pair) { // Une seule ligne (NON VERIFIE)
				Pair currentPair = (Pair) secondLine.getFirst();
				FormLine newLine = pairToLine(currentPair, result);
				result.addLine(newLine);
			}
		} else {
			throw new FormElementConversionException("pairToForm expect secondline to contain only one element " + pair.getSecondLine());
		}
		return result;
	}

	public static FormLine formLineFromIncrement(Increment inc, Form parent) {
		FormLine result = null;
		switch(inc.getKind()) {
		case ENTITY:
			try {
				FormElement uriResult = extractEntityFromIncrement(inc);
				result = new FormClassLine(parent, uriResult);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
			break;
		case CLASS:
			try {
				FormElement uriClass = extractClassFromIncrement(inc);
				result = new FormClassLine(parent, uriClass);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
			break;
			//		case OPERATOR:
		case PROPERTY:
			try {
				FormElement uriProp = extractPropertyFromIncrement(inc);
				result = new FormRelationLine(parent, uriProp);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
			break;
			//		case INVERSEPROPERTY:
		default:
			break;
		}
		return result;
	}

}
