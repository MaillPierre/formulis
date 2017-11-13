package com.irisa.formulis.view.form;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
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
import com.irisa.formulis.view.basic.URIWidget;
import com.irisa.formulis.view.create.CreationTypeOracle;
import com.irisa.formulis.view.create.SelectCreateWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.interfaces.SuggestionSelectionHandler;
import com.irisa.formulis.view.form.FormWidget.LAST_ACTION;
import com.irisa.formulis.view.form.suggest.VariableSuggestionWidget;

public class FormRelationLineWidget 
	extends AbstractFormLineWidget 
	implements SuggestionSelectionHandler, HasAllDragAndDropHandlers  {

	protected Column fixedElementCol = new Column(3); 
	protected Column variableElementCol = new Column(8);
	protected HorizontalPanel elementRow = new HorizontalPanel();
	protected Column elementCol = new Column(11, elementRow);
	
	protected AbstractFormulisWidget fixedElement = null;
	protected AbstractFormulisWidget variableElement = null;
	
	protected boolean placeholderFlag = false; // Pour drag & drop
	protected boolean draggedLineFlag = false; // Pour drag & drop
	protected FormRelationLinePlaceHolderWidget linePlaceholder = new FormRelationLinePlaceHolderWidget(); // Pour drag & drop

	public FormRelationLineWidget(FormRelationLine l, FormWidget par) {
		super(l, par);
		
		if(Controller.areLinesDraggable()) {
			getElement().setDraggable(Element.DRAGGABLE_TRUE);
		}

		try {
			this.fixedElement = FormulisWidgetFactory.getWidget(l.getFixedElement(), this, this);
			if(this.fixedElement instanceof URIWidget) {
				((URIWidget) this.fixedElement).addDescribeUriHandler(this);
			}
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
//		element.addStyleName("weblis-line-frame");
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
	public void finish() {
		setLineState(LINE_STATE.FINISHED);
	}

	@Override
	public void setLineState(LINE_STATE state, CreationTypeOracle oracl) {
//		ControlUtils.debugMessage("FormRelationLineWidget setLineState");
		switch(state) {
		case SUGGESTIONS:
			VariableSuggestionWidget sugg = new VariableSuggestionWidget(this);
			sugg.addSuggestionSelectionHandler(this);
//			sugg.addMoreCompletionsHandler(this);
//			sugg.addLessCompletionsHandler(this);
			sugg.addCompletionAskedHandler(this);
			sugg.addElementCreationHandler(this);
			sugg.setPlaceholder("Value of " + this.getFormLine().getFixedElement().getLabel());
			variableElement = sugg;
			variableElement.addClickWidgetEventHandler(this);
			getData().setVariableElement(null);
			setVariableElement(sugg);
			this.getData().setFinished(false);
			resetElementButton.setEnabled(false);
			if(! this.getData().getTempValue().isEmpty()) {
				sugg.setText(this.getData().getTempValue());
			}
			break;
		case GUIDED_CREATION:
			resetElementButton.setEnabled(true);
			this.getData().setFinished(false);
			break;
		case CREATION:
			if(oracl != null) {
				setVariableElement(new SelectCreateWidget(this, oracl));
			} else {
				setVariableElement(new SelectCreateWidget(this));
			}
			resetElementButton.setEnabled(true);
			this.getData().setFinished(false);
			break;
		case FINISHED:
			resetElementButton.setEnabled(true);
			fireFinishableLineEvent(true);
			this.getData().setFinished(true);
			break;
		default:
			break;
		}
		if(currentState != state) {
			fireHistoryEvent();
		}
		currentState = state;
//		ControlUtils.debugMessage("FormRelationLineWidget setLineState END");
	}
	
	@Override
	public void setVariableElement(AbstractFormulisWidget nWid) {
//		ControlUtils.debugMessage("FormRelationLineWidget setVariableElement " + nWid.getClass().getSimpleName());
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
			if(nWid instanceof URIWidget) {
				((URIWidget) nWid).addDescribeUriHandler(this);
			}
			nWid.setParentWidget(this);
			variableElement = nWid;
			elementRow.clear();
			elementRow.add(fixedElement);
			elementRow.add(variableElement);
			elementRow.setCellWidth(nWid, "100%");
			if(variableElement instanceof FormWidget) {
				((FormWidget) variableElement).reload();
			}
		} else {
			variableElementCol.clear();
			elementRow.add(fixedElement);
		}
//		ControlUtils.debugMessage("FormRelationLineWidget setVariableElement END");
		if(nWid != this.getVariableElement()) {
			this.getParentWidget().setLastAction(LAST_ACTION.EDIT);
		}
	}
	
	public void resetLine() {
		setLineState(LINE_STATE.SUGGESTIONS);
		this.fireFinishableLineEvent(false);
	}
	
	public void forceCreation() {
		setLineState(LINE_STATE.CREATION);
	}

	@Override
	public void onClickWidgetEvent(ClickWidgetEvent event)  { 
//		ControlUtils.debugMessage("FormRelationLineWidget onClickWidgetEvent " + event.getSource().getClass().getSimpleName());
		if(event.getSource() == this.fixedElement) {
			fireLineSelectionEvent();
		} else if(this.variableElement instanceof VariableSuggestionWidget) {
				fireLineSelectionEvent(event.getCallback());
		} else if(this.variableElement instanceof SelectCreateWidget){
			try {
				FormElement newElem =  this.getVariableElement().getData();
				AbstractFormulisWidget newElemWidget = FormulisWidgetFactory.getWidget( newElem, this);
				setVariableElement(newElemWidget);
				getData().setVariableElement(newElem);
//				setLineState(LINE_STATE.FINISHED);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
		}
//		ControlUtils.debugMessage("FormRelationLineWidget onClickWidgetEvent " + event.getSource().getClass().getSimpleName() + " END");
	}


	@Override
	public void onClick(ClickEvent event) {
//		ControlUtils.debugMessage("FormRelationLine onClick");
		if(event.getSource() == this.repeatLineButton 
				&& this.getParentWidget() != null) {
			FormWidget parWid = this.getParentWidget();
			Form parData = parWid.getData();
			parData.repeatRelationLine((FormRelationLine) this.getData());
			fireHistoryEvent();
			parWid.reload();
		}
		else if(event.getSource() == this.resetElementButton) {
//			ControlUtils.debugMessage("FormRelationLine onClick reset");
			resetLine();
		} else if(event.getSource() == this.removeLineButton) {
//			ControlUtils.debugMessage("FormRelationLine onClick remove");
			fireRemoveLineEvent();
			fireHistoryEvent();
		}
	}

	@Override
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
//		ControlUtils.debugMessage("FormRelationLineWidget onSelection (" + event.getSuggestion().getElement() + ") ");
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
//		ControlUtils.debugMessage("FormRelationLineWidget onSelection (" + event.getSuggestion().getElement() + ") ");
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

	@Override
	public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
		return addDomHandler(handler, DragStartEvent.getType());
	}

	@Override
	public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
		return addDomHandler(handler, DragEndEvent.getType());
	}

	@Override
	public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
		return addDomHandler(handler, DragEnterEvent.getType());
	}

	@Override
	public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler) {
		return addDomHandler(handler, DragLeaveEvent.getType());
	}

	@Override
	public HandlerRegistration addDragHandler(DragHandler handler) {
		return addDomHandler(handler, DragEvent.getType());
	}

	@Override
	public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
		return addDomHandler(handler, DragOverEvent.getType());
	}

	@Override
	public HandlerRegistration addDropHandler(DropHandler handler) {
		return addDomHandler(handler, DropEvent.getType());
	}
	
	public void toggleLinePlaceHolder() {
		setLinePlaceHolderFlag(! this.placeholderFlag);
	}
	
	public void setLinePlaceHolderFlag(boolean placeholder) {
		this.placeholderFlag = placeholder;
		if(placeholder) {
			contentCol.add(linePlaceholder);
			contentCol.insert(linePlaceholder, 0);
		} else {
			contentCol.remove(linePlaceholder);
		}
	}
	
	public void setDraggedLineFlag(boolean draggedFlag) {
		if(draggedLineFlag) {
			setLinePlaceHolderFlag(true);
			contentCol.addStyleName("formulis-dragged");
		} 
		else {
			contentCol.removeStyleName("formulis-dragged");
		}
	}
	
	public class FormRelationLinePlaceHolderWidget extends Composite {
		
		private FluidRow element = new FluidRow();
		private Column content = new Column(12);
		private Paragraph labelContent = new Paragraph(" ");
		
		public FormRelationLinePlaceHolderWidget() {
			initWidget(element);
			
			content.add(labelContent);
			element.add(content);
			element.addStyleName("formulis-line-placeholder");
		}
		
	}

}
