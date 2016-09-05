package com.irisa.formulis.view.form;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.control.profile.ProfileLine;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.create.CreationTypeOracle;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class FormClassLineWidget extends FormLineWidget implements ValueChangeHandler<String>, ClickHandler {

	protected HorizontalPanel elementRow = new HorizontalPanel();
	protected Column elementCol = new Column(12, elementRow);
//	private FluidRow labelButtonLine = new FluidRow();
	private TextBox labelUriBox = new TextBox();
//	private Column labelCol = new Column(9, labelUriBox);
	private Button newElementButton = new Button("", IconType.EDIT);
//	private Column buttonCol = new Column(3, newElementButton);

	protected AbstractFormulisWidget fixedElement = null;

//	element
//		contentCol 9
//			contentRow
//			elementCol 12
//				elementRow
//					fixedElement
//					labelUriBox
//		buttonsCol 3
//			buttonsContainer
//				buttonsRow
//					resetElementButton
//					repeatLineButton
//					removeLineButton
//				profileRow
//					profileCheckbox
//					profileIndexPlus
//					profileIndexMinus
//					profileIndexBox

	public FormClassLineWidget(FormClassLine l, FormWidget par) {
		super(l, par);
		
		labelUriBox.setWidth("100%");
		labelUriBox.setPlaceholder("Name of this new element (Random by default)");
		labelUriBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getFormLine().setElementLabel(SafeHtmlUtils.htmlEscape(event.getValue()));
				String traitedLabel = UriUtils.encode(event.getValue()).replace(" ", "_");
				getFormLine().setElementUri(Controller.newElementUri(traitedLabel));
			}
		});
//		labelUriBox.addClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				fireLineSelectionEvent();
//			}
//		});
		if(! l.getElementLabel().isEmpty()) {
			labelUriBox.setText(l.getElementLabel());
		}

		newElementButton.setTitle("Créer un nouvel élement");
		newElementButton.addClickHandler(this);
		if(this.getData().getParent() != null && this.getData().getParent().isRoot()) {
			newElementButton.setVisible(false);
		}

		if(! l.isAnonymous()) {
			try {
				this.fixedElement = FormulisWidgetFactory.getWidget((URI)l.getFixedElement(), this, this);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			} catch(ClassCastException e) {
				ControlUtils.debugMessage("ClassLineWidget fixed:" + l.getFixedElement() + " isAno:" + l.isAnonymous());
				throw e;
			}
		}

		elementRow.add(fixedElement);
		elementRow.add(labelUriBox);
		elementRow.addStyleName("weblis-max-width");
		contentRow.add(elementCol);
		labelUriBox.addStyleName("weblis-max-width");

		this.setLineState(LINE_STATE.FINISHED);
		this.hideLabelBox();
	}

	@Override
	public FormClassLine getFormLine() {
		return (FormClassLine) super.getFormLine();
	}
	
	public void hideLabelBox() {
		labelUriBox.setVisible(false);
		newElementButton.setVisible(false);
	}
	
	public void showLabelBox() {
		labelUriBox.setVisible(true);
		newElementButton.setVisible(true);
	}

	@Override
	public void setLineState(LINE_STATE state, CreationTypeOracle oracl) {
		setLineState(state);
	}

	@Override
	public void setLineState(LINE_STATE state) {
		if(state == LINE_STATE.FINISHED) {
			this.resetElementButton.setEnabled(false);

			if(this.getData().getParent() != null && this.getData().getParent().isRoot()) {
				this.newElementButton.setVisible(false);
			}
		} else if(state == LINE_STATE.CREATION) {
			if( this.getData().getParent() != null && ! this.getParentWidget().getData().isRoot()) {
				this.getParentWidget().clear();
				this.getParentWidget().getParentWidget().setLineState(LINE_STATE.CREATION);
			}
		}
	}

	@Override
	public void onClickWidgetEvent(ClickWidgetEvent event) {
//		if(event.getSource() == this.fixedElement) {
			fireLineSelectionEvent();
//		}
	}
	
	@Override
	public void onClick(ClickEvent event) {
		ControlUtils.debugMessage("FormClassLine onClick");
		super.onClick(event);
		if(event.getSource() == this.newElementButton) {
			if(! this.getData().getParent().isRoot()) {
				this.getParentWidget().getParentWidget().setLineState(LINE_STATE.CREATION);
			}
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if(event.getSource() == this.labelUriBox) {
			this.getFormLine().setElementLabel(SafeHtmlUtils.htmlEscape(event.getValue()));
			String traitedLabel = UriUtils.encode(event.getValue()).replace(" ", "_");
			this.getFormLine().setElementUri(Controller.newElementUri(traitedLabel));
		}
	}

	@Override
	protected ProfileLine toProfileLine() {
		return this.getFormLine().toProfileClassLine();
	}

	@Override
	public void setVariableElement(AbstractFormulisWidget nWid) {
		// Ne devrait jamais arriver
	}

	@Override
	public AbstractFormulisWidget getFixedElement() {
		return this.fixedElement;
	}

	@Override
	public AbstractFormulisWidget getVariableElement() {
		return null;
	}
	
}
