package com.irisa.formulis.view;

import com.google.gwt.user.client.ui.Composite;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.view.form.FormWidget;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Footer;

public class MainPage extends Composite {
	
	private FluidRow element = new FluidRow();
	private Column contentCol = new Column(12);
	private FluidRow formulisRow = new FluidRow(); 

	// Content
	public FormWidget formWidget;
	private Column formWidgetCol;
	public AnswersWidget ansWidget = new AnswersWidget();
	private Column ansWidgetCol;
	private Controller control;
	private AdminPanel settings = new AdminPanel();
	
	public MainPage(Controller c) {
		try {
			
			initWidget(element);
			
			control = c;
			
			// Content
			setFormWidget(new FormWidget(c.rootForm(), null));
			
			// Content
			formWidgetCol = new Column(8, formWidget);
			ansWidgetCol = new Column(4, ansWidget);
			
			ansWidget.setWidth("100%");
			
			// Contenu
			formulisRow.add(formWidgetCol);
			formulisRow.add(ansWidgetCol);
			contentCol.add(formulisRow);
			contentCol.add(settings);
			element.add(contentCol);
			element.addStyleName("mainPage");
		} catch(Exception e) {
			ControlUtils.exceptionMessage(e);
		}
	}
	
	public void setFormWidget(FormWidget wid) {
		formWidget = wid;
		ViewUtils.connectFormEventChain(formWidget, control);
	}

	public AdminPanel getSettingsWidget() {
		return settings;
	}

	public void setSettingsWidget(AdminPanel settings) {
		this.settings = settings;
	}

}
