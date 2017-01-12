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
	private TextBox labelUriBox = new TextBox();
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

	public FormClassLineWidget(FormClassLine l, FormWidget par, String startValue) {
		super(l, par);
		
		labelUriBox.setWidth("100%");
		labelUriBox.setText(startValue);
		labelUriBox.addStyleName("input-block-level");
		labelUriBox.setPlaceholder("Name of this new element");
		labelUriBox.addValueChangeHandler(this);
		labelUriBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				ValueChangeEvent.fire(labelUriBox, labelUriBox.getValue());	
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					finish();
				}
			}
		});
		
		if(! l.getEntityLabel().isEmpty()) {
			labelUriBox.setText(l.getEntityLabel());
			ValueChangeEvent.fire(labelUriBox, labelUriBox.getValue());
		} else if(! l.getTempValue().isEmpty()) {
			labelUriBox.setText(l.getTempValue());
			ValueChangeEvent.fire(labelUriBox, labelUriBox.getValue());
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

		elementRow.setWidth("100%");
		elementRow.setCellWidth(labelUriBox, "100%");
		elementRow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		contentRow.add(elementCol);
		
		this.repeatLineButton.setEnabled(false);

		reload();
	}

	public FormClassLineWidget(FormClassLine l, FormWidget par) {
		this(l, par, "");
	}

	@Override
	public FormClassLine getFormLine() {
		return (FormClassLine) super.getFormLine();
	}
	
	protected void reload() {
		elementRow.clear();
		elementRow.add(fixedElement);
		if(this.getData().isFinished()) {
			finish();
		} else {
			setLineState(LINE_STATE.SUGGESTIONS);
		}
		if(this.getParentWidget().getData().isTypeList()){
			hideLabelBox();
		}
		if(this.getData().getParent().isTypeList() || this.getData().getParent().isTyped()) {
			this.removeLineButton.setEnabled(false);
		}
	}
	
	public void finish() {
		if(this.getData().isFinishable() && ! this.getData().isFinished()) {
			setLineState(LINE_STATE.FINISHED);
		}
	}
	
	public void hideLabelBox() {
//		ControlUtils.debugMessage("FormClassLineWidget hideLabelBox");
		labelUriBox.setVisible(false);
		
		if(this.getData() != null && this.getFormLine().getEntityUri() != null) {
			labelWid = new URIWidget(this.getFormLine().getEntityUri(), null);
			elementRow.remove(labelUriBox);
			if(labelWid != null) {
				elementRow.add(labelWid);
				elementRow.setCellWidth(labelWid, "100%");
			}
		}
//		ControlUtils.debugMessage("FormClassLineWidget hideLabelBox END");
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
			this.getData().setFinished(true);
		} else if(state == LINE_STATE.SUGGESTIONS) {
			showLabelBox();
			this.getData().setFinished(false);
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
//		ControlUtils.debugMessage("FormClassLine onClick");
		if(event.getSource() == this.resetElementButton) {
//			ControlUtils.debugMessage("FormClassLine onClick reset");
			setLineState(LINE_STATE.SUGGESTIONS);
			this.fireFinishableLineEvent(this.getFormLine().isFinishable());
		} else if(event.getSource() == this.removeLineButton) {
//			ControlUtils.debugMessage("FormClassLine onClick remove");
			fireRemoveLineEvent();
		} 
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if(event.getSource() == this.labelUriBox) {
			getFormLine().setEntityLabel(event.getValue());
			this.fireFinishableLineEvent(this.getFormLine().isFinishable());
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