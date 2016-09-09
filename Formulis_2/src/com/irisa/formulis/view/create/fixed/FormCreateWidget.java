package com.irisa.formulis.view.create.fixed;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.basic.URI.KIND;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;

public class FormCreateWidget extends AbstractDataWidget {

	private HorizontalPanel element = new HorizontalPanel();
	private TextBox classNameBox = new TextBox();
	private TextBox classUriBox = new TextBox();
	private CheckBox anonymousCheck = new CheckBox();
	
	public FormCreateWidget(FormElement e) {
		super(e);
		data = new Form(null);
		initWidget(element);
		
		classNameBox.setWidth("150px");
		classNameBox.setPlaceholder("Class name");
		classNameBox.addValueChangeHandler(new ValueChangeHandler<String>(){
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				classUriBox.setText(Controller.newElementUri(event.getValue()));
			}
		});
		classUriBox.setWidth("150px");
		classUriBox.setPlaceholder("(Optional) Classe URI");
		anonymousCheck.setValue(false);
		anonymousCheck.setText("Anonymous");
		anonymousCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				classNameBox.setReadOnly(event.getValue());
				classUriBox.setReadOnly(event.getValue());
				if(event.getValue()) {
					classNameBox.setText("");
					classUriBox.setText("");
				} 
			}
		});
		
		element.add(classNameBox);
		element.add(classUriBox);
		element.add(anonymousCheck);
	}
	
	@Override
	public Form getData() {
		Form result = (Form) data;
		if(classNameBox.getText() != "" && ! anonymousCheck.getValue()) {
			// Creation d'un formulaire typé
			URI classUri = new URI(classUriBox.getText(), KIND.CLASS, classNameBox.getText());
			FormClassLine classLine = new FormClassLine(result, classUri);
			result.setTypeLine(classLine);
		} else {
			// Creation d'un formulaire anonyme
			
		}
		
		return result;
	}
}
