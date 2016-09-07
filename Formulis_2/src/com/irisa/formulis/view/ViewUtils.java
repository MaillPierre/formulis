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
					} if(formElement instanceof Display) {
						Display dis = baseElement.as(Display.class);

						String disString = "";
						Iterator<BasicElement> itDis = dis.getContentIterator();
						while(itDis.hasNext()) {
							BasicElement elemDis = itDis.next();
							disString += toSimpleHtml(elemDis).asString();
						}
						disString += "";
						return SafeHtmlUtils.fromTrustedString(disString);
					} if(formElement instanceof Typed) {
						Typed t = baseElement.as(Typed.class);
						return SafeHtmlUtils.fromTrustedString("<span title='"+ t.getUri() +"' class='typedLiteral'>" + t.getValue() + "</span>");
					} if(formElement instanceof URI) {
						URI u = baseElement.as(URI.class);
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
					} if(formElement instanceof Focus) {
						Focus f = baseElement.as(Focus.class);
						String fString = "<span class='focus' id='"+ f.getId() +"' >";
						Iterator<BasicElement> itF = f.getContentIterator();
						while(itF.hasNext()) {
							BasicElement elemFoc = itF.next();
							fString += toSimpleHtml(elemFoc).asString();
						}
						fString += "</span>";
						return SafeHtmlUtils.fromTrustedString(fString);
					} if(formElement instanceof Pair) {
						Pair pair = baseElement.as(Pair.class);

						if(pair.getForceIndent()) {
							String pairTable = "<table class='pair'><tbody>";
							// Ligne 1
							pairTable += "<tr>";
							pairTable += "<table><tbody><tr>";
							Iterator<BasicElement> itL1 = pair.getIteratorOnFirstLine();
							while(itL1.hasNext()) {
								BasicElement elemPairL1 = itL1.next();
								pairTable += "<td>" + toSimpleHtml(elemPairL1).asString() + "</td>";
							}
							pairTable += "</tr>";
							// Ligne 2
							Iterator<BasicElement> itL2 = pair.getIteratorOnSecondLine();
							pairTable += "<tr>";
							//							pairTable += "<td><span class='indent'></span></td>";
							while(itL2.hasNext()) {
								BasicElement elemPairL2 = itL2.next();
								pairTable += "<td>" + toSimpleHtml(elemPairL2).asString() + "</td>";
							}
							pairTable += "</tr></tbody></table>";
							pairTable += "</tbody></table>";
							return SafeHtmlUtils.fromTrustedString(pairTable);
						} else {
							String pString = "<span class='pairline'  >";
							Iterator<BasicElement> itpL1 = pair.getIteratorOnFirstLine();
							while(itpL1.hasNext()) {
								BasicElement elempL1 = itpL1.next();
								pString += toSimpleHtml(elempL1).asString();
							}
							Iterator<BasicElement> itpL2 = pair.getIteratorOnSecondLine();
							while(itpL2.hasNext()) {
								BasicElement elempL2 = itpL2.next();
								pString += toSimpleHtml(elempL2).asString();
							}
							pString += "</span>";
							return SafeHtmlUtils.fromTrustedString(pString);
						}
					} if(formElement instanceof List) {
					} if(formElement instanceof Space) {
						return SafeHtmlUtils.fromTrustedString("<span class='space'></span>");
					} if(formElement instanceof Prim) {
						Prim prim = baseElement.as(Prim.class);
						return SafeHtmlUtils.fromTrustedString("<span class='primitive'> "+ prim.getPrim() +"</span>");
					} if(formElement instanceof Variable) {
						Variable v = baseElement.as(Variable.class);
						return SafeHtmlUtils.fromTrustedString("<span class='variable'> "+ v.getVariable() +"</span>");
					} if(formElement instanceof Brackets) {
						Brackets bra = baseElement.as(Brackets.class);
						String braString = "<span class='brackets' > { ";
						Iterator<BasicElement> itBra = bra.getContentIterator();
						while(itBra.hasNext()) {
							BasicElement elemBra = itBra.next();
							braString += toSimpleHtml(elemBra).asString();
						}
						braString += " } </span>";
						return SafeHtmlUtils.fromTrustedString(braString);
					} if(formElement instanceof Plain) {
						Plain p = baseElement.as(Plain.class);
						String plainVal = p.getPlain();
						String plainFull = plainVal;
						if(p.getLang() != null) {
							plainFull += "@" + p.getLang();
						}
						return SafeHtmlUtils.fromTrustedString("<span title='"+ plainFull +"' class='plainLiteral'>" + plainVal + "</span>");
						//			} if(e instanceof XML) {
					} if(formElement instanceof Tuple) {
					} if(formElement instanceof Sequence) {
					} if(formElement instanceof Or) {
					} if(formElement instanceof Not) {
					} if(formElement instanceof Maybe) {
					} if(formElement instanceof Quote) {
						Quote quo = baseElement.as(Quote.class);
						String quoString = "<span class='quote' > « ";
						Iterator<BasicElement> itQuo = quo.getContentIterator();
						while(itQuo.hasNext()) {
							BasicElement elemBra = itQuo.next();
							quoString += toSimpleHtml(elemBra).asString();
						}
						quoString += " » </span>";
						return SafeHtmlUtils.fromTrustedString(quoString);
					} else {
						return SafeHtmlUtils.fromTrustedString("");
					}
				} catch (FormElementConversionException e1) {
					e1.printStackTrace();
				}
			} else if(formElement instanceof FormComponent) {
				//				Utils.debugMessage("toSimpleHtml( " + formElement + " )");
				FormComponent formCompo = (FormComponent) formElement;
				if(formCompo.isForm()) {
					String formTable = "<table class=\"weblis-suggestion-table table table-bordered\"><tbody>";
					Form formform = (Form) formCompo;
					Iterator<FormClassLine> itTypeFormLine = formform.typeLinesIterator();
					while(itTypeFormLine.hasNext()) {
						formTable += "<tr><td>";
						FormLine line = itTypeFormLine.next();
						formTable += toSimpleHtml(line).asString();
						formTable += "</td></tr>";
					}
					Iterator<FormRelationLine> itRelFormLine = formform.relationLinesIterator();
					while(itRelFormLine.hasNext()) {
						formTable += "<tr><td>";
						FormLine line = itRelFormLine.next();
						formTable += toSimpleHtml(line).asString();
						formTable += "</td></tr>";
					}
					formTable += "</tbody></table>";
//					ControlUtils.debugMessage("toSimpleHtml Form: " + formElement + " ===> " + formTable);
					return SafeHtmlUtils.fromTrustedString(formTable);
				} else if(formCompo.isLine()) {
					FormLine line = (FormLine) formCompo;
					String lineString = "";
					lineString += toSimpleHtml(line.getFixedElement()).asString();
					if(line.getVariableElement() != null) {
						lineString += toSimpleHtml(line.getVariableElement()).asString();
					}
					lineString += "";
//					ControlUtils.debugMessage("toSimpleHtml Line: " + formElement + " ===> " + lineString);
					return SafeHtmlUtils.fromTrustedString(lineString);
				} else {
					ControlUtils.debugMessage("toSimpleHtml Elément inconnu (Compo)");
				}
			} else {
				ControlUtils.debugMessage("toSimpleHtml Elément inconnu (Autre)");
			}
		}
		return SafeHtmlUtils.fromTrustedString("");
	}

	public static SimpleFormWidget toSimpleWidget(FormElement elem) {
		return new SimpleFormWidget(elem.getTag(), toSimpleHtml(elem), elem);
	}

	public static <H extends FormEventChainHandler, E extends HasFormEventChainHandlers> void connectFormEventChain(E emitter, H handler) {
		emitter.addCompletionAskedHandler(handler);
		emitter.addElementCreationHandler(handler);
		emitter.addFinishFormHandler(handler);
		emitter.addFinishLineHandler(handler);
		emitter.addLineSelectionHandler(handler);
		emitter.addMoreCompletionsHandler(handler);
		emitter.addRelationCreationHandler(handler);
		emitter.addRemoveLineHandler(handler);
		emitter.addStatementChangeHandler(handler);
	}


}
