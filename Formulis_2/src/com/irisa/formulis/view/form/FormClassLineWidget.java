package com.irisa.formulis.view.form;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.profile.ProfileLine;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.basic.URIWidget;
import com.irisa.formulis.view.create.CreationTypeOracle;
import com.irisa.formulis.view.event.ClickWidgetEvent;

public class FormClassLineWidget extends FormLineWidget implements ValueChangeHandler<String>, ClickHandler {

	protected HorizontalPanel elementRow = new HorizontalPanel();
	protected Column elementCol = new Column(12, elementRow);
//	private FluidRow labelButtonLine = new FluidRow();
	private TextBox labelUriBox = new TextBox();
//	private Column labelCol = new Column(9, labelUriBox);
	private URIWidget labelWid = null;

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
		labelUriBox.addValueChangeHandler(this);
		labelUriBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				ValueChangeEvent.fire(labelUriBox, labelUriBox.getValue());	
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					setLineState(LINE_STATE.FINISHED);
				}
			}
		});
		
		if(! l.getEntityLabel().isEmpty()) {
			labelUriBox.setText(l.getEntityLabel());
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
		elementRow.setWidth("100%");
		elementRow.setCellWidth(labelUriBox, "100%");
		elementRow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		contentRow.add(elementCol);
		labelUriBox.addStyleName("weblis-max-width");
		
		this.repeatLineButton.setEnabled(false);

		this.setLineState(LINE_STATE.FINISHED);
		this.hideLabelBox();
	}

	@Override
	public FormClassLine getFormLine() {
		return (FormClassLine) super.getFormLine();
	}
	
	public void hideLabelBox() {
		labelUriBox.setVisible(false);
		
		if(this.getData() != null && this.getFormLine().getEntityUri() != null) {
			labelWid = new URIWidget(this.getFormLine().getEntityUri(), null);
			elementRow.remove(labelUriBox);
			if(labelWid != null) {
				elementRow.add(labelWid);
				elementRow.setCellWidth(labelWid, "100%");
			}
		}
	}
	
	public void showLabelBox() {
		labelUriBox.setVisible(true);
		if(this.getData() != null) {
			if(labelWid != null) {
				elementRow.remove(labelWid);
			}
			elementRow.add(labelUriBox);
			elementRow.setCellWidth(labelUriBox, "100%");
		}
	}

	@Override
	public void setLineState(LINE_STATE state, CreationTypeOracle oracl) {
		setLineState(state);
	}

	@Override
	public void setLineState(LINE_STATE state) {
		this.resetElementButton.setEnabled(! this.getParentWidget().getData().isTypeList() && this.getFormLine().isNamed());
		if(state == LINE_STATE.FINISHED) {
			hideLabelBox();
		} else if(state == LINE_STATE.SUGGESTIONS) {
			showLabelBox();
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
		if(event.getSource() == this.resetElementButton) {
			ControlUtils.debugMessage("FormClassLine onClick reset");
			setLineState(LINE_STATE.SUGGESTIONS);
			this.fireFinishLineEvent(false);
		} else if(event.getSource() == this.removeLineButton) {
			ControlUtils.debugMessage("FormClassLine onClick remove");
			fireRemoveLineEvent();
		} 
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if(event.getSource() == this.labelUriBox) {
			getFormLine().setEntityLabel(SafeHtmlUtils.htmlEscape(event.getValue()));
			this.fireFinishLineEvent(this.getFormLine().isFinished());
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
