package com.irisa.formulis.view.form;

import com.github.gwtbootstrap.client.ui.Column;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.profile.ProfileLine;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormComponent;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.form.FormRelationLine;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.ViewUtils;
import com.irisa.formulis.view.create.CreationTypeOracle;
import com.irisa.formulis.view.create.SelectCreateWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.interfaces.SuggestionSelectionHandler;
import com.irisa.formulis.view.form.FormLineWidget.LINE_STATE;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget;

public class FormRelationLineWidget extends FormLineWidget implements SuggestionSelectionHandler {

	protected Column fixedElementCol = new Column(3); 
	protected Column variableElementCol = new Column(9);
	protected HorizontalPanel elementRow = new HorizontalPanel();
	protected Column elementCol = new Column(11, elementRow);
	
	protected AbstractFormulisWidget fixedElement = null;
	protected AbstractFormulisWidget variableElement = null;

	public FormRelationLineWidget(FormRelationLine l, FormWidget par) {
		super(l, par);
		variableElementCol.setSize(8); 

		try {
			this.fixedElement = FormulisWidgetFactory.getWidget(l.getFixedElement(), this, this);
		} catch (FormElementConversionException e) {
			ControlUtils.exceptionMessage(e);
		}
		fixedElementCol.add(fixedElement);

		// L'élément variable est fourni
		if(l.getVariableElement() != null ) {
			// Soit l'élément est un fomulaire
			if(l.getVariableElement() instanceof Form) {
				FormWidget newForm = new FormWidget((Form)l.getVariableElement(), this);
				this.setVariableElement(newForm);
			} else { // Soit c'est un élément normal DisplayElement
				BasicElement disElem = (BasicElement) l.getVariableElement();
				try {
					this.setVariableElement(FormulisWidgetFactory.getWidget(disElem, this, this));
				} catch (FormElementConversionException e) {
					ControlUtils.exceptionMessage(e);
				}
			}
			this.setLineState(LINE_STATE.FINISHED);
			
		// La ligne est totalement nouvelle (suggestions inutiles)
		} else if(this.getData().isNew()) {
			this.setLineState(LINE_STATE.CREATION);
		
		// La ligne attend ses suggestions
		} else {
//			CustomSuggestionWidget sugg = new CustomSuggestionWidget(this);
//			sugg.addSuggestionSelectionHandler(this);
//			sugg.addMoreCompletionsHandler(this);
//			sugg.addCompletionAskedHandler(this);
//			sugg.setPlaceholder("Valeur de " + this.getData().getFixedElement().toLispql());
//
//			variableElement = sugg;
//			variableElement.addClickWidgetEventHandler(this);
			setLineState(LINE_STATE.SUGGESTIONS);
		}

		if(this.variableElement != null) {
			variableElementCol.add(variableElement);
		}

		if( this.getData().getParent().isAnonymous()) {
			elementCol.setSize(12);
			elementCol.setOffset(0);
		} else {
			elementCol.setOffset(1);
			elementCol.setSize(11);
		}
		
		contentRow.add(elementCol);
		elementRow.add(fixedElement);
		elementRow.add(variableElement);
		elementRow.setWidth("100%");
		elementRow.setCellWidth(variableElement, "100%");
		elementRow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	}

	@Override
	public FormRelationLine getFormLine() {
		return (FormRelationLine) super.getFormLine();
	}

	@Override
	public void setLineState(LINE_STATE state) {
		setLineState(state, null);
	}

	@Override
	public void setLineState(LINE_STATE state, CreationTypeOracle oracl) {
		switch(state) {
		case SUGGESTIONS:
			CustomSuggestionWidget sugg = new CustomSuggestionWidget(this);
			sugg.addSuggestionSelectionHandler(this);
//			sugg.addMoreCompletionsHandler(this);
//			sugg.addCompletionAskedHandler(this);
			sugg.setPlaceholder("Value of " + this.getFormLine().getFixedElement().getLabel());
			variableElement = sugg;
//			variableElement.addClickWidgetEventHandler(this);
			getData().setVariableElement(null);
			setVariableElement(sugg);

			resetElementButton.setEnabled(false);
			break;
		case GUIDED_CREATION:
			resetElementButton.setEnabled(true);
			break;
		case CREATION:
			if(oracl != null) {
				setVariableElement(new SelectCreateWidget(this, oracl));
			} else {
				setVariableElement(new SelectCreateWidget(this));
			}
			resetElementButton.setEnabled(true);
			break;
		case FINISHED:
			resetElementButton.setEnabled(true);
			fireFinishLineEvent(true);
			break;
		}
		currentState = state;
	}
	
	@Override
	public void setVariableElement(AbstractFormulisWidget nWid) {
		if(nWid != null) {
			nWid.addClickWidgetEventHandler(this);
			if(nWid instanceof AbstractFormElementWidget) {
				AbstractFormElementWidget nFormElem = (AbstractFormElementWidget) nWid;
				ViewUtils.connectFormEventChain(nFormElem, this);
				if(nFormElem.getData() != null && nFormElem.getData() instanceof FormComponent) { // Ligne ou Form (théoriquement seulement Form possible)
					FormComponent formCompo = (FormComponent) nFormElem.getData();
					formCompo.setParent(this.getData());
				}
			}
			nWid.setParentWidget(this);
			variableElement = nWid;
			elementRow.clear();
			elementRow.add(fixedElement);
			elementRow.add(variableElement);
			elementRow.setCellWidth(nWid, "100%");
		} else {
			variableElementCol.clear();
			elementRow.add(fixedElement);
		}
	}

	@Override
	public void onClickWidgetEvent(ClickWidgetEvent event)  { 
//		ControlUtils.debugMessage("RelationLineWidget onClickWidgetEvent CLICK ON LINE " + event.getSource() + " " + event.getSource().getClass() + " " + event.getSource().equals(this.variableElement));
		if(event.getSource() == this.fixedElement) {
			fireLineSelectionEvent();
		} else if(this.variableElement instanceof CustomSuggestionWidget) {
			CustomSuggestionWidget sugg = (CustomSuggestionWidget) this.variableElement;
			if(! sugg.isMoreCompletionMode()) {
				fireLineSelectionEvent(event.getCallback());
			}
		} else if(this.variableElement instanceof SelectCreateWidget){
			try {
				FormElement newElem =  this.getVariableElement().getData();
				setVariableElement(FormulisWidgetFactory.getWidget( newElem, this));
				getData().setVariableElement(newElem);
				setLineState(LINE_STATE.FINISHED);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
		}
	}


	@Override
	public void onClick(ClickEvent event) {
//		ControlUtils.debugMessage("FormRelationLine onClick");
		if(event.getSource() == this.repeatLineButton 
				&& this.getParentWidget() != null) {
			FormWidget parWid = this.getParentWidget();
			Form parData = parWid.getData();
			parData.repeatRelationLine((FormRelationLine) this.getData());
			parWid.reload();
		}
		else if(event.getSource() == this.resetElementButton) {
//			ControlUtils.debugMessage("FormRelationLine onClick reset");
			setLineState(LINE_STATE.SUGGESTIONS);
			this.fireFinishLineEvent(false);
		} else if(event.getSource() == this.removeLineButton) {
//			ControlUtils.debugMessage("FormRelationLine onClick remove");
			fireRemoveLineEvent();
		}
	}

	@Override
	public void onSelection(SuggestionSelectionEvent event) {
//		ControlUtils.debugMessage("RelationLineWidget onSelection (" + event.getSuggestion().getElement() + ") ");
		try {
			FormElement elem = event.getSuggestion().getElement();
			AbstractFormulisWidget nWid = FormulisWidgetFactory.getWidget(elem, this);
			setVariableElement( nWid);
			getData().setVariableElement(elem);
			this.setLineState(LINE_STATE.FINISHED);
			fireStatementChangeEvent();
		} catch (FormElementConversionException e) {
			ControlUtils.exceptionMessage(e);
		} 
	}

	@Override
	protected ProfileLine toProfileLine() {
		return this.getFormLine().toProfileRelationLine();
	}

	@Override
	public AbstractFormulisWidget getFixedElement() {
		return this.fixedElement;
	}

	@Override
	public AbstractFormulisWidget getVariableElement() {
		return this.variableElement;
	}

}
