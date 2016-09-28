package com.irisa.formulis.view.create.variable;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Grid;
import com.irisa.formulis.model.basic.Plain;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;

public class TextCreateWidget extends AbstractDataWidget {

	private Grid element = new Grid( 1, 1);
	private TextBox textBox = new TextBox();
	
	public TextCreateWidget(FormElement d) {
		super(d);
		initWidget(element);
		
		element.setWidget(0, 0, textBox);
	}
	
	@Override
	public Plain getData() {
		String value = SafeHtmlUtils.htmlEscape(textBox.getText());
		return new Plain(value, "en");
	}

}
