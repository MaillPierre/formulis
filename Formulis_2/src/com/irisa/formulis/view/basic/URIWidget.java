package com.irisa.formulis.view.basic;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.ViewUtils;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.form.FormLineWidget;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.github.gwtbootstrap.client.ui.constants.Trigger;

public class URIWidget extends AbstractFormulisWidget {
	
	private Popover tooltip = new Popover();
	private InlineLabel element = new InlineLabel(getData().getLabel());

	public URIWidget(URI u, AbstractFormulisWidget par) {
		super(u, par);
		initWidget(element);
		
		tooltip.setHtml(true);
		tooltip.setHeading(u.getLabel());
		tooltip.setText(u.getUri());
		if(par instanceof FormLineWidget) {
			tooltip.setText(u.getUri() + " <br/> " + ((FormLineWidget) par).getFormLine().getInfo());
		}
		tooltip.setWidget(element);
		tooltip.setTrigger(Trigger.MANUAL);
		tooltip.setPlacement(Placement.BOTTOM);
		tooltip.setHideDelay(2000);
		tooltip.reconfigure();
		
		if(getData().getKind() == URI.KIND.CLASS) {
			element.setStyleName("class");
		}
		else if(getData().getKind() == URI.KIND.ENTITY) {
			element.setStyleName("entity");
		}
		else if(getData().getKind() == URI.KIND.PROPERTY) {
			element.setStyleName("property");
		}
		
		element.addClickHandler(this);

		element.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				tooltip.hide();
			}
		});
		element.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				tooltip.show();
			}
		});
		
		element.setWordWrap(false);
	}
	
	@Override
	public URI getData() {
		return (URI) super.getData();
	}
	
	@Override
	public String toString() {
		return "widget " + this.getData();
	}

	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}

	public void addTooltipLine(String line) {
		this.tooltip.setText(this.tooltip.getText() + ViewUtils.getHTMLBreakLineString() + line);
	}

}
