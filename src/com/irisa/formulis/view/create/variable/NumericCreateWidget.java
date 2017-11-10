package com.irisa.formulis.view.create.variable;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.DoubleBox;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.base.ValueBoxBase;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.create.AbstractCreateWidget;

public class NumericCreateWidget extends AbstractCreateWidget implements ValueChangeHandler<Boolean> {

	private FluidRow element = new FluidRow();
//	private VerticalPanel typePanel = new VerticalPanel();
	private RadioButton integerRadio = new RadioButton("numericType", "Integer");
	private RadioButton doubleRadio = new RadioButton("numericType", "Real");
	private IntegerBox integerBox = new IntegerBox();
	private DoubleBox doubleBox = new DoubleBox();
	
	private ValueBoxBase<? extends Number> numberBox = integerBox;
	private Column contentCol = new Column(6, numberBox);
	private Column modeCol = new Column(6, integerRadio, doubleRadio);
	
	private ControlUtils.LITTERAL_URIS datatype = ControlUtils.LITTERAL_URIS.xsdInteger;
	
	public NumericCreateWidget(FormElement d) {
		super(d);
		initWidget(element);
		element.add(contentCol);
		element.add(modeCol);
		element.setWidth("100%");
		
		integerRadio.addValueChangeHandler(this);
		integerBox.setPlaceholder("11");
		integerBox.addStyleName("input-block-level");
//		typePanel.add(integerRadio);
		doubleRadio.addValueChangeHandler(this);
		doubleBox.setPlaceholder("1,1");
		doubleBox.addStyleName("input-block-level");
//		typePanel.add(doubleRadio);
		
		contentCol.add(numberBox);
//		contentCol.add(typePanel);
		integerRadio.setValue(true);
	}
	
	@Override
	public Typed getData() {
		if(numberBox.getValue() != null) {
			return new Typed(datatype.getUri(), String.valueOf(numberBox.getValue()));
		}
		return null;
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if(event.getSource() == integerRadio || event.getSource() == doubleRadio) {
			Number numValue = numberBox.getValue();
			if(integerRadio.getValue()) {
				setIntegerMode(numValue);
			} else if(doubleRadio.getValue()) {
				setDoubleMode(numValue);
			} else {
				setIntegerMode(numValue);
			}
		}
	}
	
	protected void setIntegerMode(String textValue) {
		try {
			setIntegerMode(Integer.parseInt(textValue));
		}
		catch(IllegalArgumentException e) {
			ControlUtils.debugMessage("NumericCreateWidget setIntegerMode : Couldn't convert " + textValue + " to integer");
			setIntegerMode(0);
		}
	}
	
	protected void setIntegerMode(Number numValue) {
		contentCol.remove(numberBox);
		integerBox.setValue(numValue.intValue());
		numberBox = integerBox;
		contentCol.add(numberBox);
		datatype = ControlUtils.LITTERAL_URIS.xsdInteger;
	}
	
	protected void setDoubleMode(String textValue) {
		try {
			setDoubleMode(Double.parseDouble(textValue));
		}
		catch(IllegalArgumentException e) {
			ControlUtils.debugMessage("NumericCreateWidget setDoubleMode : Couldn't convert " + textValue + " to double");
			setDoubleMode(0);
		}
	}
	
	protected void setDoubleMode(Number numValue) {
		contentCol.remove(numberBox);
		doubleBox.setValue(numValue.doubleValue());
		numberBox = doubleBox;
		contentCol.add(numberBox);
		datatype = ControlUtils.LITTERAL_URIS.xsdDouble;
	}

	@Override
	public void setStartingValue(String value) {
		if(integerRadio.getValue()) {
			setIntegerMode(value);
		} else if(doubleRadio.getValue()) {
			setDoubleMode(value);
		}
	}

}
