package com.irisa.formulis.view.create.variable;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.create.AbstractCreateWidget;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.datepicker.client.ui.DateBox;

public class DateCreateWidget extends AbstractCreateWidget implements ChangeHandler{
	
	private FluidRow elementRow = new FluidRow();
	private Column elementCol = new Column(12);
	private DateBox dateBox = new DateBox();
	
	private ListBox formatBox = new ListBox(false);
	
	private String dateFormat = "yyyy-MM-dd";
	private ControlUtils.LITTERAL_URIS datatype = ControlUtils.LITTERAL_URIS.xsdDate;
	
	public DateCreateWidget(FormElement d) {
		super(d);
		initWidget(elementRow);
		elementRow.add(elementCol);

		dateBox.setStartView("YEAR");
		formatBox.addItem("Year-Month-Day", ControlUtils.LITTERAL_URIS.xsdDate.getUri());
		formatBox.addItem("Month-Day", ControlUtils.LITTERAL_URIS.xsdMonthDay.getUri());
		formatBox.addItem("Year-Month", ControlUtils.LITTERAL_URIS.xsdYearMonth.getUri());
		formatBox.addItem("Year", ControlUtils.LITTERAL_URIS.xsdYear.getUri());
		formatBox.addItem("Month", ControlUtils.LITTERAL_URIS.xsdMonth.getUri());
		formatBox.addItem("Day", ControlUtils.LITTERAL_URIS.xsdDay.getUri());
		formatBox.addChangeHandler(this);
		formatBox.setSelectedIndex(0);
		
		elementCol.add(dateBox);
		elementCol.add(formatBox);

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
	public void onChange(ChangeEvent event) {
		if(event.getSource() == formatBox ) {
			String format = null;
			String select = formatBox.getSelectedValue();
			
			if(select.equals(ControlUtils.LITTERAL_URIS.xsdYear.getUri())) {
				format = "yyyy";
				datatype = ControlUtils.LITTERAL_URIS.xsdYear;
			} 
			else if(select.equals(ControlUtils.LITTERAL_URIS.xsdMonth.getUri())) {
				format = "MM";
				datatype = ControlUtils.LITTERAL_URIS.xsdMonth;
			} 
			else if(select.equals(ControlUtils.LITTERAL_URIS.xsdDay.getUri())) {
				format = "dd";
				datatype = ControlUtils.LITTERAL_URIS.xsdDay;
			} 
			else if(select.equals(ControlUtils.LITTERAL_URIS.xsdYearMonth.getUri())) {
				format = "yyyy-MM";
				datatype = ControlUtils.LITTERAL_URIS.xsdYearMonth;
			} 
			else if(select.equals(ControlUtils.LITTERAL_URIS.xsdMonthDay.getUri())) {
				format = "MM-dd";
				datatype = ControlUtils.LITTERAL_URIS.xsdMonthDay;
			} 
			else /*if(select.equals(ControlUtils.LITTERAL_URIS.xsdDate.getUri()))*/ {
				format = "yyyy-MM-dd";
				datatype = ControlUtils.LITTERAL_URIS.xsdDate;
			}
			
			dateFormat = format;
			dateBox.setFormat(format);
		}
	}

	@Override
	public void setStartingValue(String value) {
		try {
			dateBox.setValue(DateTimeFormat.getFormat(dateFormat).parse(value));
		}
		catch(IllegalArgumentException except) {
			ControlUtils.debugMessage("DateCreateWidget setStartingValue(" + value + ") : Cannot parse to Date");
		}
	}
	

}
