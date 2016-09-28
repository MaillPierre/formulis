package com.irisa.formulis.view.create;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;

import java.util.HashMap;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.create.fixed.FormCreateWidget;
import com.irisa.formulis.view.create.variable.DateCreateWidget;
import com.irisa.formulis.view.create.variable.DateTimeCreateWidget;
import com.irisa.formulis.view.create.variable.EntityCreateWidget;
import com.irisa.formulis.view.create.variable.NumericCreateWidget;
import com.irisa.formulis.view.create.variable.TextCreateWidget;
import com.irisa.formulis.view.create.variable.TimeCreateWidget;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.github.gwtbootstrap.client.ui.ListBox;

public class SelectCreateWidget extends AbstractFormulisWidget implements ChangeHandler {
	
	private Grid element = new Grid(3, 1);
	private ListBox typeList = new ListBox();
	private HashMap<String, Integer> typeIndexMap = new HashMap<String, Integer>();
	private AbstractDataWidget createWid = null;
	private Button createButton = new Button("Create");
	private int selectTypeIndex = 0;
	private int createWidgetIndex = 1;
	private int createButtonIndex = 2;
	
	public SelectCreateWidget( AbstractFormulisWidget fParent) {
		this(fParent, null);
	}
	
	public SelectCreateWidget(AbstractFormulisWidget fParent, CreationTypeOracle oracl) {
		super(null, fParent);
		initWidget(element);
		
		element.setWidget(selectTypeIndex, 0, typeList);
		element.setWidget(createButtonIndex, 0, createButton);
		
		typeList.addItem("Date", "date");
		typeIndexMap.put("date", 0);
		typeList.addItem("Heure", "time");
		typeIndexMap.put("time", 1);
		typeList.addItem("Date et heure", "datetime");
		typeIndexMap.put("datetime", 2);
		typeList.addItem("Valeur numérique", "number");
		typeIndexMap.put("number", 3);
		typeList.addItem("Texte", "text");
		typeIndexMap.put("text", 4);
		typeList.addItem("Entité", "entity");
		typeIndexMap.put("entity", 5);
		typeList.addItem("Formulaire", "form");
		typeIndexMap.put("form", 6);
		
		this.createButton.addClickHandler(this);
		typeList.addChangeHandler(this);
		if(oracl != null) {
			String oracleAttempt = oracl.getMostLikelyLiteralType();
			if(oracleAttempt != null) {
				typeList.setItemSelected(typeIndexMap.get(oracleAttempt), true);
				this.setCreationType(oracleAttempt);
			} else {
				typeList.setItemSelected(4, true);
				this.setCreationType("text");
			}
		}
		this.element.setWidget(createWidgetIndex, 0, createWid);
	}

	@Override
	public void addClickWidgetEventHandler(com.irisa.formulis.view.event.interfaces.ClickWidgetHandler handler) {
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
		if(type == "date") {
			this.createWid = new DateCreateWidget(null);
		} else if(type == "time") {
			this.createWid = new TimeCreateWidget(null);
		} else if(type == "datetime") {
			this.createWid = new DateTimeCreateWidget(null);
		} else if(type == "number") {
			this.createWid = new NumericCreateWidget(null);
		} else if(type == "text") {
			this.createWid = new TextCreateWidget(null);
		} else if(type == "entity") {
			this.createWid = new EntityCreateWidget(null);
		} else if(type == "form") {
			this.createWid = new FormCreateWidget(null);
		}
	}

}
