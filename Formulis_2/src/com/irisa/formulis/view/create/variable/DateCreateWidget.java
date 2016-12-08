package com.irisa.formulis.view.create.variable;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.create.AbstractCreateWidget;

import java.util.HashMap;

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
	private HashMap<String, Integer> typeIndexMap = new HashMap<String, Integer>();
	private ControlUtils.LITTERAL_URIS datatype = ControlUtils.LITTERAL_URIS.xsdDate;
	
	public DateCreateWidget(FormElement d) {
		super(d);
		initWidget(elementRow);
		elementRow.add(elementCol);

		dateBox.setStartView("YEAR");
		formatBox.addItem("Year-Month-Day", ControlUtils.LITTERAL_URIS.xsdDate.getUri());
		typeIndexMap.put(ControlUtils.LITTERAL_URIS.xsdDate.getUri(), 0);
		formatBox.addItem("Month-Day", ControlUtils.LITTERAL_URIS.xsdMonthDay.getUri());
		typeIndexMap.put(ControlUtils.LITTERAL_URIS.xsdMonthDay.getUri(), 1);
		formatBox.addItem("Year-Month", ControlUtils.LITTERAL_URIS.xsdYearMonth.getUri());
		typeIndexMap.put(ControlUtils.LITTERAL_URIS.xsdYearMonth.getUri(), 2);
		formatBox.addItem("Year", ControlUtils.LITTERAL_URIS.xsdYear.getUri());
		typeIndexMap.put(ControlUtils.LITTERAL_URIS.xsdYear.getUri(), 3);
		formatBox.addItem("Month", ControlUtils.LITTERAL_URIS.xsdMonth.getUri());
		typeIndexMap.put(ControlUtils.LITTERAL_URIS.xsdMonth.getUri(), 4);
		formatBox.addItem("Day", ControlUtils.LITTERAL_URIS.xsdDay.getUri());
		typeIndexMap.put(ControlUtils.LITTERAL_URIS.xsdDay.getUri(), 5);
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
			String select = formatBox.getSelectedValue();
			
			this.setFormatDatatype(select);
		}
	}
	
	public void setFormatDatatype(String type) {
		ControlUtils.debugMessage("DateCreateWidget setFormatDatatype " + type );
		String format = null;
		
		if(type.equals(ControlUtils.LITTERAL_URIS.xsdYear.getUri())) {
			format = "yyyy";
			datatype = ControlUtils.LITTERAL_URIS.xsdYear;
		} 
		else if(type.equals(ControlUtils.LITTERAL_URIS.xsdMonth.getUri())) {
			format = "MM";
			datatype = ControlUtils.LITTERAL_URIS.xsdMonth;
		} 
		else if(type.equals(ControlUtils.LITTERAL_URIS.xsdDay.getUri())) {
			format = "dd";
			datatype = ControlUtils.LITTERAL_URIS.xsdDay;
		} 
		else if(type.equals(ControlUtils.LITTERAL_URIS.xsdYearMonth.getUri())) {
			format = "yyyy-MM";
			datatype = ControlUtils.LITTERAL_URIS.xsdYearMonth;
		} 
		else if(type.equals(ControlUtils.LITTERAL_URIS.xsdMonthDay.getUri())) {
			format = "MM-dd";
			datatype = ControlUtils.LITTERAL_URIS.xsdMonthDay;
		} 
		else {
			format = "yyyy-MM-dd";
			datatype = ControlUtils.LITTERAL_URIS.xsdDate;
		}
		
		dateFormat = format;
		dateBox.setFormat(format);
		formatBox.setSelectedIndex(this.typeIndexMap.get(type));
		ControlUtils.debugMessage("DateCreateWidget setFormatDatatype " + type + " END");
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
