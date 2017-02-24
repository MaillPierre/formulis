package com.irisa.formulis.view.create;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;

import java.util.HashMap;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.create.fixed.FormCreateWidget;
import com.irisa.formulis.view.create.variable.DateCreateWidget;
import com.irisa.formulis.view.create.variable.DateTimeCreateWidget;
import com.irisa.formulis.view.create.variable.EntityCreateWidget;
import com.irisa.formulis.view.create.variable.NumericCreateWidget;
import com.irisa.formulis.view.create.variable.TextCreateWidget;
import com.irisa.formulis.view.create.variable.TimeCreateWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.interfaces.ClickWidgetHandler;
import com.github.gwtbootstrap.client.ui.ListBox;

/**
 * Encapsulation widget for all line-based creation widgets, give access to the list of type 
 * and return to the line automatically the data contained inside the selected creation widget
 * @author pmaillot
 *
 */
public class SelectCreateWidget extends AbstractFormulisWidget implements ChangeHandler {
	
	private Grid element = new Grid(3, 1);
	private ListBox typeList = new ListBox();
	private HashMap<String, Integer> typeIndexMap = new HashMap<String, Integer>();
	private AbstractCreateWidget createWid = null;
	private Button createButton = new Button("Create");
	private int selectTypeIndex = 0;
	private int createWidgetIndex = 1;
	private int createButtonIndex = 2;
	
	private CreationTypeOracle oracle = null;
	
	public SelectCreateWidget( AbstractFormulisWidget fParent) {
		this(fParent, null);
	}
	
	public SelectCreateWidget(AbstractFormulisWidget fParent, CreationTypeOracle oracl) {
		super(null, fParent);
		initWidget(element);
		
		oracle = oracl;
		
		element.setWidget(selectTypeIndex, 0, typeList);
		element.setWidget(createButtonIndex, 0, createButton);
		
		typeList.addItem("Date", "date");
		typeIndexMap.put("date", 0);
		typeList.addItem("Hour", "time");
		typeIndexMap.put("time", 1);
		typeList.addItem("Date and hour", "datetime");
		typeIndexMap.put("datetime", 2);
		typeList.addItem("Numeric value", "number");
		typeIndexMap.put("number", 3);
		typeList.addItem("Text", "text");
		typeIndexMap.put("text", 4);
		typeList.addItem("Entity", "entity");
		typeIndexMap.put("entity", 5);
		typeList.addItem("Form", "form");
		typeIndexMap.put("form", 6);
		
		this.createButton.addClickHandler(this);
		typeList.addChangeHandler(this);
		if(oracl != null) {
			String oracleAttempt = oracl.getMostLikelyLiteralType();
			String simpleOracleAttempt = oracl.getSimpleMostLikelyLiteralType();
			if(oracleAttempt != null) {
				typeList.setItemSelected(typeIndexMap.get(simpleOracleAttempt), true); // Seul les versions simple sont dans le menu (sans le détail des dates)
				this.setCreationType(oracleAttempt);
			} else {
				typeList.setItemSelected(4, true);
				this.setCreationType("text");
			}
		}
		this.element.setWidget(createWidgetIndex, 0, createWid);
	}

	@Override
	public void addClickWidgetEventHandler(ClickWidgetHandler handler) {
		super.addClickWidgetEventHandler(handler);
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == this.createButton && this.createWid != null && this.createWid.getData() != null) {
			fireClickWidgetEvent(new ClickWidgetEvent(this));
		}
	}
	
	@Override
	public FormElement getData() {
		return this.createWid.getData();
	}

	@Override
	public void onChange(ChangeEvent event) {
		if(event.getSource() == this.typeList) {
			int selectIndex = typeList.getSelectedIndex();
			setCreationType(this.typeList.getValue(selectIndex));
			this.element.setWidget(createWidgetIndex, 0, createWid);
		}
	}
	
	public void setCreationType(String type) {
//		ControlUtils.debugMessage("SelectCreateWidget setCreationType(" + type + ")");
		if(type.equals("date")) {
//			ControlUtils.debugMessage("SelectCreateWidget setCreationType(" + type + ") date");
			this.createWid = new DateCreateWidget(null);
		} else if(type.equals("date-year")) {
//			ControlUtils.debugMessage("SelectCreateWidget setCreationType(" + type + ") date-year");
			this.createWid = new DateCreateWidget(null);
			((DateCreateWidget)this.createWid).setFormatDatatype(ControlUtils.LITTERAL_URIS.xsdYear.getUri());
		} else if(type.equals("date-month")) {
//			ControlUtils.debugMessage("SelectCreateWidget setCreationType(" + type + ") date-month");
			this.createWid = new DateCreateWidget(null);
			((DateCreateWidget)this.createWid).setFormatDatatype(ControlUtils.LITTERAL_URIS.xsdMonth.getUri());
		} else if(type.equals("date-day")) {
//			ControlUtils.debugMessage("SelectCreateWidget setCreationType(" + type + ") date-day");
			this.createWid = new DateCreateWidget(null);
			((DateCreateWidget)this.createWid).setFormatDatatype(ControlUtils.LITTERAL_URIS.xsdDay.getUri());
		} else if(type.equals("date-monthday")) {
//			ControlUtils.debugMessage("SelectCreateWidget setCreationType(" + type + ") date-monthday");
			this.createWid = new DateCreateWidget(null);
			((DateCreateWidget)this.createWid).setFormatDatatype(ControlUtils.LITTERAL_URIS.xsdMonthDay.getUri());
		} else if(type.equals("date-yearmonth")) {
//			ControlUtils.debugMessage("SelectCreateWidget setCreationType(" + type + ") date-yearmonth");
			this.createWid = new DateCreateWidget(null);
			((DateCreateWidget)this.createWid).setFormatDatatype(ControlUtils.LITTERAL_URIS.xsdYearMonth.getUri());
		} else if(type.equals("time")) {
			this.createWid = new TimeCreateWidget(null);
		} else if(type.equals("datetime")) {
			this.createWid = new DateTimeCreateWidget(null);
		} else if(type.equals("number")) {
			this.createWid = new NumericCreateWidget(null);
		} else if(type.equals("text")) {
			this.createWid = new TextCreateWidget(null);
		} else if(type.equals("entity")) {
			this.createWid = new EntityCreateWidget(null);
		} else if(type.equals("form")) {
			this.createWid = new FormCreateWidget(null);
		}
		if(this.oracle != null) {
			this.createWid.setStartingValue(this.oracle.getStartValue());
		}
//		ControlUtils.debugMessage("SelectCreateWidget setCreationType(" + type + ") FIN");
	}

}
