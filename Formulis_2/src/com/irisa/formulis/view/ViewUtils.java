package com.irisa.formulis.view;

import java.util.Iterator;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.*;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormComponent;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.form.FormLine;
import com.irisa.formulis.model.form.FormRelationLine;
import com.irisa.formulis.view.custom.SimpleFormWidget;
import com.irisa.formulis.view.event.interfaces.FormEventChainHandler;
import com.irisa.formulis.view.event.interfaces.HasFormEventChainHandlers;

public class ViewUtils {

	public static String getHTMLSpaceString() {
		return "<span class=\"space\"></span>";
	}

	public static String getHTMLIndentString() {
		return "<span class=\"indent\"></span>";
	}

	public static HTML getHTMLIndent() {
		return new HTML("<span class=\"indent\"></span>");
	}

	public static String getHTMLBreakLineString() {
		return "<br/>";
	}

	public static SafeHtml toSimpleHtml(FormElement formElement) {
		if(formElement != null) {
			if(formElement instanceof BasicElement) {
				try {
					BasicElement baseElement = (BasicElement) formElement;
					if(formElement instanceof Keyword) {
						Keyword k = baseElement.as(Keyword.class);
						return SafeHtmlUtils.fromTrustedString( "<span class='keyword'> " + k.getKeyword() + " </span>" );
					} 
					if(formElement instanceof Display) {
						Display dis = baseElement.as(Display.class);
						return toSimpleHtmlDisplay(dis);
					} 
					if(formElement instanceof Typed) {
						Typed t = baseElement.as(Typed.class);
						return SafeHtmlUtils.fromTrustedString("<span title='"+ t.getUri() +"' class='typedLiteral'>" + t.getValue() + "</span>");
					} 
					if(formElement instanceof URI) {
						URI u = baseElement.as(URI.class);
						return toSimpleHtmlUri(u);
					} 
					if(formElement instanceof Focus) {
						Focus f = baseElement.as(Focus.class);
						return toSimpleHtmlFocus(f);
					} 
					if(formElement instanceof Pair) {
						Pair pair = baseElement.as(Pair.class);
						return toSimpleHtmlPair(pair);
					} 
					if(formElement instanceof List) {
						List list = baseElement.as(List.class);
						return toSimpleHtmlList(list);
					} 
					if(formElement instanceof Space) {
						return SafeHtmlUtils.fromTrustedString("<span class='space'></span>");
					} 
					if(formElement instanceof Prim) {
						Prim prim = baseElement.as(Prim.class);
						return SafeHtmlUtils.fromTrustedString("<span class='primitive'> "+ prim.getPrim() +"</span>");
					} 
					if(formElement instanceof Variable) {
						Variable v = baseElement.as(Variable.class);
						return SafeHtmlUtils.fromTrustedString("<span class='variable'> "+ v.getVariable() +"</span>");
					} 
					if(formElement instanceof Brackets) {
						Brackets bra = baseElement.as(Brackets.class);
						return toSimpleHtmlBrackets(bra);
					} 
					if(formElement instanceof Plain) {
						Plain p = baseElement.as(Plain.class);
						return toSimpleHtmlPlain(p);
					} 
					if(formElement instanceof And) {
						And a = baseElement.as(And.class);
						return toSimpleHtmlAnd(a);
					} 
					if(formElement instanceof Tuple) {
						Tuple tup = baseElement.as(Tuple.class);
						return toSimpleHtmlTuple(tup);
					} 
					if(formElement instanceof Sequence) {
						Sequence seq = baseElement.as(Sequence.class);
						return toSimpleHtmlSequence(seq);
					} 
					if(formElement instanceof Or) {
						Or or = baseElement.as(Or.class);
						return toSimpleHtmlOr(or);
					} 
					if(formElement instanceof Not) {
						Not not = baseElement.as(Not.class);
						return toSimpleHtmlNot(not);
					} 
					if(formElement instanceof Maybe) {
						Maybe mabe = baseElement.as(Maybe.class);
						return toSimpleHtmlMaybe(mabe);
					} 
					if(formElement instanceof Quote) {
						Quote quo = baseElement.as(Quote.class);
						return toSimpleHtmlQuote(quo);
					} else {
						ControlUtils.debugMessage("ViewUtils toSimpleJtml unable to convert " + baseElement.toString());
						return SafeHtmlUtils.fromTrustedString("");
					}
				} catch (FormElementConversionException e1) {
					ControlUtils.exceptionMessage(e1);
				}
			} else if(formElement instanceof FormComponent) {
				//				Utils.debugMessage("toSimpleHtml( " + formElement + " )");
				FormComponent formCompo = (FormComponent) formElement;
				if(formCompo.isForm()) {
					Form formform = (Form) formCompo;
					return toSimpleHtmlForm(formform);
				} else if(formCompo.isLine()) {
					FormLine line = (FormLine) formCompo;
					return toSimpleHtmlFormLine(line);
				} else {
					ControlUtils.debugMessage("toSimpleHtml Elément inconnu (Compo)");
				}
			} else {
				ControlUtils.debugMessage("toSimpleHtml Elément inconnu (Autre)");
			}
		}
		return SafeHtmlUtils.fromTrustedString("");
	}
	
	private static SafeHtml toSimpleHtmlList(List list) {
		// TODO Auto-generated method stub
		return null;
	}

	private static SafeHtml toSimpleHtmlMaybe(Maybe mabe) {
		// TODO Auto-generated method stub
		return null;
	}

	private static SafeHtml toSimpleHtmlNot(Not not) {
		// TODO Auto-generated method stub
		return null;
	}

	private static SafeHtml toSimpleHtmlOr(Or or) {
		// TODO Auto-generated method stub
		return null;
	}

	private static SafeHtml toSimpleHtmlSequence(Sequence seq) {
		// TODO Auto-generated method stub
		return null;
	}

	private static SafeHtml toSimpleHtmlTuple(Tuple tup) {
		// TODO Auto-generated method stub
		return null;
	}

	private static SafeHtml toSimpleHtmlAnd(And a) { // TODO Affichage d'un And dans une statement buggé
		String andTable = "<table class=\"table\"><tbody>";
		
		Iterator<BasicElement> itAnd = a.getContentIterator();
		while(itAnd.hasNext()) {
			BasicElement andElem = itAnd.next();
			SafeHtml safeAnd = toSimpleHtml(andElem);
			if(safeAnd != null) {
				andTable += "<tr><td>";
				andTable += safeAnd.asString();
				andTable += "</td></tr>";
			}
		}
		
		andTable += "</tbody></table>";
		return SafeHtmlUtils.fromTrustedString(andTable);
	}

	private static SafeHtml toSimpleHtmlForm(Form formform) {
		String formTable = "<table class=\"weblis-suggestion-table table table-bordered\"><tbody>";
		if(! formform.isAnonymous()) {
			Iterator<FormClassLine> itTypeFormLine = formform.typeLinesIterator();
			while(itTypeFormLine.hasNext()) {
				FormClassLine line = itTypeFormLine.next();
				SafeHtml safeLine = toSimpleHtml(line);
				if(! line.isAnonymous()) {
					formTable += "<tr><td>";
					formTable += safeLine.asString();
					formTable += "</td></tr>";
				}
			}
		}
		Iterator<FormRelationLine> itRelFormLine = formform.relationLinesIterator();
		while(itRelFormLine.hasNext()) {
			FormRelationLine line = itRelFormLine.next();
			SafeHtml safeLine = toSimpleHtml(line);
			if(safeLine != null) {
				formTable += "<tr><td>";
				formTable += toSimpleHtml(line).asString();
				formTable += "</td></tr>";
			}
		}
		formTable += "</tbody></table>";
//		ControlUtils.debugMessage("toSimpleHtml Form: " + formElement + " ===> " + formTable);
		return SafeHtmlUtils.fromTrustedString(formTable);
	}

	private static SafeHtml toSimpleHtmlFormLine(FormLine line) {
		String lineString = "";
		SafeHtml safeFixElem = toSimpleHtml(line.getFixedElement());
		if(safeFixElem != null) {
			lineString += safeFixElem.asString();
		}
		if(line.getVariableElement() != null) {
			SafeHtml safeVarElem = toSimpleHtml(line.getVariableElement());
			if(safeVarElem != null) {
				lineString += safeVarElem.asString();
			}
		}
		lineString += "";
//		ControlUtils.debugMessage("toSimpleHtml Line: " + formElement + " ===> " + lineString);
		return SafeHtmlUtils.fromTrustedString(lineString);
	}
	
	private static SafeHtml toSimpleHtmlDisplay(Display dis) {
		String disString = "";
		Iterator<BasicElement> itDis = dis.getContentIterator();
		while(itDis.hasNext()) {
			BasicElement elemDis = itDis.next();
			SafeHtml safeDis = toSimpleHtml(elemDis);
			if(safeDis != null) {
				disString += safeDis.asString();
			}
		}
		disString += "";
		return SafeHtmlUtils.fromTrustedString(disString);
	}

	private static SafeHtml toSimpleHtmlUri(URI u) {
		String uriString = "<span title='"+ u.getUri() +"' class='";
		if(u.getKind() == URI.KIND.CLASS) {
			uriString += "class";
		}
		else if(u.getKind() == URI.KIND.ENTITY) {
			uriString +="entity";
		}
		else if(u.getKind() == URI.KIND.PROPERTY) {
			uriString +="property";
		}
		uriString +="' > ";
		if(!u.getLabel().equals("")) {
			uriString += SafeHtmlUtils.htmlEscape(u.getLabel()) ;
		} else {
			uriString += SafeHtmlUtils.htmlEscape(u.getUri()) ;
		}
		uriString +=" </span>";
		return SafeHtmlUtils.fromTrustedString(uriString);
	}

	private static SafeHtml toSimpleHtmlFocus(Focus f) {
		String fString = "<span class='focus' id='"+ f.getId() +"' >";
		Iterator<BasicElement> itF = f.getContentIterator();
		while(itF.hasNext()) {
			BasicElement elemFoc = itF.next();
			SafeHtml safeFoc = toSimpleHtml(elemFoc);
			if(safeFoc != null) {
				fString += safeFoc.asString();
			}
		}
		fString += "</span>";
		return SafeHtmlUtils.fromTrustedString(fString);
	}

	private static SafeHtml toSimpleHtmlPair(Pair pair) {

		if(pair.getForceIndent()) {
			String pairTable = "<table class='pair'><tbody>";
			// Ligne 1
			pairTable += "<tr>";
			pairTable += "<table><tbody><tr>";
			Iterator<BasicElement> itL1 = pair.getIteratorOnFirstLine();
			while(itL1.hasNext()) {
				BasicElement elemPairL1 = itL1.next();
				SafeHtml safePL1 = toSimpleHtml(elemPairL1);
				if(safePL1 != null) {
					pairTable += "<td>" + safePL1.asString() + "</td>";
				}
			}
			pairTable += "</tr>";
			// Ligne 2
			Iterator<BasicElement> itL2 = pair.getIteratorOnSecondLine();
			pairTable += "<tr>";
			//							pairTable += "<td><span class='indent'></span></td>";
			while(itL2.hasNext()) {
				BasicElement elemPairL2 = itL2.next();
				SafeHtml safePL2 = toSimpleHtml(elemPairL2);
				if(safePL2 != null) {
					pairTable += "<td>" + safePL2.asString() + "</td>";
				}
			}
			pairTable += "</tr></tbody></table>";
			pairTable += "</tbody></table>";
			return SafeHtmlUtils.fromTrustedString(pairTable);
		} else {
			String pString = "<span class='pairline'  >";
			Iterator<BasicElement> itpL1 = pair.getIteratorOnFirstLine();
			while(itpL1.hasNext()) {
				BasicElement elempL1 = itpL1.next();
				SafeHtml safePL1 = toSimpleHtml(elempL1);
				if(safePL1 != null) {
					pString += safePL1.asString();
				}
			}
			Iterator<BasicElement> itpL2 = pair.getIteratorOnSecondLine();
			while(itpL2.hasNext()) {
				BasicElement elempL2 = itpL2.next();
				SafeHtml safePL2 = toSimpleHtml(elempL2);
				if(safePL2 != null) {
					pString += safePL2.asString();
				}
			}
			pString += "</span>";
			return SafeHtmlUtils.fromTrustedString(pString);
		}
	}

	private static SafeHtml toSimpleHtmlQuote(Quote quo) {
		String quoString = "<span class='quote' > « ";
		Iterator<BasicElement> itQuo = quo.getContentIterator();
		while(itQuo.hasNext()) {
			BasicElement elemQuo = itQuo.next();
			SafeHtml safeQuo = toSimpleHtml(elemQuo);
			if(safeQuo != null) {
				quoString += safeQuo.asString();
			}
		}
		quoString += " » </span>";
		return SafeHtmlUtils.fromTrustedString(quoString);
	}

	private static SafeHtml toSimpleHtmlPlain(Plain p) {
		String plainVal = p.getPlain();
		String plainFull = plainVal;
		if(p.getLang() != null) {
			plainFull += "@" + p.getLang();
		}
		return SafeHtmlUtils.fromTrustedString("<span title='"+ plainFull +"' class='plainLiteral'>" + plainVal + "</span>");
	}

	private static SafeHtml toSimpleHtmlBrackets(Brackets bra) {
		String braString = "<span class='brackets' > { ";
		Iterator<BasicElement> itBra = bra.getContentIterator();
		while(itBra.hasNext()) {
			BasicElement elemBra = itBra.next();
			SafeHtml safeBra = toSimpleHtml(elemBra);
			if(safeBra != null) {
				braString += safeBra.asString();
			}
		}
		braString += " } </span>";
		return SafeHtmlUtils.fromTrustedString(braString);
	}

	public static SimpleFormWidget toSimpleWidget(FormElement elem) {
		return new SimpleFormWidget(elem.getTag(), toSimpleHtml(elem), elem);
	}

	public static <H extends FormEventChainHandler, E extends HasFormEventChainHandlers> void connectFormEventChain(E emitter, H handler) {
		emitter.addCompletionAskedHandler(handler);
		emitter.addClassCreationHandler(handler);
		emitter.addDescribeUriHandler(handler);
		emitter.addElementCreationHandler(handler);
		emitter.addFinishFormHandler(handler);
		emitter.addFinishLineHandler(handler);
		emitter.addHistoryHandler(handler);
		emitter.addLessCompletionsHandler(handler);
		emitter.addLineSelectionHandler(handler);
		emitter.addMoreCompletionsHandler(handler);
		emitter.addMoreFormLinesHandler(handler);
		emitter.addRelationCreationHandler(handler);
		emitter.addReloadHandler(handler);
		emitter.addRemoveLineHandler(handler);
		emitter.addStatementChangeHandler(handler);
	}


}
