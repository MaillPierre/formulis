package com.irisa.formulis.view.form;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.control.profile.ProfileLine;
import com.irisa.formulis.model.form.FormLine;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.create.CreationTypeOracle;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.interfaces.ClickWidgetHandler;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;

public abstract class FormLineWidget extends AbstractFormElementWidget 
	implements ClickWidgetHandler {
	
	protected FluidRow element = new FluidRow();
	protected FluidRow contentRow = new FluidRow();
	protected Column contentCol = new Column(8, contentRow);

	protected FluidContainer buttonsContainer = new FluidContainer();
	protected HorizontalPanel buttonsRow = new HorizontalPanel(); // TEST
	protected Column buttonsCol = new Column(4, buttonsContainer);
	protected Button resetElementButton = new Button("", IconType.REMOVE_CIRCLE);
	protected Button repeatLineButton = new Button("", IconType.PLUS);
	protected Button removeLineButton = new Button("", IconType.REMOVE);
	
	protected CheckBox profileCheckbox = new CheckBox();
	protected HorizontalPanel profileRow = new HorizontalPanel();
	protected Button profileIndexPlus = new Button("+");
	protected Button profileIndexMinus = new Button("-");
	protected IntegerBox profileIndexBox = new IntegerBox();
	
	protected LINE_STATE currentState = LINE_STATE.SUGGESTIONS;
	
//	element
//		contentCol
//			contentRow
//				LINE STUFF
//		buttonsCol
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
					
	
	public enum LINE_STATE {
		/**
		 * Default state
		 */
		SUGGESTIONS,
		GUIDED_CREATION,
		CREATION,
		FINISHED
	}
	
	public FormLineWidget( FormLine l, FormWidget par) {
		super(l, par);
		
		initWidget(element);
		
		resetElementButton.setTitle("Reset");
		resetElementButton.addClickHandler(this);
		resetElementButton.setBlock(true);
		repeatLineButton.setTitle("Repeat");
		repeatLineButton.addClickHandler(this);
		repeatLineButton.setBlock(true);
		removeLineButton.setTitle("Delete");
		removeLineButton.addClickHandler(this);
		removeLineButton.setBlock(true);
				
		buttonsContainer.add(buttonsRow);
		buttonsRow.setWidth("100%"); // TEST
		buttonsRow.add(resetElementButton);
		buttonsRow.add(repeatLineButton);
		buttonsRow.add(removeLineButton);
		
		resetElementButton.setSize(ButtonSize.SMALL);
		repeatLineButton.setSize(ButtonSize.SMALL);
		removeLineButton.setSize(ButtonSize.SMALL);
		
		profileRow.add(profileIndexPlus);
		profileRow.add(profileIndexMinus);
		profileRow.add(profileIndexBox);
		profileRow.add(profileCheckbox);
		buttonsRow.add(profileRow);
		
		buttonsContainer.addStyleName("no-gutter");
		contentCol.addStyleName("no-gutter");
		buttonsCol.addStyleName("no-gutter");
		
		profileIndexPlus.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				getData().incrementWeight();
				profileIndexBox.setValue(getData().getWeight());
				
				getParentWidget().reload();
			}
		});
		profileIndexMinus.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				getData().decrementWeight();
				profileIndexBox.setValue(getData().getWeight());
				
				getParentWidget().reload();
			}
		});
		profileIndexBox.setReadOnly(true);
		profileIndexBox.setMaxLength(2);
		profileIndexBox.setWidth("100%");
		profileIndexBox.setValue(getData().getWeight());
		profileCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setSelectedForProfile(event.getValue());
			}
		});
		profileCheckbox.setText("Add to profile");
		setProfileMode(false);
		
		element.add(contentCol);
		element.add(buttonsCol);
	}
	
	public FormLine getFormLine() {
		return getData();
	}
	
	@Override
	public FormLine getData() {
		return (FormLine)super.getData();
	}

	public void setFormLine(FormLine line) {
		this.data = line;
	}
	
	public abstract void setVariableElement(AbstractFormulisWidget nWid);
	
	public abstract AbstractFormulisWidget getFixedElement();

	public abstract AbstractFormulisWidget getVariableElement();

	@Override
	public abstract void onClickWidgetEvent(ClickWidgetEvent event);

	@Override
	public abstract void onClick(ClickEvent event);
	
	public abstract void setLineState(LINE_STATE state);
	public abstract void setLineState(LINE_STATE state, CreationTypeOracle oracl);
	
	@Override
	public FormWidget getParentWidget() {
		return (FormWidget) super.getParentWidget();
	}

	@Override
	public ProfileElement toProfileElement() {
		if(this.isSelectedForProfile()) {
			return toProfileLine();
		}
		return null;
	}
	
	@Override
	public boolean isSelectedForProfile() {
		return this.profileCheckbox.getValue();
	}
	
	@Override
	public void setSelectedForProfile(boolean val) {
		this.profileCheckbox.setValue(val);
		if(val) {
			this.getParentWidget().setSelectedForProfile(val);
		}
	}
	
	@Override
	public void setProfileMode(boolean value) {
		super.setProfileMode(value);
		
		this.profileRow.setVisible(value);
		if(! value) {
			this.buttonsRow.remove(profileRow);
			this.buttonsCol.setSize(4);
			this.contentCol.setSize(8);
		} else {
			this.buttonsRow.add(profileRow);
			this.buttonsCol.setSize(5);
			this.contentCol.setSize(7);
		}
		
//		this.profileRow.setVisible(value);
	}
	
	protected abstract ProfileLine toProfileLine();

	@Override
	public String toString() {
		String result = "line widget " + getData().toString();
		
		return result;
	}
	
}
