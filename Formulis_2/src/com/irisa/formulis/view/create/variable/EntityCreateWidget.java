package com.irisa.formulis.view.create.variable;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.basic.URI.KIND;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.view.AbstractDataWidget;

public class EntityCreateWidget extends AbstractDataWidget {

	private HorizontalPanel element = new HorizontalPanel();
	private TextBox labelBox = new TextBox();
	private TextBox uriBox = new TextBox();
	
	public EntityCreateWidget(FormElement d) {
		super(d);
		initWidget(element);
		
		labelBox.setPlaceholder("Entity name (label)");
		uriBox.setPlaceholder("[OPTIONAL] Entity URI");
		
		element.add(labelBox);
		element.add(uriBox);
	}
	
	@Override
	public URI getData() {
		String safeLabelText = SafeHtmlUtils.htmlEscape(labelBox.getText()).replace(" ", "").replace("\t", "").replace("\n", "");
		String safeUriText = safeLabelText;
		if(uriBox.getText() != "" ) {
			safeUriText = SafeHtmlUtils.htmlEscape(uriBox.getText()).replace(" ", "").replace("\t", "").replace("\n", "");
		}
		return new URI(safeUriText, KIND.ENTITY, safeLabelText);
	}

}
