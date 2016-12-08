package com.irisa.formulis.view.create.variable;

import java.util.Date;

import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;
import com.irisa.formulis.view.create.AbstractCreateWidget;

public class DateTimeCreateWidget extends AbstractCreateWidget {

	private FluidContainer element = new FluidContainer();
	private DateTimeBox datetimeBox = new DateTimeBox();
	
	public DateTimeCreateWidget(FormElement d) {
		super(d);
		initWidget(element);
		
		element.add(datetimeBox);
	}
	
	@Override
	public FormElement getData() {
		Date selectedDatetime = datetimeBox.getValue();
		String value = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601).format(selectedDatetime);
		return new Typed(ControlUtils.LITTERAL_URIS.xsdDatetime.getUri(), value);
	}

	@Override
	public void setStartingValue(String value) {
		try {
			datetimeBox.setValue(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601).parse(value));
		}
		catch(IllegalArgumentException except) {
			ControlUtils.debugMessage("DateCreateWidget setStartingValue(" + value + ") : Cannot parse to Date");
		}
	}

}
