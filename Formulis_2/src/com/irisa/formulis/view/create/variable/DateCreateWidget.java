package com.irisa.formulis.view.create.variable;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.datepicker.client.ui.DateBox;

public class DateCreateWidget extends AbstractDataWidget implements ValueChangeHandler<Boolean>{
	
//	private Grid element = new Grid(2,1);
	private FluidRow elementRow = new FluidRow();
	private Column elementCol = new Column(12);
	private DateBox dateBox = new DateBox();
	
	private FluidContainer checkRow = new FluidContainer(); 
	private CheckBox yearCheckBox = new CheckBox("Year");
	private CheckBox monthCheckBox = new CheckBox("Month");
	private CheckBox dayCheckBox = new CheckBox("Day");
	private CheckBox yearMonthCheckBox = new CheckBox("Year-Month");
	private CheckBox monthDayCheckBox = new CheckBox("Month-Day");
	
	private String dateFormat = "yyyy-MM-dd";
	private ControlUtils.LITTERAL_URIS datatype = ControlUtils.LITTERAL_URIS.xsdDate;
	
	public DateCreateWidget(FormElement d) {
		super(d);
		initWidget(elementRow);
		elementRow.add(elementCol);

		dateBox.setStartView("YEAR");
		checkRow.add(yearCheckBox);
		checkRow.add(monthCheckBox);
		checkRow.add(dayCheckBox);
		checkRow.add(yearMonthCheckBox);
		checkRow.add(monthDayCheckBox);
		
		elementCol.add(dateBox);
		elementCol.add(checkRow);
		yearCheckBox.addValueChangeHandler(this);
		monthCheckBox.addValueChangeHandler(this);
		dayCheckBox.addValueChangeHandler(this);
		yearCheckBox.setValue(true);
		monthCheckBox.setValue(true);
		dayCheckBox.setValue(true);
	}

	@Override
	public FormElement getData() {
		if(dateBox.getValue() != null) {
			String value =  DateTimeFormat.getFormat(dateFormat).format(dateBox.getValue());
			return new Typed(datatype.getUri(), value);
		}
		else {
			return null;
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if(event.getSource() == yearCheckBox || event.getSource() == monthCheckBox || event.getSource() == dayCheckBox ) {
			String format = "";
			// Format
			if(yearCheckBox.getValue()) {
				format += "yyyy";
			}
			if(yearCheckBox.getValue() && monthCheckBox.getValue()) {
				format += "-";
			}
			if(monthCheckBox.getValue()) {
				format += "MM";
			}
			if(monthCheckBox.getValue() && dayCheckBox.getValue() ) {
				format += "-";
			}
			if(dayCheckBox.getValue()) {
				format += "dd";
			}
			
			// Datatype
			if(yearCheckBox.getValue()) {
				datatype = ControlUtils.LITTERAL_URIS.xsdYear;
			}
			if(monthCheckBox.getValue()) {
				datatype = ControlUtils.LITTERAL_URIS.xsdMonth;
			}
			if(dayCheckBox.getValue()) {
				datatype = ControlUtils.LITTERAL_URIS.xsdDay;
			}
			if(yearCheckBox.getValue() && monthCheckBox.getValue()) {
				datatype = ControlUtils.LITTERAL_URIS.xsdYearMonth;
			}
			if(monthCheckBox.getValue() && dayCheckBox.getValue() ) {
				datatype = ControlUtils.LITTERAL_URIS.xsdMonthDay;
			}
			if(yearCheckBox.getValue() && monthCheckBox.getValue() && dayCheckBox.getValue()) {
				datatype = ControlUtils.LITTERAL_URIS.xsdDate;
			}
			
			dateBox.setFormat(format);
			dateFormat = format;
		} else if(event.getSource() == yearMonthCheckBox) {
			if(yearMonthCheckBox.getValue()) {
				String format = "yyyy-MM";
				datatype = ControlUtils.LITTERAL_URIS.xsdYearMonth;
				dateBox.setFormat(format);
				dateFormat = format;
			}
		} else if(event.getSource() == monthDayCheckBox) {
			if(monthDayCheckBox.getValue()) {
				String format = "MM-dd";
				datatype = ControlUtils.LITTERAL_URIS.xsdMonthDay;
				dateBox.setFormat(format);
				dateFormat = format;
			}
		} 
		if( event.getSource() == yearCheckBox || event.getSource() == monthCheckBox || event.getSource() == dayCheckBox || event.getSource() == yearMonthCheckBox || event.getSource() == monthDayCheckBox){
			if( ! (yearCheckBox.getValue() || monthCheckBox.getValue() || dayCheckBox.getValue() || yearMonthCheckBox.getValue() || monthDayCheckBox.getValue())) {
				String format = "yyyy-MM-dd";
				datatype = ControlUtils.LITTERAL_URIS.xsdDate;
				dateBox.setFormat(format);
				dateFormat = format;
			}
		}
//		ControlUtils.debugMessage("DateCreateWidget onValueChange " + dateFormat + " " +datatype.getUri() );
	}
	

}
