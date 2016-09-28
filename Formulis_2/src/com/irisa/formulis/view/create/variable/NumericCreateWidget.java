package com.irisa.formulis.view.create.variable;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.DoubleBox;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.base.ValueBoxBase;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;

public class NumericCreateWidget extends AbstractDataWidget implements ValueChangeHandler<Boolean> {

	private FluidRow element = new FluidRow();
	private Column contentCol = new Column(12);
	private FluidContainer typePanel = new FluidContainer();
	private RadioButton integerRadio = new RadioButton("numericType", "Integer");
	private RadioButton doubleRadio = new RadioButton("numericType", "Real");
	private IntegerBox integerBox = new IntegerBox();
	private DoubleBox doubleBox = new DoubleBox();
	
	private ValueBoxBase numberBox = integerBox;
	
	private ControlUtils.LITTERAL_URIS datatype = ControlUtils.LITTERAL_URIS.xsdInteger;
	
	public NumericCreateWidget(FormElement d) {
		super(d);
		initWidget(element);
		element.add(contentCol);
		
		integerRadio.addValueChangeHandler(this);
		integerBox.setPlaceholder("11");
		typePanel.add(integerRadio);
		doubleRadio.addValueChangeHandler(this);
		doubleBox.setPlaceholder("1,1");
		typePanel.add(doubleRadio);
		
		contentCol.add(numberBox);
		contentCol.add(typePanel);
		integerRadio.setValue(true);
	}
	
	@Override
	public Typed getData() {
		if(numberBox.getValue() != null) {
			return new Typed(datatype.getUri(), String.valueOf(numberBox.getValue()));
		} else {
			return null;
		}
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if(event.getSource() == integerRadio || event.getSource() == doubleRadio) {
			if(integerRadio.getValue()) {
				numberBox = integerBox;
			} else if(doubleRadio.getValue()) {
				numberBox = doubleBox;
			} else {
				numberBox = integerBox;
			}
		}
	}

}
