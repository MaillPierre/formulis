package com.irisa.formulis.view;

import com.github.gwtbootstrap.client.ui.Label;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.*;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormComponent;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.form.FormRelationLine;
import com.irisa.formulis.view.basic.*;
import com.irisa.formulis.view.event.interfaces.ClickWidgetHandler;
import com.irisa.formulis.view.form.FormClassLineWidget;
import com.irisa.formulis.view.form.FormRelationLineWidget;
import com.irisa.formulis.view.form.FormWidget;

public class FormulisWidgetFactory {
	
	public static AbstractFormulisWidget getWidget(BasicElement e, AbstractFormulisWidget par) throws FormElementConversionException {
		return getWidget(e, par, null);
	}
	
	public static AbstractFormulisWidget getWidget(BasicElement e, AbstractFormulisWidget par, ClickWidgetHandler ch) throws FormElementConversionException {
//		Utils.displayDebugMessage("getWidget( " + e + ")");
		if(e != null) {
			if(e instanceof Display) {
					DisplayWidget diw = new DisplayWidget(e.as(Display.class), par);
					if(ch != null) {
						diw.addClickWidgetEventHandler(ch);
					}
					return diw;
			} else if( e instanceof URI) {
					URIWidget uw = new URIWidget(e.as(URI.class), par);
					if(ch != null) {
						uw.addClickWidgetEventHandler(ch);
					}
					return uw;
			} else if(e instanceof Focus) {
					FocusWidget fw = new FocusWidget(e.as(Focus.class), par);
					return fw;
			} else if(e instanceof Keyword) {
					KeywordWidget kww = new KeywordWidget(e.as(Keyword.class), par);
					if(ch != null) {
						kww.addClickWidgetEventHandler(ch);
					}
					return kww;
			} else if(e instanceof Pair) {
					return new PairWidget(e.as(Pair.class), par);
			} else if(e instanceof Space) {
					return new SpaceWidget(par);
			} else if(e instanceof Typed) {
					TypedWidget tw = new TypedWidget(e.as(Typed.class), par);;
					if(ch != null) {
						tw.addClickWidgetEventHandler(ch);
					}
					return tw;
			} else if(e instanceof Prim) {
					PrimWidget prw = new PrimWidget(e.as(Prim.class), par);
					if(ch != null) {
						prw.addClickWidgetEventHandler(ch);
					}
					return prw;
			} else if(e instanceof Plain) {
					PlainWidget plw = new PlainWidget(e.as(Plain.class), par);
					if(ch != null) {
						plw.addClickWidgetEventHandler(ch);
					}
					return plw;
			} else if(e instanceof Variable) {
					VariableWidget vw = new VariableWidget(e.as(Variable.class), par);
					if(ch != null) {
						vw.addClickWidgetEventHandler(ch);
					}
					return vw;
			} else if(e instanceof And) {
					AndWidget liw = new AndWidget(e.as(And.class),  par);
					if(ch != null) {
						liw.addClickWidgetEventHandler(ch);
					}
					return liw;
			} else if(e instanceof Maybe) {
					OperatorWidget maw = new OperatorWidget(e.as(Maybe.class), new Keyword("Maybe"), par);
					if(ch != null) {
						maw.addClickWidgetEventHandler(ch);
					}
					return maw;
			} else if(e instanceof Not) {
					OperatorWidget now = new OperatorWidget(e.as(Not.class), new Keyword("Not"), par);
					if(ch != null) {
						now.addClickWidgetEventHandler(ch);
					}
					return now;
			} else if(e instanceof Or) {
					OperatorWidget orw = new OperatorWidget(e.as(Or.class), new Keyword("Or"), par);
					if(ch != null) {
						orw.addClickWidgetEventHandler(ch);
					}
					return orw;
			} else if(e instanceof List) {
					return new ListWidget(e.as(List.class), par);
			} else if(e instanceof Brackets) {
					FramedWidget braw = new FramedWidget(e.as(Brackets.class), new Label("{"), new Label("}"), par);
					if(ch != null) {
						braw.addClickWidgetEventHandler(ch);
					}
					return braw;
			} else if(e instanceof Quote) {
					FramedWidget quow = new FramedWidget(e.as(Quote.class), new Label("«"), new Label("»"), par);
					if(ch != null) {
						quow.addClickWidgetEventHandler(ch);
					}
					return quow;
			} else {
				throw new FormElementConversionException("getDisplayWidget( " + e + " ) CAN'T CREATE DISPLAYWIDGET FROM THAT" );
			}
		} else {
			throw new FormElementConversionException("getDisplayWidget( " + e + " ) CAN'T CREATE DISPLAYWIDGET FROM THAT" );
		}
	}

	public static AbstractFormulisWidget getWidget(FormElement elem, AbstractFormulisWidget par) throws FormElementConversionException {
		if(elem != null && elem instanceof BasicElement) {
			return getWidget((BasicElement)elem, par);
		} else if(elem != null && elem instanceof FormComponent) {
//			ControlUtils.debugMessage("getWidget " + elem);
			if(elem instanceof Form) {
//				ControlUtils.debugMessage("getWidget Form " + elem);
				return new FormWidget((Form) elem, par);
			} else if(elem instanceof FormRelationLine && par instanceof FormWidget) {
//				ControlUtils.debugMessage("getWidget FormRelationLine " + elem);
				return new FormRelationLineWidget((FormRelationLine)elem, (FormWidget) par);
			} else if(elem instanceof FormClassLine && par instanceof FormWidget && ! ((FormClassLine) elem).isAnonymous()) {
//				ControlUtils.debugMessage("getWidget FormClassLine " + elem);
				return new FormClassLineWidget((FormClassLine) elem, (FormWidget)par);
			} else {
				ControlUtils.debugMessage("getWidget INCONNU " + elem);
			}
		} else {
			throw new FormElementConversionException("getDisplayWidget( " + elem + " ) CAN'T CREATE DISPLAYWIDGET FROM THAT" );
		}
		return null;
	}
	
}
