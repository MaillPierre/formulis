package com.irisa.formulis.view.create.variable;

import java.util.Date;

import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.user.client.ui.Grid;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;
import com.irisa.formulis.view.create.AbstractCreateWidget;

public class TimeCreateWidget extends AbstractCreateWidget {

	private DateTimeBox timeBox = new DateTimeBox();
	private Grid element = new Grid(1,1);
	
	public TimeCreateWidget(FormElement d) {
		super(d);
		initWidget(element);
		
		timeBox.setMinView("HOUR");
		timeBox.setMaxView("HOUR");
		timeBox.setStartView("HOUR");
		timeBox.setMinuteStep(5);
		timeBox.setFormat("hh:ii");
		
		element.setWidget(0, 0, timeBox);
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public FormElement getData() {
		int hours = timeBox.getValue().getHours();
		int minutes = timeBox.getValue().getMinutes();
		int seconds = timeBox.getValue().getSeconds();
		return new Typed(ControlUtils.LITTERAL_URIS.xsdTime.getUri(), String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setStartingValue(String value) {
		try {
			timeBox.setValue(new Date(Date.parse(value)));
		}
		catch(IllegalArgumentException e) {
			ControlUtils.debugMessage("TimeCreateWidget setStartingValue : Couldn't convert to time");
		}
	}

}
