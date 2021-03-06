package com.irisa.formulis.view.form;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Column;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.profile.ProfileLine;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.FormulisWidgetFactory;
import com.irisa.formulis.view.basic.URIWidget;
import com.irisa.formulis.view.create.CreationTypeOracle;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.DescribeUriEvent;
import com.irisa.formulis.view.event.ModificationModeEvent;
import com.irisa.formulis.view.event.SuggestionSelectionEvent;
import com.irisa.formulis.view.event.callback.AbstractFormCallback;
import com.irisa.formulis.view.event.interfaces.CompletionAskedHandler;
import com.irisa.formulis.view.event.interfaces.HasModificationModeHandler;
import com.irisa.formulis.view.event.interfaces.ModificationModeHandler;
import com.irisa.formulis.view.event.interfaces.SuggestionSelectionHandler;
import com.irisa.formulis.view.form.FormWidget.LAST_ACTION;
import com.irisa.formulis.view.form.suggest.EntitySuggestionWidget;

/**
 * Type line with an uri and a field with suggestions of existing elements.
 * Entering a name in the field sends events to the parent form and is the minimum condition to send it to the server.
 * Selecting a suggestion makes the parent form load the description of the resource and enter modification mode.
 * Reseting the line while on description mode makes the form quit modification mode (TBD)
 * @author pmaillot
 *
 */
public class FormClassLineWidget 
	extends AbstractFormLineWidget 
	implements ValueChangeHandler<String>, ClickHandler, CompletionAskedHandler, SuggestionSelectionHandler, HasModificationModeHandler {
	
	private LinkedList<ModificationModeHandler> _modificationModeHandlers = new LinkedList<ModificationModeHandler>();

	protected HorizontalPanel elementRow = new HorizontalPanel();
	protected Column elementCol = new Column(12, elementRow);
//	private TextBox labelUriBox = new TextBox();
	private EntitySuggestionWidget labelUriBox = null;
	private URIWidget labelWid = null;

	protected AbstractFormulisWidget fixedElement = null;

	public FormClassLineWidget(FormClassLine l, FormWidget par, String startValue) {
		super(l, par);

//		ControlUtils.debugMessage("FormClassLineWidget " + l + "  " + l.getVariableElement() + "  " + l.getEntityLabel());
		if(! l.isAnonymous()) {
			try {
				this.fixedElement = FormulisWidgetFactory.getWidget((URI)l.getFixedElement(), this, this);
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			} catch(ClassCastException e) {
//				ControlUtils.debugMessage("ClassLineWidget fixed:" + l.getFixedElement() + " isAno:" + l.isAnonymous());
				throw e;
			}
		}
		
		labelUriBox = new EntitySuggestionWidget(l, this);
		
		labelUriBox.setWidth("100%");
		labelUriBox.setText(startValue);
		labelUriBox.addStyleName("input-block-level");
		labelUriBox.setPlaceholder("Name of this new element");
		labelUriBox.addValueChangeHandler(this);
		labelUriBox.addSuggestionSelectionHandler(this);
		labelUriBox.addCompletionAskedHandler(this);
		labelUriBox.setSuggestionOnly(true);
//		labelUriBox.addKeyUpHandler(new KeyUpHandler() {
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				ValueChangeEvent.fire(labelUriBox, labelUriBox.getValue());	
//				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
////					finish();
//				}
//			}
//		});
		
		if( l .getEntityUri() != null) {
			try {
				setVariableElement(FormulisWidgetFactory.getWidget(l.getEntityUri(), this.getParentWidget()));
			} catch (FormElementConversionException e) {
				ControlUtils.exceptionMessage(e);
			}
		} else if(! l.getEntityLabel().isEmpty()) {
			labelUriBox.setText(l.getEntityLabel());
			ValueChangeEvent.fire(labelUriBox, labelUriBox.getValue());
		} else if(! l.getTempValue().isEmpty()) {
			labelUriBox.setText(l.getTempValue());
			ValueChangeEvent.fire(labelUriBox, labelUriBox.getValue());
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
//		ControlUtils.debugMessage("FormClassWidget reload " + this.getParentWidget().getData().getMainType() + " " +  this.getData());
		elementRow.clear();
		elementRow.add(fixedElement);
		
		if(this.getParentWidget().getData().isTypeList() ) {
			hideLabelBox();
			this.resetElementButton.setEnabled(false);
		} 
		if(this.getParentWidget().getData().isTyped() 
				&& this.getParentWidget().getData().getMainType().equals(this.getData())) {
			if(this.getData().isFinishable() && this.getData().isFinished()) {
				setLineState(LINE_STATE.FINISHED);
			} else {
				setLineState(LINE_STATE.SUGGESTIONS);
			}
		} else {
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
			if(labelWid != null) {
				elementRow.remove(labelWid); // There can be only one label
			}
			if(this.getFormLine().getEntityLabel().equals("")) {
				labelWid = new URIWidget(this.getFormLine().getEntityUri(), this);
			}
//			ControlUtils.debugMessage("FormClassLineWidget hideLabelBox" + labelWid);
			elementRow.remove(labelUriBox);
			if(labelWid != null) {
				elementRow.add(labelWid);
				elementRow.setCellWidth(labelWid, "100%");
			}
		}
//		ControlUtils.debugMessage("FormClassLineWidget hideLabelBox END");
	}
	
	public void showLabelBox() {
//		ControlUtils.debugMessage("FormClassLineWidget showLabelBox");
		labelUriBox.setVisible(true);
		if(this.getData() != null) {
			if(labelWid != null) {
				elementRow.remove(labelWid);
			}
			elementRow.add(labelUriBox);
			elementRow.setCellWidth(labelUriBox, "100%");
		}
//		ControlUtils.debugMessage("FormClassLineWidget showLabelBox END");
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
		} else if(state == LINE_STATE.FIXED) {
			
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
			this.fireModificationModeChange(false);
		} else if(event.getSource() == this.removeLineButton) {
//			ControlUtils.debugMessage("FormClassLine onClick remove");
			fireRemoveLineEvent();
		} 
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
//		ControlUtils.debugMessage("FormClassLineWidget onValueChange " + event.getValue() + " " + (event.getSource() == this.labelUriBox));
		if(event.getSource() == this.labelUriBox) {
			getFormLine().setEntityLabel(event.getValue());
			if(event.getValue() != this.getFormLine().getEntityLabel()) {
				getParentWidget().setLastAction(LAST_ACTION.EDIT);
			}
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

	@Override
	/**
	 * Suggestion selection should trigger the load and edition of an existing value
	 */
	public void onSuggestionSelection(SuggestionSelectionEvent event) {
//		ControlUtils.debugMessage("Entity Selected " + event.getSuggestion() + " " + event.getSuggestion().getElement().getClass().getSimpleName());
		if(event.getSuggestion().getElement() instanceof URI) {
			DescribeUriEvent descEvent = null;
			AbstractFormCallback cb = getUriDescriptionCallback();
			URI u = (URI)event.getSuggestion().getElement();
			descEvent = new DescribeUriEvent(this, cb, u);
			this.fireDescribeUriEvent(descEvent);
		}
	}
	
	public AbstractFormCallback getUriDescriptionCallback() {
		return new AbstractFormCallback() {			
			@Override
			public void call(Form desc) {
				getParentWidget().setData(desc);
				getParentWidget().setLastAction(LAST_ACTION.LOAD_EXISTING);
				fireModificationModeChange(true);
			}
		};
	}

	@Override
	public void addModificationModeHandler(ModificationModeHandler handler) {
		this._modificationModeHandlers.add(handler);
	}

	@Override
	public void fireModificationModeChange(boolean modif) {
		ModificationModeEvent event = new ModificationModeEvent(this, modif);
		Iterator<ModificationModeHandler> itHandler = this._modificationModeHandlers.iterator();
		while(itHandler.hasNext()) {
			ModificationModeHandler handler = itHandler.next();
			handler.onModificationModeChange(event);
		}
	}
	
}
