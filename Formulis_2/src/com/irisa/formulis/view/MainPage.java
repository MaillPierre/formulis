package com.irisa.formulis.view;

import com.google.gwt.user.client.ui.Composite;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.view.form.FormWidget;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;

public class MainPage extends Composite {
	
	private FluidRow mainPanel = new FluidRow();

	// Content
	public FormWidget formWidget;
	private Column formWidgetCol;
	public AnswersWidget ansWidget = new AnswersWidget();
	private Column ansWidgetCol;
	private Controller control;
	
	public MainPage(Controller c) {
		try {
			
			initWidget(mainPanel);
			
			control = c;
			
			// Content
			setFormWidget(new FormWidget(c.rootForm(), null));
			
			// Content
			formWidgetCol = new Column(8, formWidget);
			ansWidgetCol = new Column(4, ansWidget);
			
			ansWidget.setWidth("100%");
			
			// Contenu
			mainPanel.add(formWidgetCol);
			mainPanel.add(ansWidgetCol);
			mainPanel.addStyleName("mainPage");
		} catch(Exception e) {
			ControlUtils.exceptionMessage(e);
		}
	}
	
	public void setFormWidget(FormWidget wid) {
		formWidget = wid;
		ViewUtils.connectFormEventChain(formWidget, control);
	}

}
